package com.homecash.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homecash.core.simulation.MockSmsSimulationService
import com.homecash.data.local.AppDatabase
import com.homecash.data.remote.SupabaseAuthApi
import com.homecash.data.repository.HomeCashRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: HomeCashRepository,
    private val db: AppDatabase,
    private val authApi: SupabaseAuthApi = SupabaseAuthApi()
) : ViewModel() {
    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()
    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val _loginError = MutableStateFlow<String?>(null)
    val loginError = _loginError.asStateFlow()
    private val _notice = MutableStateFlow<String?>(null)
    val notice = _notice.asStateFlow()

    fun onUsernameChange(newVal: String) { _username.value = newVal; _loginError.value = null }
    fun onPasswordChange(newVal: String) { _password.value = newVal; _loginError.value = null }
    fun clearNotice() { _notice.value = null }

    fun startGoogleSignIn(context: Context) {
        _loginError.value = null
        try {
            authApi.startGoogleOAuth(context)
        } catch (e: Exception) {
            _loginError.value = e.message ?: "تعذر بدء تسجيل الدخول بواسطة Google"
        }
    }

    fun login(onSuccess: (isFirstTime: Boolean) -> Unit) {
        if (_isLoading.value) return
        val login = _username.value.trim()
        val secret = _password.value
        if (login.isBlank() || secret.isBlank()) { _loginError.value = "أدخل البريد الإلكتروني وكلمة المرور"; return }
        _isLoading.value = true
        _loginError.value = null
        viewModelScope.launch {
            try {
                if (login.equals("admin", ignoreCase = true) && secret == "admin") {
                    repository.setActiveUser("admin")
                    MockSmsSimulationService.startSimulation(db, viewModelScope)
                    onSuccess(true)
                } else {
                    MockSmsSimulationService.stopSimulation()
                    if (!login.contains('@')) throw IllegalArgumentException("استخدم البريد الإلكتروني للحساب الحقيقي")
                    val result = authApi.signIn(login, secret)
                    repository.setActiveUser(result.userId)
                    val existing = db.userDao().getUserSync(result.userId)
                    // A new local profile is created only after the real setup wizard is completed.
                    onSuccess(existing == null)
                }
            } catch (e: Exception) {
                _loginError.value = e.message ?: "تعذر تسجيل الدخول. حاول مرة أخرى."
            } finally { _isLoading.value = false }
        }
    }

    fun register() {
        val email = _username.value.trim(); val secret = _password.value
        if (!email.contains('@')) { _loginError.value = "أدخل بريدًا إلكترونيًا صحيحًا"; return }
        if (secret.length < 8) { _loginError.value = "كلمة المرور يجب ألا تقل عن 8 أحرف"; return }
        if (_isLoading.value) return
        _isLoading.value = true; _loginError.value = null
        viewModelScope.launch {
            try {
                authApi.signUp(email, secret)
                _notice.value = "تم إرسال طلب إنشاء الحساب. إذا كان تأكيد البريد مفعلاً في Supabase، افتح رسالة التأكيد ثم سجّل الدخول."
            } catch (e: Exception) { _loginError.value = e.message ?: "تعذر إنشاء الحساب" }
            finally { _isLoading.value = false }
        }
    }

    fun forgotPassword() {
        val email = _username.value.trim()
        if (!email.contains('@')) { _loginError.value = "اكتب بريدك الإلكتروني أولاً لاستعادة كلمة المرور"; return }
        if (_isLoading.value) return
        _isLoading.value = true; _loginError.value = null
        viewModelScope.launch {
            try { authApi.requestPasswordReset(email); _notice.value = "إذا كان البريد مسجلاً، ستصلك رسالة استعادة كلمة المرور." }
            catch (e: Exception) { _loginError.value = e.message ?: "تعذر إرسال رسالة الاستعادة" }
            finally { _isLoading.value = false }
        }
    }
}
