package hu.bme.student.auction.auction_platform.common

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class ResourceNotFoundException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)