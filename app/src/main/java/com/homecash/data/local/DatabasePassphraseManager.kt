package com.homecash.data.local

import android.content.Context
import android.util.Base64
import java.security.SecureRandom

object DatabasePassphraseManager {
    private const val PREF_NAME = "homecash_db_sec_prefs"
    private const val KEY_PASSPHRASE = "db_passphrase_key"

    fun getPassphrase(context: Context): ByteArray {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val existing = prefs.getString(KEY_PASSPHRASE, null)
        if (existing != null) {
            return Base64.decode(existing, Base64.DEFAULT)
        }
        val randomBytes = ByteArray(32)
        SecureRandom().nextBytes(randomBytes)
        val encoded = Base64.encodeToString(randomBytes, Base64.DEFAULT)
        prefs.edit().putString(KEY_PASSPHRASE, encoded).apply()
        return randomBytes
    }
}
