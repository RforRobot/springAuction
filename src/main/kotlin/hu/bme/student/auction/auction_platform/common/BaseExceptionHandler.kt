package hu.bme.student.auction.auction_platform.common

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

abstract class BaseExceptionHandler {

    protected fun buildResponse(
        status: HttpStatus,
        message: String,
        request: HttpServletRequest,
        validationErrors: Map<String, String>? = null
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = status.value(),
            error = status.reasonPhrase,
            message = message,
            path = request.requestURI,
            validationErrors = validationErrors
        )
        return ResponseEntity.status(status).body(errorResponse)
    }
}