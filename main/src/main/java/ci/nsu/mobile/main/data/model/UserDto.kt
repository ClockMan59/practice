package ci.nsu.mobile.main.data.model

data class UserDto(
    val id: Int,
    val login: String,
    val email: String,
    val person: PersonDto
) {
    fun fullName(): String {
        return listOf(person.lastName, person.firstName, person.middleName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
    }
}
