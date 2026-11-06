package hu.bme.student.auction.auction_platform.bidding

open class BiddingException(message: String) : RuntimeException(message)

class AuctionNotActiveException(message: String = "Auction is not active for bidding") : BiddingException(message)

class InvalidBidException(message: String) : BiddingException(message)

class SelfBiddingProhibitedException(message: String = "Sellers cannot bid on their own auctions") : BiddingException(message)
