package hu.bme.student.auction.auction_platform.auction

import hu.bme.student.auction.auction_platform.user.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.NoSuchElementException
import java.util.UUID

import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Propagation
import java.math.BigDecimal
import java.time.Instant

@Service
@Transactional(readOnly = true)
class AuctionService(
    private val auctionRepository: AuctionRepository,
    private val userRepository: UserRepository
) {

    companion object {
        private val logger = LoggerFactory.getLogger(AuctionService::class.java)
    }

    @Transactional
    fun createAuction(request: CreateAuctionRequest, sellerEmail: String): AuctionResponse {
        val normalizedEmail = sellerEmail.trim().lowercase()
        val seller = userRepository.findByEmail(normalizedEmail)
            ?: throw NoSuchElementException("Authenticated seller not found")

        request.reservePrice?.let {
            require(it >= request.startingPrice) { "Reserve price cannot be lower than the starting price" }
        }

        val auction = Auction(
            title = request.title.trim(),
            description = request.description.trim(),
            startingPrice = request.startingPrice,
            reservePrice = request.reservePrice,
            endTime = request.endTime,
            seller = seller
        )

        return AuctionResponse.fromEntity(auctionRepository.save(auction))
    }

    fun getAuctionById(id: UUID): AuctionResponse {
        val auction = auctionRepository.findByIdWithSeller(id)
            .orElseThrow { NoSuchElementException("Auction not found with ID: $id") }
        return AuctionResponse.fromEntity(auction)
    }

    fun getActiveAuctions(pageable: Pageable): Page<AuctionResponse> {
        return auctionRepository.findAllByStatus(AuctionStatus.ACTIVE, pageable)
            .map { AuctionResponse.fromEntity(it) }
    }

    fun getAuctionsBySeller(sellerId: UUID, pageable: Pageable): Page<AuctionResponse> {
        return auctionRepository.findAllBySellerId(sellerId, pageable)
            .map { AuctionResponse.fromEntity(it) }
    }

    @Transactional
    fun cancelAuction(auctionId: UUID, requestedByUserEmail: String): AuctionResponse {
        val auction = auctionRepository.findByIdWithSeller(auctionId)
            .orElseThrow { NoSuchElementException("Auction not found with ID: $auctionId") }

        val normalizedEmail = requestedByUserEmail.trim().lowercase()
        if (auction.seller.email != normalizedEmail) {
            throw AccessDeniedException("You are not authorized to cancel this auction")
        }

        check(auction.status == AuctionStatus.ACTIVE) { "Only ACTIVE auctions can be cancelled" }
        check(auction.currentHighestBid == null) { "Cannot cancel an auction that has already received bids" }

        auction.status = AuctionStatus.CANCELLED
        return AuctionResponse.fromEntity(auction)
    }

    fun findExpiredAuctionIds(): List<UUID> {
        return auctionRepository.findExpiredAuctions(
            status = AuctionStatus.ACTIVE,
            cutoff = Instant.now()
        ).mapNotNull { it.id }
    }

    // Isolated transaction
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun closeAuction(auctionId: UUID) {
        val auction = auctionRepository.findById(auctionId).orElse(null) ?: return

        if (auction.status != AuctionStatus.ACTIVE) {
            return
        }

        if (auction.endTime.isAfter(Instant.now())) {
            return
        }

        auction.status = AuctionStatus.ENDED
        auctionRepository.save(auction)

        val reserveMet = auction.reservePrice?.let { reserve ->
            (auction.currentHighestBid ?: BigDecimal.ZERO) >= reserve
        } ?: true

        if (auction.currentHighestBid != null && reserveMet) {
            logger.info("Auction {} closed successfully. Winning bid: {}", auction.id, auction.currentHighestBid)
            // Hook for Bidding/WebSocket: broadcast AuctionEndedEvent(winner, finalPrice)
        } else {
            logger.info("Auction {} ended with no winner (reserve not met or no bids placed).", auction.id)
            // Hook for Bidding/WebSocket: broadcast AuctionUnsoldEvent()
        }
    }
}