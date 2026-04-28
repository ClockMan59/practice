package ci.nsu.mobile.main.data.network

import ci.nsu.mobile.main.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest
            .newBuilder()
            .addHeader("Content-Type", "application/json")

        val path = originalRequest.url.encodedPath
        val shouldAttachToken = when {
            path.endsWith("/auth/login") -> false
            path.endsWith("/auth/register") -> false
            path.endsWith("/groups") -> false
            else -> true
        }

        if (shouldAttachToken) {
            sessionManager.token?.let { token ->
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
        }

        return chain.proceed(requestBuilder.build())
    }
}
