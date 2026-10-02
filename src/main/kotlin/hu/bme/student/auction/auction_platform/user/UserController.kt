package hu.bme.student.auction.auction_platform.user

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController {

    @GetMapping("/me")
    fun getCurrentUser(@AuthenticationPrincipal currentUser: User): ResponseEntity<UserProfileDto> {
        val profile = UserProfileDto(
            id = currentUser.id!!,
            email = currentUser.email,
            name = currentUser.name,
            role = currentUser.role,
            balance = currentUser.balance
        )
        return ResponseEntity.ok(profile)
    }
}