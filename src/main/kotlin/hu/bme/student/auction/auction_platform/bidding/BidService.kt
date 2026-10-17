package hu.bme.student.auction.auction_platform.bidding

import hu.bme.student.auction.auction_platform.auction.Auction
import hu.bme.student.auction.auction_platform.auction.AuctionRepository
import hu.bme.student.auction.auction_platform.auction.AuctionStatus
import hu.bme.student.auction.auction_platform.common.ResourceNotFoundException
import hu.bme.student.auction.auction_platform.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Service
class BidService(
    private val bidRepository: BidRepository,
    private val auctionRepository: AuctionRepository,
    private val messagingTemplate: SimpMessagingTemplate
) {

    @Transactional
    fun placeBid(auctionId: UUID, bidder: User, request: PlaceBidRequest): BidResponse {
        val now = Instant.now()

        // XLOCK
        val auction = auctionRepository.findByIdWithPessimisticLock(auctionId)
            ?: throw ResourceNotFoundException("Auction with id $auctionId not found")

        validateAuctionAcceptingBids(auction, now)

        if (auction.seller.id == bidder.id) {
            throw SelfBiddingProhibitedException("Sellers are prohibited from bidding on their own listings")
        }

        // Min increase
        val minimumRequired = calculateMinimumRequiredBid(auction)
        if (request.bidAmount < minimumRequired) {
            throw InvalidBidException("Bid of ${request.bidAmount} is insufficient. Minimum required bid is $minimumRequired")
        }

        val bid = bidRepository.save(
            Bid(
                bidAmount = request.bidAmount,
                auction = auction,
                bidder = bidder,
                createdAt = now
            )
        )

        auction.currentHighestBid = request.bidAmount

        val response = BidResponse.fromEntity(bid)

        // STOMP update
        broadcastBidUpdate(response)

        return response
    }

    @Transactional(readOnly = true)
    fun getAuctionBidHistory(auctionId: UUID, pageable: Pageable): Page<BidResponse> {
        if (!auctionRepository.existsById(auctionId)) {
            throw ResourceNotFoundException("Auction with id $auctionId not found")
        }
        return bidRepository.findAllByAuctionIdOrderByCreatedAtDesc(auctionId, pageable)
            .map { BidResponse.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    fun getUserBids(bidderId: UUID, pageable: Pageable): Page<BidResponse> {
        return bidRepository.findAllByBidderIdOrderByCreatedAtDesc(bidderId, pageable)
            .map { BidResponse.fromEntity(it) }
    }

    private fun validateAuctionAcceptingBids(auction: Auction, now: Instant) {
        if (auction.status != AuctionStatus.ACTIVE) {
            throw AuctionNotActiveException("Cannot bid on auction with status: ${auction.status}")
        }
        if (now.isBefore(auction.startTime)) {
            throw AuctionNotActiveException("Auction has not started yet")
        }
        if (now.isAfter(auction.endTime)) {
            throw AuctionNotActiveException("Auction has already expired")
        }
    }

    private fun calculateMinimumRequiredBid(auction: Auction): BigDecimal {
        val currentMax = auction.currentHighestBid
        return if (currentMax == null || currentMax.compareTo(BigDecimal.ZERO) == 0) {
            auction.startingPrice
        } else {
            currentMax.add(BigDecimal("1"))
        }
    }

    private fun broadcastBidUpdate(bidResponse: BidResponse) {
        val payload = BidBroadcastPayload(
            auctionId = bidResponse.auctionId,
            newHighestBid = bidResponse.bidAmount,
            highestBidderId = bidResponse.bidderId,
            bidId = bidResponse.id,
            timestamp = bidResponse.createdAt
        )
        messagingTemplate.convertAndSend("/topic/auctions/${bidResponse.auctionId}/bids", payload)
    }
}