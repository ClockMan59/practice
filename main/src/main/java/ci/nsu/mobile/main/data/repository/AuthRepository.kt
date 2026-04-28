package ci.nsu.mobile.main.data.repository

import ci.nsu.mobile.main.data.model.GroupDto
import ci.nsu.mobile.main.data.model.RegisterRequest
import ci.nsu.mobile.main.data.model.UserDto

interface AuthRepository {
    suspend fun login(login: String, password: String): Result<UserDto>
    suspend fun register(request: RegisterRequest): Result<Unit>
    suspend fun getUsers(): Result<List<UserDto>>
    suspend fun getGroups(): Result<List<GroupDto>>
}
