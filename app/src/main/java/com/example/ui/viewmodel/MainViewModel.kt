package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.MarketplaceLinkEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.SupportTicketEntity
import com.example.data.local.entity.TicketReplyEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.WalletTransactionEntity
import com.example.data.local.entity.WithdrawalRequestEntity
import com.example.data.repository.AppRepository
import com.example.data.service.CompliantPayoutProviderImpl
import com.example.data.service.MarketplaceVerificationServiceImpl
import com.example.data.service.OtpResponse
import com.example.data.service.ProductionReadyOtpService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AuthUiState(
    val phone: String = "",
    val otp: String = "",
    val sessionToken: String = "",
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val devOtpHint: String? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val repository: AppRepository = AppRepository(
        db = AppDatabase.getInstance(application),
        otpService = ProductionReadyOtpService(),
        verificationService = MarketplaceVerificationServiceImpl(),
        payoutService = CompliantPayoutProviderImpl()
    )

    // Auth State
    private val _authUiState = MutableStateFlow(AuthUiState())
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    // Current Logged-in User Phone
    private val _currentUserPhone = MutableStateFlow<String?>(null)
    val currentUserPhone: StateFlow<String?> = _currentUserPhone.asStateFlow()

    // Active User Entity Flow
    val currentUser: StateFlow<UserEntity?> = _currentUserPhone.flatMapLatest { phone ->
        if (phone.isNullOrEmpty()) flowOf(null)
        else repository.getUserFlow(phone)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // User Orders Flow
    val userOrders: StateFlow<List<OrderEntity>> = _currentUserPhone.flatMapLatest { phone ->
        if (phone.isNullOrEmpty()) flowOf(emptyList())
        else repository.getOrdersForUserFlow(phone)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User Wallet Transactions Flow
    val userTransactions: StateFlow<List<WalletTransactionEntity>> = _currentUserPhone.flatMapLatest { phone ->
        if (phone.isNullOrEmpty()) flowOf(emptyList())
        else repository.getTransactionsForUserFlow(phone)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User Withdrawals Flow
    val userWithdrawals: StateFlow<List<WithdrawalRequestEntity>> = _currentUserPhone.flatMapLatest { phone ->
        if (phone.isNullOrEmpty()) flowOf(emptyList())
        else repository.getWithdrawalsForUserFlow(phone)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Support Tickets Flow
    val userTickets: StateFlow<List<SupportTicketEntity>> = _currentUserPhone.flatMapLatest { phone ->
        if (phone.isNullOrEmpty()) flowOf(emptyList())
        else repository.getTicketsForUserFlow(phone)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Categories
    val categories: StateFlow<List<CategoryEntity>> = repository.getActiveCategoriesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Mode Toggle
    private val _isAdminMode = MutableStateFlow(false)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    init {
        // Automatically start on Order Earning Dashboard with default session
        loginWithDemoUser("9876543210")
    }

    fun toggleAdminMode() {
        _isAdminMode.value = !_isAdminMode.value
    }

    // Admin Data Flows
    val allOrders: StateFlow<List<OrderEntity>> = repository.getAllOrdersFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allWithdrawals: StateFlow<List<WithdrawalRequestEntity>> = repository.getAllWithdrawalsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTickets: StateFlow<List<SupportTicketEntity>> = repository.getAllTicketsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allLinks: StateFlow<List<MarketplaceLinkEntity>> = repository.getAllLinksFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Auth Actions
    fun updatePhone(newPhone: String) {
        _authUiState.value = _authUiState.value.copy(
            phone = newPhone.filter { it.isDigit() }.take(10),
            errorMessage = null
        )
    }

    fun updateOtp(newOtp: String) {
        _authUiState.value = _authUiState.value.copy(
            otp = newOtp.filter { it.isDigit() }.take(6),
            errorMessage = null
        )
    }

    fun sendOtp() {
        val phone = _authUiState.value.phone
        viewModelScope.launch {
            _authUiState.value = _authUiState.value.copy(isLoading = true, errorMessage = null)
            val response: OtpResponse = repository.requestLoginOtp(phone)
            if (response.success) {
                _authUiState.value = _authUiState.value.copy(
                    isLoading = false,
                    sessionToken = response.sessionToken,
                    devOtpHint = response.devOtpForTest,
                    successMessage = "OTP sent! Test code: ${response.devOtpForTest ?: "123456"}"
                )
            } else {
                _authUiState.value = _authUiState.value.copy(
                    isLoading = false,
                    errorMessage = response.message
                )
            }
        }
    }

    fun verifyOtp() {
        val state = _authUiState.value
        viewModelScope.launch {
            _authUiState.value = _authUiState.value.copy(isLoading = true, errorMessage = null)
            val isVerified = repository.verifyLoginOtp(state.phone, state.otp, state.sessionToken)
            if (isVerified) {
                _currentUserPhone.value = state.phone
                _authUiState.value = _authUiState.value.copy(
                    isLoading = false,
                    isLoggedIn = true,
                    successMessage = "Logged in successfully!"
                )
            } else {
                _authUiState.value = _authUiState.value.copy(
                    isLoading = false,
                    errorMessage = "Invalid OTP code. Please try again."
                )
            }
        }
    }

    fun logout() {
        _currentUserPhone.value = null
        _authUiState.value = AuthUiState()
    }

    // Quick Login for testing/demo
    fun loginWithDemoUser(phone: String = "9876543210") {
        viewModelScope.launch {
            repository.verifyLoginOtp(phone, "123456", "DEMO_TOKEN")
            _currentUserPhone.value = phone
            _authUiState.value = AuthUiState(phone = phone, isLoggedIn = true)
        }
    }

    // Set PIN Action
    fun setSecurityPin(pin: String, onResult: (Boolean, String) -> Unit) {
        val phone = _currentUserPhone.value ?: return
        viewModelScope.launch {
            val success = repository.setWithdrawalPin(phone, pin)
            if (success) {
                onResult(true, "Security PIN updated successfully!")
            } else {
                onResult(false, "PIN must be 4 to 8 digits.")
            }
        }
    }

    // Open Link & Create Order
    fun openMarketplaceLink(
        categoryId: String,
        categoryName: String,
        link: MarketplaceLinkEntity,
        onOrderCreated: (OrderEntity) -> Unit
    ) {
        val phone = _currentUserPhone.value ?: return
        viewModelScope.launch {
            val order = repository.createOrderOnLinkClick(phone, categoryId, categoryName, link)
            onOrderCreated(order)
        }
    }

    // Withdrawal Action
    fun submitWithdrawalRequest(
        amount: Double,
        method: String,
        upiId: String? = null,
        accountNumber: String? = null,
        ifscCode: String? = null,
        holderName: String? = null,
        pin: String,
        onResult: (Boolean, String) -> Unit
    ) {
        val phone = _currentUserPhone.value ?: return
        viewModelScope.launch {
            val (success, message) = repository.requestWithdrawal(
                phone = phone,
                amount = amount,
                method = method,
                upiId = upiId,
                accountNumber = accountNumber,
                ifscCode = ifscCode,
                holderName = holderName,
                enteredPin = pin
            )
            onResult(success, message)
        }
    }

    // Ticket Actions
    fun createTicket(
        category: String,
        subject: String,
        message: String,
        orderId: String? = null,
        onCreated: () -> Unit
    ) {
        val phone = _currentUserPhone.value ?: return
        viewModelScope.launch {
            repository.createSupportTicket(phone, category, subject, message, orderId)
            onCreated()
        }
    }

    fun replyTicket(ticketId: Long, senderRole: String, senderName: String, message: String) {
        viewModelScope.launch {
            repository.replyToTicket(ticketId, senderRole, senderName, message)
        }
    }

    fun closeTicket(ticketId: Long) {
        viewModelScope.launch {
            repository.closeTicket(ticketId)
        }
    }

    fun getTicketReplies(ticketId: Long): StateFlow<List<TicketReplyEntity>> {
        return repository.getTicketRepliesFlow(ticketId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    // Admin Link Management
    fun adminAddLink(
        categoryId: String,
        title: String,
        targetUrl: String,
        offerText: String,
        terms: String,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            repository.addMarketplaceLink(
                MarketplaceLinkEntity(
                    categoryId = categoryId,
                    title = title,
                    targetUrl = targetUrl,
                    cashbackOfferText = offerText,
                    termsAndConditions = terms
                )
            )
            onDone()
        }
    }

    fun adminDeleteLink(linkId: Long) {
        viewModelScope.launch {
            repository.deleteMarketplaceLink(linkId)
        }
    }

    // Admin Order Verification
    fun adminMarkOrderReceived(orderId: Long, amount: Double, notes: String?, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.markOrderAsReceivedAndReward(orderId, amount, notes)
            onDone(success)
        }
    }

    fun adminRejectOrder(orderId: Long, reason: String) {
        viewModelScope.launch {
            repository.rejectOrder(orderId, reason)
        }
    }

    fun adminApproveWithdrawal(requestId: Long, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.updateWithdrawalStatus(
                requestId = requestId,
                status = "Paid",
                payoutRef = "ADMIN_CONFIRMED_${System.currentTimeMillis().toString().takeLast(6)}"
            )
            onDone()
        }
    }
}
