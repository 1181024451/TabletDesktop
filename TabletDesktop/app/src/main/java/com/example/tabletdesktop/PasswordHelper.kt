package com.example.tabletdesktop
import android.content.Context
import android.content.SharedPreferences
import java.util.Base64
class PasswordHelper(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("desktop_prefs", Context.MODE_PRIVATE)
    fun setPassword(password: String) {
        prefs.edit().putString("switch_password", encrypt(password)).apply()
        prefs.edit().putBoolean("password_enabled", true).apply()
    }
    fun verifyPassword(input: String): Boolean {
        if (!prefs.getBoolean("password_enabled", false)) return true
        val stored = prefs.getString("switch_password", null) ?: return true
        return decrypt(stored) == input
    }
    fun isPasswordEnabled(): Boolean = prefs.getBoolean("password_enabled", false)
    fun clearPassword() {
        prefs.edit().remove("switch_password").apply()
        prefs.edit().putBoolean("password_enabled", false).apply()
    }
    fun hasPassword(): Boolean = prefs.getString("switch_password", null) != null
    private fun encrypt(text: String): String {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            Base64.getEncoder().encodeToString(text.toByteArray())
        else
            android.util.Base64.encodeToString(text.toByteArray(), android.util.Base64.DEFAULT)
    }
    private fun decrypt(encoded: String): String {
        val bytes = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            Base64.getDecoder().decode(encoded)
        else
            android.util.Base64.decode(encoded, android.util.Base64.DEFAULT)
        return String(bytes)
    }
}
