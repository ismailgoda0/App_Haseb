package com.homecash.data.remote

import android.util.Log
import com.homecash.BuildConfig
import com.homecash.data.local.entity.DeviceEntity
import com.homecash.data.local.entity.TransactionEntity
import com.homecash.data.local.entity.WalletEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.time.Instant
import java.util.concurrent.TimeUnit

object CloudSyncApi {
    private const val TAG = "CloudSyncApi"
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()
    private val mediaType = "application/json; charset=utf-8".toMediaType()

    val isConfigured: Boolean
        get() = BuildConfig.SUPABASE_URL.startsWith("https://") && BuildConfig.SUPABASE_ANON_KEY.isNotBlank()

    suspend fun syncTransaction(userId: String, transaction: TransactionEntity): Boolean = withContext(Dispatchers.IO) {
        if (!isConfigured || userId.isBlank()) {
            Log.i(TAG, "Cloud sync skipped: Supabase configuration missing or offline mode active.")
            return@withContext false
        }
        try {
            val json = JSONObject().apply {
                put("id", transaction.id)
                put("user_id", userId)
                put("wallet_id", transaction.walletId)
                put("device_id", transaction.deviceId)
                put("fingerprint", transaction.fingerprint)
                put("direction", transaction.direction)
                put("amount", transaction.amount)
                put("fee", transaction.fee)
                put("cashier_commission", transaction.cashierCommission)
                put("balance_after", transaction.balanceAfter)
                put("counterparty_number", transaction.counterpartyNumber)
                put("counterparty_name", transaction.counterpartyName)
                put("operation_code", transaction.operationCode)
                put("category", transaction.category)
                put("notes", transaction.note)
                put("raw_text", transaction.rawText)
                put("occurred_at", Instant.ofEpochMilli(transaction.occurredAt).toString())
                put("status", transaction.status)
                put("is_auto_categorized", transaction.isAutoCategorized)
                put("note", transaction.note)
                put("is_manual", transaction.isManual)
                put("is_synced", true)
            }

            val request = Request.Builder()
                .url(BuildConfig.SUPABASE_URL.trimEnd('/') + "/rest/v1/transactions")
                .post(json.toString().toRequestBody(mediaType))
                .header("apikey", BuildConfig.SUPABASE_ANON_KEY)
                .header("Authorization", "Bearer ${BuildConfig.SUPABASE_ANON_KEY}")
                .header("Prefer", "resolution=merge-duplicates")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    Log.d(TAG, "Successfully synced transaction: ${transaction.id}")
                    true
                } else {
                    Log.w(TAG, "Failed to sync transaction ${transaction.id}: HTTP ${response.code}")
                    false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing transaction ${transaction.id}", e)
            false
        }
    }

    suspend fun syncDeviceHeartbeat(userId: String, device: DeviceEntity): Boolean = withContext(Dispatchers.IO) {
        if (!isConfigured || userId.isBlank()) return@withContext false
        try {
            val json = JSONObject().apply {
                put("id", device.id)
                put("user_id", userId)
                put("device_name", device.deviceName)
                put("status", device.status)
                put("last_sync_at", Instant.ofEpochMilli(device.lastSyncAt).toString())
            }

            val request = Request.Builder()
                .url(BuildConfig.SUPABASE_URL.trimEnd('/') + "/rest/v1/devices")
                .post(json.toString().toRequestBody(mediaType))
                .header("apikey", BuildConfig.SUPABASE_ANON_KEY)
                .header("Authorization", "Bearer ${BuildConfig.SUPABASE_ANON_KEY}")
                .header("Prefer", "resolution=merge-duplicates")
                .build()

            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing device heartbeat", e)
            false
        }
    }

    suspend fun syncWalletBalance(userId: String, wallet: WalletEntity): Boolean = withContext(Dispatchers.IO) {
        if (!isConfigured || userId.isBlank()) return@withContext false
        try {
            val json = JSONObject().apply {
                put("id", wallet.id)
                put("user_id", userId)
                put("provider_name", wallet.providerName)
                put("current_balance", wallet.currentBalance)
                put("initial_balance", wallet.initialBalance)
                put("send_fee_pct", wallet.sendFeePct)
                put("send_fee_fixed", wallet.sendFeeFixed)
                put("receive_fee_pct", wallet.receiveFeePct)
                put("receive_fee_fixed", wallet.receiveFeeFixed)
                put("color_hex", wallet.colorHex)
                put("is_active", wallet.isActive)
                put("last_updated", Instant.ofEpochMilli(wallet.lastUpdated).toString())
            }

            val request = Request.Builder()
                .url(BuildConfig.SUPABASE_URL.trimEnd('/') + "/rest/v1/wallets")
                .post(json.toString().toRequestBody(mediaType))
                .header("apikey", BuildConfig.SUPABASE_ANON_KEY)
                .header("Authorization", "Bearer ${BuildConfig.SUPABASE_ANON_KEY}")
                .header("Prefer", "resolution=merge-duplicates")
                .build()

            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing wallet balance", e)
            false
        }
    }
}
