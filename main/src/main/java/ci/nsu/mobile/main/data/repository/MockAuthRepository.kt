package ci.nsu.mobile.main.data.repository

import ci.nsu.mobile.main.data.model.*
import kotlinx.coroutines.delay

class MockAuthRepository : AuthRepository {
    override suspend fun login(login: String, password: String): Result<UserDto> {
        delay(500) // Имитируем загрузку
        return Result.success(
            UserDto(1, login, "test@nsu.ru",
                PersonDto("Иван", "Иванов", "Иванович", "2000-01-01", "MALE", 1))
        )
    }

    override suspend fun register(request: RegisterRequest): Result<Unit> {
        delay(500)
        return Result.success(Unit)
    }

    override suspend fun getUsers(): Result<List<UserDto>> {
        return Result.success(emptyList())
    }

    override suspend fun getGroups(): Result<List<GroupDto>> {
        // Вот твои группы, которые появятся в приложении!
        return Result.success(listOf(
            GroupDto(1, "ИВТ-21"),
            GroupDto(2, "ПМИ-22"),
            GroupDto(3, "ФИТ-23"),
            GroupDto(4, "Матфак-24")
        ))
    }
}