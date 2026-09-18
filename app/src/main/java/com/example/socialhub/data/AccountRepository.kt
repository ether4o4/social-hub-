package com.example.socialhub.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import org.json.JSONObject

data class Account(
    val platformId: String,
    val username: String,
    val connected: Boolean
)

/**
 * Stores per-platform account credentials encrypted at rest via EncryptedSharedPreferences.
 * Real OAuth per platform requires each platform's developer keys; this store keeps the
 * user's saved username/password per app so the hub remembers accounts and connection state.
 */
class AccountRepository(context: Context) {

    private val prefs by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            "social_hub_accounts",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun all(): Map<String, Account> {
        val map = mutableMapOf<String, Account>()
        for (platform in platforms) {
            if (platform.id == "all") continue
            prefs.getString(KEY_ACCOUNT + platform.id, null)?.let { json ->
                val obj = JSONObject(json)
                map[platform.id] = Account(
                    platformId = platform.id,
                    username = obj.optString("username"),
                    connected = obj.optBoolean("connected", false)
                )
            }
        }
        return map
    }

    fun get(platformId: String): Account? = all()[platformId]

    fun isConnected(platformId: String): Boolean =
        prefs.getBoolean(KEY_CONNECTED + platformId, false)

    fun connect(platformId: String, username: String, password: String) {
        val obj = JSONObject()
        obj.put("username", username)
        obj.put("connected", true)
        prefs.edit()
            .putString(KEY_ACCOUNT + platformId, obj.toString())
            .putString(KEY_CRED + platformId, password)
            .putBoolean(KEY_CONNECTED + platformId, true)
            .apply()
    }

    fun disconnect(platformId: String) {
        prefs.edit()
            .remove(KEY_ACCOUNT + platformId)
            .remove(KEY_CRED + platformId)
            .remove(KEY_CONNECTED + platformId)
            .apply()
    }

    private companion object {
        const val KEY_ACCOUNT = "account_"
        const val KEY_CRED = "cred_"
        const val KEY_CONNECTED = "connected_"
    }
}
