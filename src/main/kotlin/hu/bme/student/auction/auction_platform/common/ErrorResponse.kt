package hu.bme.student.auction.auction_platform.common

import java.time.Instant

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val path: String? = null,
    val timestamp: Instant = Instant.now(),
    val validationErrors: Map<String, String>? = null
)