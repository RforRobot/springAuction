package hu.bme.student.auction.auction_platform.user

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) {

    @Transactional
    fun register(request: RegisterRequest): AuthResponse {
        val normalizedEmail = request.email.trim().lowercase()

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw IllegalArgumentException("Email is already registered: $normalizedEmail")
        }

        val user = User(
            email = normalizedEmail,
            passwordHash = passwordEncoder.encode(request.password)!!,
            name = request.name.trim(),
            role = Role.ROLE_USER
        )

        val savedUser = userRepository.save(user)
        val token = jwtService.generateToken(savedUser)

        return AuthResponse(
            token = token,
            userId = savedUser.id!!,
            email = savedUser.email,
            name = savedUser.name,
            role = savedUser.role
        )
    }

    @Transactional(readOnly = true)
    fun login(request: LoginRequest): AuthResponse {
        val normalizedEmail = request.email.trim().lowercase()

        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(normalizedEmail, request.password)
        )

        val user = userRepository.findByEmail(normalizedEmail)
            ?: throw IllegalStateException("Authenticated user not found in database")

        val token = jwtService.generateToken(user)

        return AuthResponse(
            token = token,
            userId = user.id!!,
            email = user.email,
            name = user.name,
            role = user.role
        )
    }
}