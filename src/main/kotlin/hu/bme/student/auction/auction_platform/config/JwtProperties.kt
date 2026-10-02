package hu.bme.student.auction.auction_platform.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    var secret: String = "",
    var expirationMs: Long = 86400000L
)