package hu.bme.student.auction.auction_platform.user

import hu.bme.student.auction.auction_platform.common.BaseExceptionHandler
import hu.bme.student.auction.auction_platform.common.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
class AuthExceptionHandler : BaseExceptionHandler() {

    @ExceptionHandler(BadCredentialsException::class, UsernameNotFoundException::class)
    fun handleAuthenticationException(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        return buildResponse(
            status = HttpStatus.UNAUTHORIZED,
            message = "Invalid email or password",
            request = request
        )
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(
        ex: AccessDeniedException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        return buildResponse(
            status = HttpStatus.FORBIDDEN,
            message = "Access denied: insufficient permissions",
            request = request
        )
    }
}