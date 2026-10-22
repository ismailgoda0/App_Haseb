package com.homecash.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.homecash.HomeCashApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "HomeCashSmsReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val pendingResult = goAsync()
        try {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            if (messages.isNullOrEmpty()) {
                pendingResult.finish()
                return
            }

            val sender = messages[0].displayOriginatingAddress ?: ""
            val fullBody = messages.joinToString(separator = "") { it.displayMessageBody ?: "" }

            Log.d(TAG, "Received SMS from: $sender")

            val app = context.applicationContext as? HomeCashApp
            if (app == null) {
                pendingResult.finish()
                return
            }
            val repository = app.repository

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    repository.processIncomingSms(sender, fullBody)
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing SMS in repository", e)
                } finally {
                    pendingResult.finish()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling incoming SMS", e)
            pendingResult.finish()
        }
    }
}
