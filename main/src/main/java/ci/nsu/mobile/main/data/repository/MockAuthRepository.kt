package ci.nsu.mobile.main.data.repository

import ci.nsu.mobile.main.data.local.SessionManager
import ci.nsu.mobile.main.data.model.GroupDto
import ci.nsu.mobile.main.data.model.PersonDto
import ci.nsu.mobile.main.data.model.RegisterRequest
import ci.nsu.mobile.main.data.model.UserDto
import kotlinx.coroutines.delay

class MockAuthRepository(
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun login(login: String, password: String): Result<UserDto> {
        delay(500)
        val user = UserDto(
            id = 1,
            login = login,
            email = "test@nsu.ru",
            person = PersonDto(
                firstName = "Иван",
                lastName = "Иванов",
                middleName = "Иванович",
                birthDate = "2000-01-01",
                gender = "MALE",
                groupId = 1
            )
        )

        sessionManager.saveSession("mock-token", user)
        return Result.success(user)
    }

    override suspend fun register(request: RegisterRequest): Result<Unit> {
        delay(500)
        return Result.success(Unit)
    }

    override suspend fun getUsers(): Result<List<UserDto>> {
        return Result.success(
            listOf(
                UserDto(
                    id = 1,
                    login = "test",
                    email = "test@nsu.ru",
                    person = PersonDto(
                        firstName = "Иван",
                        lastName = "Иванов",
                        middleName = "Иванович",
                        birthDate = "2000-01-01",
                        gender = "MALE",
                        groupId = 1
                    )
                )
            )
        )
    }

    override suspend fun getGroups(): Result<List<GroupDto>> {
        return Result.success(
            listOf(
                GroupDto(1, "ИВТ-21"),
                GroupDto(2, "ПМИ-22"),
                GroupDto(3, "ФИТ-23"),
                GroupDto(4, "Матфак-24")
            )
        )
    }
}
