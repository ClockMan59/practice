package ci.nsu.mobile.main.data.network

import android.os.Build
import ci.nsu.mobile.main.BuildConfig
import ci.nsu.mobile.main.data.local.SessionManager
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppClientFactory {

    fun create(
        sessionManager: SessionManager,
        baseUrl: String = resolvePreferredBaseUrl()
    ): AppService {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sessionManager))
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(AppService::class.java)
    }

    fun createAlternate(sessionManager: SessionManager): AppService {
        return create(sessionManager, resolveAlternateBaseUrl())
    }

    private fun resolvePreferredBaseUrl(): String {
        return if (isProbablyEmulator()) {
            BuildConfig.EMULATOR_API_BASE_URL
        } else {
            BuildConfig.API_BASE_URL
        }
    }

    private fun resolveAlternateBaseUrl(): String {
        return if (isProbablyEmulator()) {
            BuildConfig.API_BASE_URL
        } else {
            BuildConfig.EMULATOR_API_BASE_URL
        }
    }

    private fun isProbablyEmulator(): Boolean {
        return Build.FINGERPRINT.contains("generic", ignoreCase = true) ||
            Build.MODEL.contains("Emulator", ignoreCase = true) ||
            Build.MODEL.contains("Android SDK built for", ignoreCase = true) ||
            Build.MANUFACTURER.contains("Genymotion", ignoreCase = true) ||
            Build.PRODUCT.contains("sdk", ignoreCase = true) ||
            Build.HARDWARE.contains("ranchu", ignoreCase = true)
    }
}
