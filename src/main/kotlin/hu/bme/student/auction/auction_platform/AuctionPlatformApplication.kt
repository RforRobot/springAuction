package hu.bme.student.auction.auction_platform

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AuctionPlatformApplication

fun main(args: Array<String>) {
	runApplication<AuctionPlatformApplication>(*args)
}
