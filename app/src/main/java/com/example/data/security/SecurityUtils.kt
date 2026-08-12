package com.example.data.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Locale

object SecurityUtils {

    /**
     * Generates a random cryptographic salt hex string.
     */
    fun generateSalt(): String {
        val random = SecureRandom()
        val saltBytes = ByteArray(16)
        random.nextBytes(saltBytes)
        return saltBytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Hashes text (PIN / OTP secret) using SHA-256 with salt.
     * Never store plain text passwords, OTPs, or PINs.
     */
    fun hashWithSalt(input: String, salt: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val saltedInput = "$input:$salt"
        val digest = md.digest(saltedInput.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    /**
     * Verifies plain text input against stored hash + salt.
     */
    fun verifyWithSalt(input: String, storedHash: String, salt: String): Boolean {
        val computedHash = hashWithSalt(input, salt)
        return computedHash.equals(storedHash, ignoreCase = true)
    }

    /**
     * Validates 10-digit mobile number for India (+91 standard).
     */
    fun isValidMobileNumber(phone: String): Boolean {
        val cleaned = phone.replace(Regex("[^0-9]"), "")
        return cleaned.length == 10 && cleaned.matches(Regex("^[6-9][0-9]{9}$"))
    }

    /**
     * Validates 4 to 8 digit numeric security PIN.
     */
    fun isValidWithdrawalPin(pin: String): Boolean {
        return pin.length in 4..8 && pin.all { it.isDigit() }
    }

    /**
     * Validates UPI ID format (e.g. user@upi, 9876543210@ybl, etc.).
     */
    fun isValidUpiId(upiId: String): Boolean {
        val trimmed = upiId.trim()
        return trimmed.contains("@") && trimmed.length >= 5 && !trimmed.contains(" ")
    }

    /**
     * Validates Indian Bank Account Number (9 to 18 digits).
     */
    fun isValidBankAccount(accNumber: String): Boolean {
        val cleaned = accNumber.trim()
        return cleaned.length in 9..18 && cleaned.all { it.isDigit() }
    }

    /**
     * Validates Indian Financial System Code (IFSC) format e.g., SBIN0001234.
     */
    fun isValidIfscCode(ifsc: String): Boolean {
        val formatted = ifsc.trim().uppercase(Locale.ROOT)
        return formatted.matches(Regex("^[A-Z]{4}0[A-Z0-9]{6}$"))
    }

    /**
     * In-memory Rate Limiter to prevent brute force OTP or withdrawal attempts.
     */
    private val actionTimestamps = mutableMapOf<String, MutableList<Long>>()

    @Synchronized
    fun isRateLimited(key: String, maxRequests: Int = 3, timeWindowMs: Long = 60_000L): Boolean {
        val now = System.currentTimeMillis()
        val timestamps = actionTimestamps.getOrPut(key) { mutableListOf() }
        
        // Remove outdated timestamps
        timestamps.removeAll { (now - it) > timeWindowMs }

        if (timestamps.size >= maxRequests) {
            return true // Rate limit exceeded
        }

        timestamps.add(now)
        return false
    }

    @Synchronized
    fun getRemainingCooldownSeconds(key: String, timeWindowMs: Long = 60_000L): Long {
        val now = System.currentTimeMillis()
        val timestamps = actionTimestamps[key] ?: return 0L
        if (timestamps.isEmpty()) return 0L
        val oldest = timestamps.minOrNull() ?: return 0L
        val diff = timeWindowMs - (now - oldest)
        return if (diff > 0) diff / 1000L else 0L
    }
}
