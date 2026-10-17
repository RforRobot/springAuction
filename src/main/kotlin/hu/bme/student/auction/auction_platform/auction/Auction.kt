package hu.bme.student.auction.auction_platform.auction

import hu.bme.student.auction.auction_platform.user.User
import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "auctions",
    indexes = [
        Index(name = "idx_auctions_status_end_time", columnList = "status, end_time")
    ]
)
class Auction(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    val id: UUID? = null,

    @Column(nullable = false, length = 150)
    var title: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    var description: String,

    @Column(name = "starting_price", updatable = false, nullable = false, precision = 19, scale = 4)
    val startingPrice: BigDecimal,

    @Column(name = "reserve_price", precision = 19, scale = 4)
    var reservePrice: BigDecimal? = null,

    @Column(name = "current_highest_bid", precision = 19, scale = 4)
    var currentHighestBid: BigDecimal? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: AuctionStatus = AuctionStatus.ACTIVE,

    @Column(name = "start_time", updatable = false, nullable = false)
    val startTime: Instant = Instant.now(),

    @Column(name = "end_time", nullable = false)
    var endTime: Instant,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id", updatable = false, nullable = false)
    val seller: User,

    @Version
    var version: Long = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass = if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass = if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as Auction
        return id != null && id == other.id
    }

    override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    override fun toString(): String {
        return "Auction(id=$id, title='$title', status=$status, currentHighestBid=$currentHighestBid, endTime=$endTime)"
    }
}