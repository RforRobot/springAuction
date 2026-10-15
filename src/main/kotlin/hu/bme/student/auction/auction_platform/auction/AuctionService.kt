package hu.bme.student.auction.auction_platform.auction

import hu.bme.student.auction.auction_platform.user.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.NoSuchElementException
import java.util.UUID

@Service
@Transactional(readOnly = true)
class AuctionService(
    private val auctionRepository: AuctionRepository,
    private val userRepository: UserRepository
) {

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
}