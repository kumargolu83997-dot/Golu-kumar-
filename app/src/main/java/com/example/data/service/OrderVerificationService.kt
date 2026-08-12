package com.example.data.service

interface OrderVerificationService {
    fun calculateReward(orderAmount: Double): Double
    suspend fun verifyExternalMarketplaceOrder(orderNumber: String): OrderVerificationResult
}

data class OrderVerificationResult(
    val isVerified: Boolean,
    val orderAmount: Double,
    val rewardAmount: Double,
    val status: String, // "Received", "Rejected", "Processing"
    val statusDetails: String
)

class MarketplaceVerificationServiceImpl : OrderVerificationService {

    /**
     * Strict reward rules as specified:
     * - Verified received order from ₹100 to ₹999 = ₹4 reward
     * - Verified received order of ₹1000 or more = ₹100 reward
     * - Orders under ₹100 = ₹0
     */
    override fun calculateReward(orderAmount: Double): Double {
        return when {
            orderAmount >= 1000.0 -> 100.0
            orderAmount >= 100.0 -> 4.0
            else -> 0.0
        }
    }

    override suspend fun verifyExternalMarketplaceOrder(orderNumber: String): OrderVerificationResult {
        // Backend affiliate postback API hook (Flipkart Affiliate API / Amazon Associates API / Meesho / Myntra)
        return OrderVerificationResult(
            isVerified = false,
            orderAmount = 0.0,
            rewardAmount = 0.0,
            status = "Processing",
            statusDetails = "Awaiting delivery and return window completion from merchant affiliate tracking feed."
        )
    }
}
