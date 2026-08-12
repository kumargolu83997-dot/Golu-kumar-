package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.MarketplaceLinkEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.SupportTicketEntity
import com.example.data.local.entity.TicketReplyEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.WalletTransactionEntity
import com.example.data.local.entity.WithdrawalRequestEntity
import com.example.data.security.SecurityUtils
import com.example.data.service.OrderVerificationService
import com.example.data.service.OtpResponse
import com.example.data.service.OtpService
import com.example.data.service.PayoutProviderService
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class AppRepository(
    private val db: AppDatabase,
    private val otpService: OtpService,
    private val verificationService: OrderVerificationService,
    private val payoutService: PayoutProviderService
) {
    private val userDao = db.userDao()
    private val categoryDao = db.categoryDao()
    private val linkDao = db.marketplaceLinkDao()
    private val orderDao = db.orderDao()
    private val transactionDao = db.walletTransactionDao()
    private val withdrawalDao = db.withdrawalRequestDao()
    private val ticketDao = db.supportTicketDao()

    // Authentication & User Profile
    fun getUserFlow(phone: String): Flow<UserEntity?> = userDao.getUserByPhoneFlow(phone)

    suspend fun getUserByPhone(phone: String): UserEntity? = userDao.getUserByPhone(phone)

    suspend fun requestLoginOtp(phone: String): OtpResponse {
        if (!SecurityUtils.isValidMobileNumber(phone)) {
            return OtpResponse(false, "", "Invalid 10-digit mobile number")
        }
        return otpService.sendOtp(phone)
    }

    suspend fun verifyLoginOtp(phone: String, enteredOtp: String, sessionToken: String): Boolean {
        val isValid = otpService.verifyOtp(phone, enteredOtp, sessionToken)
        if (isValid) {
            var existingUser = userDao.getUserByPhone(phone)
            if (existingUser == null) {
                existingUser = UserEntity(
                    phone = phone,
                    name = "User ${phone.takeLast(4)}"
                )
                userDao.insertOrUpdateUser(existingUser)
            }
        }
        return isValid
    }

    // Set / Verify Withdrawal Security PIN
    suspend fun setWithdrawalPin(phone: String, plainPin: String): Boolean {
        if (!SecurityUtils.isValidWithdrawalPin(plainPin)) return false
        val salt = SecurityUtils.generateSalt()
        val hashedPin = SecurityUtils.hashWithSalt(plainPin, salt)
        userDao.updateWithdrawalPin(phone, hashedPin, salt)
        return true
    }

    suspend fun verifyWithdrawalPin(phone: String, plainPin: String): Boolean {
        val user = userDao.getUserByPhone(phone) ?: return false
        if (user.hashedPin == null || user.pinSalt == null) return false
        return SecurityUtils.verifyWithSalt(plainPin, user.hashedPin, user.pinSalt)
    }

    // Categories & Links
    fun getCategoriesFlow(): Flow<List<CategoryEntity>> = categoryDao.getAllCategoriesFlow()
    fun getActiveCategoriesFlow(): Flow<List<CategoryEntity>> = categoryDao.getAllActiveCategoriesFlow()

    fun getLinksForCategoryFlow(categoryId: String): Flow<List<MarketplaceLinkEntity>> =
        linkDao.getActiveLinksForCategoryFlow(categoryId)

    fun getAllLinksFlow(): Flow<List<MarketplaceLinkEntity>> = linkDao.getAllLinksFlow()

    suspend fun addMarketplaceLink(link: MarketplaceLinkEntity): Long = linkDao.insertLink(link)
    suspend fun updateMarketplaceLink(link: MarketplaceLinkEntity) = linkDao.updateLink(link)
    suspend fun deleteMarketplaceLink(id: Long) = linkDao.deleteLinkById(id)

    // Orders & Reward Processing
    fun getOrdersForUserFlow(phone: String): Flow<List<OrderEntity>> = orderDao.getOrdersForUserFlow(phone)
    fun getAllOrdersFlow(): Flow<List<OrderEntity>> = orderDao.getAllOrdersFlow()

    suspend fun createOrderOnLinkClick(
        userId: String,
        categoryId: String,
        categoryName: String,
        link: MarketplaceLinkEntity
    ): OrderEntity {
        val orderNum = "ORD-${(100000..999999).random()}"
        val order = OrderEntity(
            orderNumber = orderNum,
            userId = userId,
            categoryId = categoryId,
            categoryName = categoryName,
            linkId = link.id,
            linkTitle = link.title,
            targetUrl = link.targetUrl,
            status = "Reward Processing", // Step 5 requirement
            isRewardCredited = false
        )
        val insertedId = orderDao.insertOrder(order)
        return order.copy(id = insertedId)
    }

    /**
     * Admin/Verification Service marks order as "Received".
     * Validates amount & calculates reward (₹4 for ₹100-999, ₹100 for ₹1000+).
     * Credits balance EXACTLY ONCE.
     */
    suspend fun markOrderAsReceivedAndReward(orderId: Long, actualOrderAmount: Double, adminNotes: String? = null): Boolean {
        val order = orderDao.getOrderById(orderId) ?: return false
        if (order.isRewardCredited || order.status == "Received") {
            return false // Already credited / processed
        }

        val rewardAmount = verificationService.calculateReward(actualOrderAmount)

        // Update order status to Received
        orderDao.updateOrderVerificationStatus(
            orderId = orderId,
            status = "Received",
            amount = actualOrderAmount,
            reward = rewardAmount,
            notes = adminNotes
        )

        // Credit reward if eligible and not already credited
        if (rewardAmount > 0) {
            orderDao.markRewardCredited(orderId)
            userDao.creditUserBalance(order.userId, rewardAmount)

            // Record transaction log
            transactionDao.insertTransaction(
                WalletTransactionEntity(
                    userId = order.userId,
                    amount = rewardAmount,
                    type = "CREDIT_REWARD",
                    status = "COMPLETED",
                    referenceId = order.orderNumber,
                    description = "Cashback reward for verified order #${order.orderNumber} (${order.categoryName})"
                )
            )
        }
        return true
    }

    suspend fun rejectOrder(orderId: Long, reason: String) {
        orderDao.updateOrderVerificationStatus(
            orderId = orderId,
            status = "Rejected",
            amount = 0.0,
            reward = 0.0,
            notes = reason
        )
    }

    // Wallet Transactions & Withdrawals
    fun getTransactionsForUserFlow(phone: String): Flow<List<WalletTransactionEntity>> =
        transactionDao.getTransactionsForUserFlow(phone)

    fun getWithdrawalsForUserFlow(phone: String): Flow<List<WithdrawalRequestEntity>> =
        withdrawalDao.getWithdrawalRequestsForUserFlow(phone)

    fun getAllWithdrawalsFlow(): Flow<List<WithdrawalRequestEntity>> =
        withdrawalDao.getAllWithdrawalRequestsFlow()

    suspend fun requestWithdrawal(
        phone: String,
        amount: Double,
        method: String,
        upiId: String? = null,
        accountNumber: String? = null,
        ifscCode: String? = null,
        holderName: String? = null,
        enteredPin: String
    ): Pair<Boolean, String> {
        if (amount < 100.0) {
            return Pair(false, "Minimum withdrawal amount is ₹100")
        }

        if (!verifyWithdrawalPin(phone, enteredPin)) {
            return Pair(false, "Incorrect withdrawal PIN")
        }

        val user = userDao.getUserByPhone(phone) ?: return Pair(false, "User not found")
        if (user.balance < amount) {
            return Pair(false, "Insufficient wallet balance")
        }

        if (method == "UPI") {
            if (upiId.isNullOrEmpty() || !SecurityUtils.isValidUpiId(upiId)) {
                return Pair(false, "Invalid UPI ID")
            }
        } else if (method == "BANK") {
            if (accountNumber.isNullOrEmpty() || !SecurityUtils.isValidBankAccount(accountNumber)) {
                return Pair(false, "Invalid Bank Account Number (9-18 digits)")
            }
            if (ifscCode.isNullOrEmpty() || !SecurityUtils.isValidIfscCode(ifscCode)) {
                return Pair(false, "Invalid IFSC Code (e.g. SBIN0001234)")
            }
            if (holderName.isNullOrEmpty()) {
                return Pair(false, "Account Holder Name is required")
            }
        }

        // Debit user balance first atomically
        val debitedRows = userDao.debitUserBalanceForWithdrawal(phone, amount)
        if (debitedRows <= 0) {
            return Pair(false, "Failed to debit balance")
        }

        val requestId = "WDR-${(100000..999999).random()}"

        // Submit to payout gateway - stays Pending
        val payoutRes = if (method == "UPI") {
            payoutService.initiateUpiPayout(requestId, upiId!!, amount)
        } else {
            payoutService.initiateBankPayout(requestId, accountNumber!!, ifscCode!!, holderName!!, amount)
        }

        val withdrawal = WithdrawalRequestEntity(
            requestId = requestId,
            userId = phone,
            amount = amount,
            method = method,
            upiId = upiId,
            accountNumber = accountNumber,
            ifscCode = ifscCode,
            accountHolderName = holderName,
            status = "Pending", // Step 14 requirement
            payoutReference = payoutRes.payoutReferenceId
        )
        withdrawalDao.insertWithdrawalRequest(withdrawal)

        transactionDao.insertTransaction(
            WalletTransactionEntity(
                userId = phone,
                amount = -amount,
                type = "WITHDRAWAL_REQUEST",
                status = "PENDING",
                referenceId = requestId,
                description = "Withdrawal request via $method (Ref: $requestId)"
            )
        )

        return Pair(true, "Withdrawal request submitted successfully. Status: Pending.")
    }

    // Support Tickets
    fun getTicketsForUserFlow(phone: String): Flow<List<SupportTicketEntity>> =
        ticketDao.getTicketsForUserFlow(phone)

    fun getAllTicketsFlow(): Flow<List<SupportTicketEntity>> =
        ticketDao.getAllTicketsFlow()

    fun getTicketRepliesFlow(ticketId: Long): Flow<List<TicketReplyEntity>> =
        ticketDao.getRepliesForTicketFlow(ticketId)

    suspend fun createSupportTicket(
        phone: String,
        category: String,
        subject: String,
        message: String,
        orderId: String? = null
    ): Long {
        val ticketNum = "TCK-${(10000..99999).random()}"
        val ticket = SupportTicketEntity(
            ticketNumber = ticketNum,
            userId = phone,
            category = category,
            subject = subject,
            initialMessage = message,
            relatedOrderId = orderId,
            status = "Open"
        )
        val id = ticketDao.insertTicket(ticket)
        // Add initial message as reply
        ticketDao.insertReply(
            TicketReplyEntity(
                ticketId = id,
                senderRole = "USER",
                senderName = "Customer",
                message = message
            )
        )
        return id
    }

    suspend fun replyToTicket(ticketId: Long, senderRole: String, senderName: String, message: String) {
        ticketDao.insertReply(
            TicketReplyEntity(
                ticketId = ticketId,
                senderRole = senderRole,
                senderName = senderName,
                message = message
            )
        )
        val newStatus = if (senderRole == "ADMIN") "Replied" else "In Progress"
        ticketDao.updateTicketStatus(ticketId, newStatus)
    }

    suspend fun closeTicket(ticketId: Long) {
        ticketDao.updateTicketStatus(ticketId, "Closed")
    }

    suspend fun updateWithdrawalStatus(requestId: Long, status: String, payoutRef: String?) {
        withdrawalDao.updateWithdrawalStatus(requestId, status, payoutRef)
    }
}
