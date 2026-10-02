package hu.bme.student.auction.auction_platform.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    var secret: String = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
    var expirationMs: Long = 86400000L
)