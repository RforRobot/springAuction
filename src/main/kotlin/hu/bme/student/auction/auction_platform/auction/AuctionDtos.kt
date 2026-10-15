package hu.bme.student.auction.auction_platform.auction

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class CreateAuctionRequest(
    @field:NotBlank(message = "Title must not be blank")
    @field:Size(min = 3, max = 150, message = "Title must be between 3 and 150 characters")
    val title: String,

    @field:NotBlank(message = "Description must not be blank")
    val description: String,

    @field:NotNull(message = "Starting price is required")
    @field:DecimalMin(value = "0.01", message = "Starting price must be at least 0.01")
    val startingPrice: BigDecimal,

    @field:DecimalMin(value = "0.01", message = "Reserve price must be at least 0.01")
    val reservePrice: BigDecimal? = null,

    @field:NotNull(message = "End time is required")
    @field:Future(message = "End time must be in the future")
    val endTime: Instant
)

data class AuctionResponse(
    val id: UUID,
    val title: String,
    val description: String,
    val startingPrice: BigDecimal,
    val reservePrice: BigDecimal?,
    val currentHighestBid: BigDecimal?,
    val status: AuctionStatus,
    val startTime: Instant,
    val endTime: Instant,
    val sellerId: UUID,
    val sellerEmail: String
) {
    companion object {
        fun fromEntity(auction: Auction): AuctionResponse = AuctionResponse(
            id = auction.id ?: throw IllegalStateException("Auction ID cannot be null when mapping to response"),
            title = auction.title,
            description = auction.description,
            startingPrice = auction.startingPrice,
            reservePrice = auction.reservePrice,
            currentHighestBid = auction.currentHighestBid,
            status = auction.status,
            startTime = auction.startTime,
            endTime = auction.endTime,
            sellerId = auction.seller.id ?: throw IllegalStateException("Seller ID cannot be null"),
            sellerEmail = auction.seller.email
        )
    }
}