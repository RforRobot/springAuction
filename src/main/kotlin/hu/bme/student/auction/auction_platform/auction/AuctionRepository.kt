package hu.bme.student.auction.auction_platform.auction

import jakarta.persistence.LockModeType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.Optional
import java.util.UUID

@Repository
interface AuctionRepository : JpaRepository<Auction, UUID> {

    // EntityGraph Forces LEFT JOIN to avoid N+1 problem
    @EntityGraph(attributePaths = ["seller"])
    @Query("SELECT a FROM Auction a WHERE a.id = :id")
    fun findByIdWithSeller(@Param("id") id: UUID): Optional<Auction>

    // Pageable maps to SQL LIMIT & OFFSET
    @EntityGraph(attributePaths = ["seller"])
    fun findAllByStatus(status: AuctionStatus, pageable: Pageable): Page<Auction>

    @EntityGraph(attributePaths = ["seller"])
    fun findAllBySellerId(sellerId: UUID, pageable: Pageable): Page<Auction>

    @Query("SELECT a FROM Auction a WHERE a.status = :status AND a.endTime <= :cutoff")
    fun findExpiredAuctions(
        @Param("status") status: AuctionStatus = AuctionStatus.ACTIVE,
        @Param("cutoff") cutoff: Instant = Instant.now()
    ): List<Auction>

    // XLOCK
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Auction a WHERE a.id = :id")
    fun findByIdWithPessimisticLock(@Param("id") id: UUID): Auction?

}