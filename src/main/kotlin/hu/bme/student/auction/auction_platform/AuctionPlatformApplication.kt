package hu.bme.student.auction.auction_platform

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
class AuctionPlatformApplication

fun main(args: Array<String>) {
	runApplication<AuctionPlatformApplication>(*args)
}
