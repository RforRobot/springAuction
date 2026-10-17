package hu.bme.student.auction.auction_platform.bidding

import hu.bme.student.auction.auction_platform.auction.Auction
import hu.bme.student.auction.auction_platform.user.User
import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "bids",
    indexes = [
        Index(name = "idx_bids_auction_created_at", columnList = "auction_id, created_at DESC"),
        Index(name = "idx_bids_bidder_id", columnList = "bidder_id")
    ]
)
class Bid(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    val id: UUID? = null,

    @Column(name = "bid_amount", precision = 19, scale = 4, nullable = false, updatable = false)
    val bidAmount: BigDecimal,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auction_id", nullable = false, updatable = false)
    val auction: Auction,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bidder_id", nullable = false, updatable = false)
    val bidder: User,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass = if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass = if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as Bid
        return id != null && id == other.id
    }

    override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    override fun toString(): String =
        "Bid(id=$id, bidAmount=$bidAmount, createdAt=$createdAt)"
}