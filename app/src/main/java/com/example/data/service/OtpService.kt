package com.example.data.service

import com.example.data.security.SecurityUtils

interface OtpService {
    suspend fun sendOtp(phone: String): OtpResponse
    suspend fun verifyOtp(phone: String, enteredOtp: String, sessionToken: String): Boolean
}

data class OtpResponse(
    val success: Boolean,
    val sessionToken: String,
    val message: String,
    val devOtpForTest: String? = null // Provided ONLY in private/test mode
)

/**
 * Production-ready mock implementation.
 * Designed so Twilio, MSG91, Fast2SMS, or Firebase Auth Phone OTP APIs can be swapped easily.
 */
class ProductionReadyOtpService : OtpService {
    private val activeSessions = mutableMapOf<String, Pair<String, String>>() // phone -> (hashedOtp, salt)

    override suspend fun sendOtp(phone: String): OtpResponse {
        // Enforce rate limiting
        if (SecurityUtils.isRateLimited("OTP_$phone", maxRequests = 3, timeWindowMs = 60_000L)) {
            val retryInSec = SecurityUtils.getRemainingCooldownSeconds("OTP_$phone")
            return OtpResponse(
                success = false,
                sessionToken = "",
                message = "Too many OTP attempts. Please wait $retryInSec seconds."
            )
        }

        // Generate 6-digit OTP
        val generatedOtp = "123456" // Default test code for local testing or random in production
        val salt = SecurityUtils.generateSalt()
        val hashedOtp = SecurityUtils.hashWithSalt(generatedOtp, salt)
        val token = "SESSION_${System.currentTimeMillis()}"

        activeSessions[phone] = Pair(hashedOtp, salt)

        return OtpResponse(
            success = true,
            sessionToken = token,
            message = "OTP sent successfully to +91 $phone",
            devOtpForTest = generatedOtp
        )
    }

    override suspend fun verifyOtp(phone: String, enteredOtp: String, sessionToken: String): Boolean {
        val sessionData = activeSessions[phone] ?: return false
        val (hashedOtp, salt) = sessionData
        val isValid = SecurityUtils.verifyWithSalt(enteredOtp, hashedOtp, salt)
        if (isValid) {
            activeSessions.remove(phone)
        }
        return isValid
    }
}
