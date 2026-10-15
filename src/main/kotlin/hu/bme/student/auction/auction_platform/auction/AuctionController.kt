package hu.bme.student.auction.auction_platform.auction

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/auctions")
class AuctionController(
    private val auctionService: AuctionService
) {

    @PostMapping
    fun createAuction(
        @Valid @RequestBody request: CreateAuctionRequest,
        @AuthenticationPrincipal principal: UserDetails
    ): ResponseEntity<AuctionResponse> {
        val response = auctionService.createAuction(request, principal.username)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{id}")
    fun getAuction(@PathVariable id: UUID): ResponseEntity<AuctionResponse> {
        return ResponseEntity.ok(auctionService.getAuctionById(id))
    }

    @GetMapping
    fun listActiveAuctions(
        @PageableDefault(size = 20, sort = ["endTime"], direction = Sort.Direction.ASC) pageable: Pageable
    ): ResponseEntity<Page<AuctionResponse>> {
        return ResponseEntity.ok(auctionService.getActiveAuctions(pageable))
    }

    @GetMapping("/seller/{sellerId}")
    fun listSellerAuctions(
        @PathVariable sellerId: UUID,
        @PageableDefault(size = 20, sort = ["startTime"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<AuctionResponse>> {
        return ResponseEntity.ok(auctionService.getAuctionsBySeller(sellerId, pageable))
    }

    @PatchMapping("/{id}/cancel")
    fun cancelAuction(
        @PathVariable id: UUID,
        @AuthenticationPrincipal principal: UserDetails
    ): ResponseEntity<AuctionResponse> {
        return ResponseEntity.ok(auctionService.cancelAuction(id, principal.username))
    }
}