package hu.bme.student.auction.auction_platform.bidding

import hu.bme.student.auction.auction_platform.common.BaseExceptionHandler
import hu.bme.student.auction.auction_platform.common.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class BiddingExceptionHandler : BaseExceptionHandler() {

    @ExceptionHandler(AuctionNotActiveException::class)
    fun handleAuctionNotActive(
        ex: AuctionNotActiveException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        return buildResponse(
            status = HttpStatus.CONFLICT,
            message = ex.message ?: "Auction is not open for bidding",
            request = request
        )
    }

    @ExceptionHandler(InvalidBidException::class, SelfBiddingProhibitedException::class)
    fun handleInvalidBid(
        ex: RuntimeException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        return buildResponse(
            status = HttpStatus.BAD_REQUEST,
            message = ex.message ?: "Invalid bid submission",
            request = request
        )
    }
}