package hu.bme.student.auction.auction_platform.bidding

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface BidRepository : JpaRepository<Bid, UUID> {

    @EntityGraph(attributePaths = ["bidder", "auction"])
    fun findAllByAuctionIdOrderByCreatedAtDesc(auctionId: UUID, pageable: Pageable): Page<Bid>

    @EntityGraph(attributePaths = ["auction"])
    fun findAllByBidderIdOrderByCreatedAtDesc(bidderId: UUID, pageable: Pageable): Page<Bid>

    @Query("SELECT b FROM Bid b WHERE b.auction.id = :auctionId ORDER BY b.bidAmount DESC LIMIT 1")
    fun findTopByAuctionIdOrderByBidAmountDesc(@Param("auctionId") auctionId: UUID): Bid?
}