package hu.bme.student.auction.auction_platform.bidding

import hu.bme.student.auction.auction_platform.user.User
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1")
class BidController(
    private val bidService: BidService
) {

    @PostMapping("/auctions/{auctionId}/bids")
    fun placeBid(
        @PathVariable auctionId: UUID,
        @AuthenticationPrincipal currentUser: User,
        @Valid @RequestBody request: PlaceBidRequest
    ): ResponseEntity<BidResponse> {
        val bid = bidService.placeBid(auctionId, currentUser, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(bid)
    }

    @GetMapping("/auctions/{auctionId}/bids")
    fun getAuctionBids(
        @PathVariable auctionId: UUID,
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<Page<BidResponse>> {
        val history = bidService.getAuctionBidHistory(auctionId, pageable)
        return ResponseEntity.ok(history)
    }

    @GetMapping("/users/me/bids")
    fun getMyBids(
        @AuthenticationPrincipal currentUser: User,
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<Page<BidResponse>> {
        val userBids = bidService.getUserBids(currentUser.id!!, pageable)
        return ResponseEntity.ok(userBids)
    }
}