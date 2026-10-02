package hu.bme.student.auction.auction_platform.user

import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.math.BigDecimal
import java.util.*

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var passwordHash: String,

    @Column(nullable = false)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: Role = Role.ROLE_USER,

    @Column(nullable = false, precision = 12, scale = 2)
    var balance: BigDecimal = BigDecimal.ZERO
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority(role.name))

    override fun getPassword(): String = passwordHash

    override fun getUsername(): String = email

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass = if (other is HibernateProxy) {
            other.hibernateLazyInitializer.persistentClass
        } else {
            other.javaClass
        }
        val thisEffectiveClass = if (this is HibernateProxy) {
            this.hibernateLazyInitializer.persistentClass
        } else {
            this.javaClass
        }
        if (thisEffectiveClass != oEffectiveClass) return false
        other as User

        return id != null && id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) {
            hibernateLazyInitializer.persistentClass.hashCode()
        } else {
            javaClass.hashCode()
        }
}