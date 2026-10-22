package com.homecash

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.homecash.core.simulation.MockDataGenerator
import com.homecash.core.simulation.MockSmsSimulationService
import com.homecash.data.local.AppDatabase
import com.homecash.data.repository.HomeCashRepository
import com.homecash.data.remote.SupabaseAuthApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.UUID

class HomeCashApp : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { HomeCashRepository(database, this) }
    private val authApi by lazy { SupabaseAuthApi() }

    data class OAuthLogin(val userId: String, val email: String?, val eventId: String = UUID.randomUUID().toString())
    private val _oauthLogin = MutableStateFlow<OAuthLogin?>(null)
    val oauthLogin = _oauthLogin.asStateFlow()

    fun handleOAuthIntent(intent: Intent?) {
        val uri: Uri = intent?.data ?: return
        if (uri.scheme != "com.homecash" || uri.host != "auth" || uri.path != "/callback") return
        val code = uri.getQueryParameter("code")
        val errorDescription = uri.getQueryParameter("error_description")
        if (code.isNullOrBlank()) {
            Toast.makeText(this, errorDescription ?: "لم يكتمل تسجيل Google", Toast.LENGTH_LONG).show()
            return
        }
        applicationScope.launch {
            try {
                val result = authApi.exchangeGoogleOAuthCode(this@HomeCashApp, code)
                repository.setActiveUser(result.userId)
                _oauthLogin.value = OAuthLogin(result.userId, result.email)
            } catch (e: Exception) {
                Toast.makeText(this@HomeCashApp, e.message ?: "تعذر إكمال تسجيل Google", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        com.homecash.core.notifications.AppNotificationCenter.initialize(this)

        // Initialize provider configurations from JSON
        com.homecash.data.repository.ProviderManager.init(this)

        // Seed initial demo data for testing
        applicationScope.launch(Dispatchers.IO) {
            MockDataGenerator.seedDatabaseIfEmpty(database)
            
            // Start simulation if logged in
            if (repository.activeUserId().isNotBlank()) {
                MockSmsSimulationService.startSimulation(database, applicationScope)
            }
        }

        // Start background SMS monitoring service
        try {
            com.homecash.service.HomeCashService.start(this)
        } catch (_: Exception) {}
    }
}
