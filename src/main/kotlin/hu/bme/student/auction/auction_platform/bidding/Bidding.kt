package hu.bme.student.auction.auction_platform.bidding

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class PlaceBidRequest(
    @field:NotNull(message = "Bid amount is mandatory")
    @field:DecimalMin(value = "0.0001", message = "Bid amount must be positive")
    @field:Digits(integer = 15, fraction = 4, message = "Bid amount precision cannot exceed 19 digits with 4 decimals")
    val bidAmount: BigDecimal
)

data class BidResponse(
    val id: UUID,
    val auctionId: UUID,
    val bidderId: UUID,
    val bidderEmail: String,
    val bidAmount: BigDecimal,
    val createdAt: Instant
) {
    companion object {
        fun fromEntity(bid: Bid): BidResponse = BidResponse(
            id = bid.id!!,
            auctionId = bid.auction.id!!,
            bidderId = bid.bidder.id!!,
            bidderEmail = bid.bidder.email,
            bidAmount = bid.bidAmount,
            createdAt = bid.createdAt
        )
    }
}

data class BidBroadcastPayload(
    val auctionId: UUID,
    val newHighestBid: BigDecimal,
    val highestBidderId: UUID,
    val bidId: UUID,
    val timestamp: Instant
)

class AuctionNotActiveException(message: String) : RuntimeException(message)
class InvalidBidException(message: String) : RuntimeException(message)
class SelfBiddingProhibitedException(message: String) : RuntimeException(message)