package hu.bme.student.auction.auction_platform.common

import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
class GlobalExceptionHandler : BaseExceptionHandler() {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val fieldErrors = ex.bindingResult.allErrors.associate { error ->
            val fieldName = (error as? FieldError)?.field ?: error.objectName
            val message = error.defaultMessage ?: "Validation failed"
            fieldName to message
        }

        return buildResponse(
            status = HttpStatus.BAD_REQUEST,
            message = "Validation failed for one or more fields",
            request = request,
            validationErrors = fieldErrors
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(
        ex: IllegalArgumentException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        return buildResponse(
            status = HttpStatus.BAD_REQUEST,
            message = ex.message ?: "Invalid request argument",
            request = request
        )
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFound(
        ex: ResourceNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        return buildResponse(
            status = HttpStatus.NOT_FOUND,
            message = ex.message ?: "Resource not found",
            request = request
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        return buildResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            message = ex.message ?: "An unexpected error occurred",
            request = request
        )
    }
}