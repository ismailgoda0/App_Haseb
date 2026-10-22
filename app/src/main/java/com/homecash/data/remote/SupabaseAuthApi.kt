package com.homecash.data.remote

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Base64
import com.homecash.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.concurrent.TimeUnit

/** Supabase Auth REST client. Uses only the project's public anon key; authorization is enforced by Supabase. */
class SupabaseAuthApi {
    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(12, TimeUnit.SECONDS)
        .callTimeout(20, TimeUnit.SECONDS)
        .build()
    private val json = Json { ignoreUnknownKeys = true }
    private val mediaType = "application/json; charset=utf-8".toMediaType()

    data class AuthResult(val userId: String, val email: String?)

    /** Starts a Supabase-hosted Google OAuth flow using PKCE and an app deep-link callback. */
    fun startGoogleOAuth(context: Context) {
        requireConfigured()
        val random = ByteArray(32).also { SecureRandom().nextBytes(it) }
        val verifier = Base64.encodeToString(random, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
        val challenge = Base64.encodeToString(
            MessageDigest.getInstance("SHA-256").digest(verifier.toByteArray(Charsets.US_ASCII)),
            Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
        )
        context.getSharedPreferences(OAUTH_PREFS, Context.MODE_PRIVATE).edit().putString(PKCE_VERIFIER, verifier).apply()
        val redirect = "com.homecash://auth/callback"
        val url = BuildConfig.SUPABASE_URL.trimEnd('/') + "/auth/v1/authorize" +
            "?provider=google" +
            "&redirect_to=" + java.net.URLEncoder.encode(redirect, "UTF-8") +
            "&code_challenge=" + java.net.URLEncoder.encode(challenge, "UTF-8") +
            "&code_challenge_method=s256"
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /** Exchanges the one-time OAuth code; the verifier is removed even if exchange fails. */
    suspend fun exchangeGoogleOAuthCode(context: Context, code: String): AuthResult = withContext(Dispatchers.IO) {
        requireConfigured()
        val prefs = context.getSharedPreferences(OAUTH_PREFS, Context.MODE_PRIVATE)
        val verifier = prefs.getString(PKCE_VERIFIER, null) ?: throw IOException("انتهت محاولة تسجيل Google. حاول مرة أخرى.")
        try {
            val payload = "{\"auth_code\":${quote(code)},\"code_verifier\":${quote(verifier)}}"
            val request = Request.Builder()
                .url(BuildConfig.SUPABASE_URL.trimEnd('/') + "/auth/v1/token?grant_type=pkce")
                .post(payload.toRequestBody(mediaType))
                .header("apikey", BuildConfig.SUPABASE_ANON_KEY)
                .header("Content-Type", "application/json")
                .build()
            client.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (!response.isSuccessful) throw IOException(extractError(body, response.code))
                val root = runCatching { json.parseToJsonElement(body).jsonObject }.getOrElse { throw IOException("استجابة OAuth غير مفهومة") }
                val user = root["user"]?.jsonObject ?: throw IOException("لم تُرجع خدمة Google هوية المستخدم")
                val id = user["id"]?.jsonPrimitive?.content
                if (id.isNullOrBlank()) throw IOException("تعذر تحديد هوية حساب Google")
                AuthResult(id, user["email"]?.jsonPrimitive?.content)
            }
        } finally {
            prefs.edit().remove(PKCE_VERIFIER).apply()
        }
    }

    companion object {
        private const val OAUTH_PREFS = "homecash_oauth_temporary"
        private const val PKCE_VERIFIER = "pkce_verifier"
    }

    val isConfigured: Boolean
        get() = BuildConfig.SUPABASE_URL.startsWith("https://") && BuildConfig.SUPABASE_ANON_KEY.isNotBlank()

    suspend fun signIn(email: String, password: String): AuthResult = requestAuth(
        path = "token?grant_type=password",
        payload = "{\"email\":${quote(email.trim())},\"password\":${quote(password)} }",
        requireUser = true
    )

    suspend fun signUp(email: String, password: String): AuthResult = requestAuth(
        path = "signup",
        payload = "{\"email\":${quote(email.trim())},\"password\":${quote(password)} }",
        requireUser = false
    )

    suspend fun requestPasswordReset(email: String) = withContext(Dispatchers.IO) {
        requireConfigured()
        val url = BuildConfig.SUPABASE_URL.trimEnd('/') + "/auth/v1/recover?email=" + java.net.URLEncoder.encode(email.trim(), "UTF-8")
        val request = Request.Builder().url(url).post("{}".toRequestBody(mediaType)).header("apikey", BuildConfig.SUPABASE_ANON_KEY).header("Content-Type", "application/json").build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException(extractError(response.body?.string(), response.code))
        }
    }

    private suspend fun requestAuth(path: String, payload: String, requireUser: Boolean): AuthResult = withContext(Dispatchers.IO) {
        requireConfigured()
        val request = Request.Builder()
            .url(BuildConfig.SUPABASE_URL.trimEnd('/') + "/auth/v1/" + path)
            .post(payload.toRequestBody(mediaType))
            .header("apikey", BuildConfig.SUPABASE_ANON_KEY)
            .header("Content-Type", "application/json")
            .build()
        client.newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) throw IOException(extractError(body, response.code))
            val root = runCatching { json.parseToJsonElement(body).jsonObject }.getOrElse { throw IOException("استجابة غير مفهومة من خدمة تسجيل الدخول") }
            val user = root["user"]?.jsonObject ?: if (!requireUser) root else null
            val id = user?.get("id")?.jsonPrimitive?.content
            if (id.isNullOrBlank()) {
                if (!requireUser) throw IOException("تم إنشاء الحساب. راجع بريدك لتأكيد الحساب قبل تسجيل الدخول.")
                throw IOException("لم ترجع خدمة تسجيل الدخول هوية المستخدم")
            }
            AuthResult(id, user["email"]?.jsonPrimitive?.content)
        }
    }

    private fun requireConfigured() {
        if (!isConfigured) throw IOException("لم يتم إعداد Supabase بعد. أضف HOME_CASH_SUPABASE_URL و HOME_CASH_SUPABASE_ANON_KEY إلى gradle.properties.")
    }

    private fun extractError(body: String?, status: Int): String {
        val message = runCatching {
            val obj = json.parseToJsonElement(body.orEmpty()).jsonObject
            (obj["msg"] ?: obj["message"] ?: obj["error_description"] ?: obj["error"])?.jsonPrimitive?.content
        }.getOrNull()
        return when {
            status == 429 -> "طلبات كثيرة جداً. انتظر قليلاً ثم حاول مرة أخرى."
            !message.isNullOrBlank() -> message.take(180)
            else -> "تعذر إكمال الطلب (HTTP $status). تحقق من الإعدادات والاتصال."
        }
    }

    private fun quote(value: String): String = kotlinx.serialization.json.JsonPrimitive(value).toString()
}
