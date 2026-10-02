package hu.bme.student.auction.auction_platform.user

import java.math.BigDecimal
import java.util.UUID

data class UserProfileDto(
    val id: UUID,
    val email: String,
    val name: String,
    val role: Role,
    val balance: BigDecimal
)