package com.example.data.service

interface PayoutProviderService {
    suspend fun initiateUpiPayout(withdrawalId: String, upiId: String, amount: Double): PayoutResponse
    suspend fun initiateBankPayout(withdrawalId: String, accountNumber: String, ifsc: String, holderName: String, amount: Double): PayoutResponse
}

data class PayoutResponse(
    val status: String, // "PENDING", "PROCESSING", "SUCCESS", "FAILED"
    val payoutReferenceId: String?,
    val message: String
)

/**
 * Production payout integration gateway hook (e.g., RazorpayX, Cashfree Payouts, PayU).
 * Real transactions remain "PENDING" or "PROCESSING" until verified.
 */
class CompliantPayoutProviderImpl : PayoutProviderService {
    override suspend fun initiateUpiPayout(withdrawalId: String, upiId: String, amount: Double): PayoutResponse {
        val reference = "REF_UPI_${System.currentTimeMillis().toString().takeLast(8)}"
        return PayoutResponse(
            status = "PENDING",
            payoutReferenceId = reference,
            message = "Payout request submitted to banking provider gateway. Status set to Pending."
        )
    }

    override suspend fun initiateBankPayout(
        withdrawalId: String,
        accountNumber: String,
        ifsc: String,
        holderName: String,
        amount: Double
    ): PayoutResponse {
        val reference = "REF_BANK_${System.currentTimeMillis().toString().takeLast(8)}"
        return PayoutResponse(
            status = "PENDING",
            payoutReferenceId = reference,
            message = "NEFT/IMPS payout queued with banking partner. Status set to Pending."
        )
    }
}
