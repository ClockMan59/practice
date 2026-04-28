package ci.nsu.mobile.main.data.local

import android.content.Context
import android.content.SharedPreferences
import ci.nsu.mobile.main.data.model.UserDto

class SessionManager(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    val token: String?
        get() = preferences.getString(KEY_TOKEN, null)

    val userId: Long?
        get() = if (preferences.contains(KEY_USER_ID)) {
            preferences.getLong(KEY_USER_ID, -1L).takeIf { it >= 0L }
        } else {
            null
        }

    val userLogin: String?
        get() = preferences.getString(KEY_USER_LOGIN, null)

    val userDisplayName: String?
        get() = preferences.getString(KEY_USER_DISPLAY_NAME, null)

    fun isLoggedIn(): Boolean = !token.isNullOrBlank() && userId != null

    fun saveSession(token: String, user: UserDto) {
        preferences.edit()
            .putString(KEY_TOKEN, token)
            .putLong(KEY_USER_ID, user.id.toLong())
            .putString(KEY_USER_LOGIN, user.login)
            .putString(KEY_USER_DISPLAY_NAME, user.fullName())
            .apply()
    }

    fun clearSession() {
        preferences.edit()
            .remove(KEY_TOKEN)
            .remove(KEY_USER_ID)
            .remove(KEY_USER_LOGIN)
            .remove(KEY_USER_DISPLAY_NAME)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "deposit_auth_prefs"
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_LOGIN = "user_login"
        private const val KEY_USER_DISPLAY_NAME = "user_display_name"
    }
}
