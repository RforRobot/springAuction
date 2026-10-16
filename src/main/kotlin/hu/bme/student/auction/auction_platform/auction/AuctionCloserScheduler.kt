package hu.bme.student.auction.auction_platform.auction

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class AuctionCloserScheduler(
    private val auctionService: AuctionService
) {
    private val logger = LoggerFactory.getLogger(AuctionCloserScheduler::class.java)

    @Scheduled(fixedDelayString = "\${auction.scheduler.close-interval-ms:5000}")
    fun closeExpiredAuctions() {
        val expiredIds = auctionService.findExpiredAuctionIds()
        if (expiredIds.isEmpty()) {
            return
        }

        logger.debug("Found {} expired auction(s) to close", expiredIds.size)

        for (id in expiredIds) {
            try {
                auctionService.closeAuction(id)
            } catch (ex: Exception) {
                logger.error("Failed to close expired auction with ID: $id", ex)
            }
        }
    }
}