package hu.bme.student.auction.auction_platform.config

import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component

import hu.bme.student.auction.auction_platform.user.JwtService

@Component
class AuthChannelInterceptor(
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService
) : ChannelInterceptor {

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)

        if (accessor != null && StompCommand.CONNECT == accessor.command) {
            val authHeader = accessor.getFirstNativeHeader("Authorization")
                ?: accessor.getFirstNativeHeader("Auth-Token")

            if (!authHeader.isNullOrBlank() && authHeader.startsWith("Bearer ")) {
                val token = authHeader.substring(7)
                val username = jwtService.extractUsername(token)

                if (username != null) {
                    val userDetails = userDetailsService.loadUserByUsername(username)
                    if (jwtService.isTokenValid(token, userDetails)) {
                        val authentication = UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.authorities
                        )
                        accessor.user = authentication
                    }
                }
            }
        }

        return message
    }
}