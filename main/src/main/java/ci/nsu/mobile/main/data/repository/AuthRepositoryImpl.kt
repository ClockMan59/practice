package ci.nsu.mobile.main.data.repository

import ci.nsu.mobile.main.data.local.SessionManager
import ci.nsu.mobile.main.data.model.GroupDto
import ci.nsu.mobile.main.data.model.LoginRequest
import ci.nsu.mobile.main.data.model.RegisterRequest
import ci.nsu.mobile.main.data.model.UserDto
import ci.nsu.mobile.main.data.network.AppService
import retrofit2.Response

class AuthRepositoryImpl(
    private val primaryApiService: AppService,
    private val secondaryApiService: AppService,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun login(login: String, password: String): Result<UserDto> {
        return try {
            val response = requestWithFallback { service ->
                service.login(LoginRequest(login, password))
            }
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                sessionManager.saveSession(body.token, body.user)
                Result.success(body.user)
            } else {
                Result.failure(Exception(response.errorMessage("Не удалось выполнить вход")))
            }
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override suspend fun register(request: RegisterRequest): Result<Unit> {
        return try {
            val response = requestWithFallback { service ->
                service.register(request)
            }
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.errorMessage("Не удалось зарегистрироваться")))
            }
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override suspend fun getUsers(): Result<List<UserDto>> {
        return try {
            val response = requestWithFallback { service ->
                service.getUsers()
            }
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception(response.errorMessage("Не удалось загрузить список пользователей"))
                )
            }
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override suspend fun getGroups(): Result<List<GroupDto>> {
        return try {
            val response = requestWithFallback { service ->
                service.getGroups()
            }
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorMessage("Не удалось загрузить список групп")))
            }
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private suspend fun <T> requestWithFallback(
        request: suspend (AppService) -> Response<T>
    ): Response<T> {
        return try {
            request(primaryApiService)
        } catch (primaryException: Exception) {
            request(secondaryApiService)
        }
    }
}

private fun Response<*>.errorMessage(defaultMessage: String): String {
    val responseBody = errorBody()?.string()?.trim().orEmpty()
    return buildString {
        append(defaultMessage)
        append(" (HTTP ")
        append(code())
        append(")")
        if (responseBody.isNotEmpty()) {
            append(": ")
            append(responseBody)
        }
    }
}
