package hu.bme.student.auction.auction_platform.user

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.UUID

data class RegisterRequest(
    @field:NotBlank(message = "Name must not be blank")
    @field:Size(min = 2, max = 32, message = "Name must be between 2 and 32 characters")
    val name: String,

    @field:NotBlank(message = "Email must not be blank")
    @field:Email(message = "Email must be valid")
    val email: String,

    @field:NotBlank(message = "Password must not be blank")
    @field:Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
    val password: String
)

data class LoginRequest(
    @field:NotBlank(message = "Email must not be blank")
    @field:Email(message = "Email must be valid")
    val email: String,

    @field:NotBlank(message = "Password must not be blank")
    val password: String
)

data class AuthResponse(
    val token: String,
    val userId: UUID,
    val email: String,
    val name: String,
    val role: Role
)