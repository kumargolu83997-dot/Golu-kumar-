package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.WithdrawalRequestEntity
import com.example.ui.theme.BentoBg
import com.example.ui.theme.BentoCardBorder
import com.example.ui.theme.BentoDarkPurpleText
import com.example.ui.theme.BentoPrimaryContainer
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WithdrawalScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val user by viewModel.currentUser.collectAsState()
    val withdrawals by viewModel.userWithdrawals.collectAsState()

    val balance = user?.balance ?: 0.0
    val hasPinSet = user?.hashedPin != null

    var amountText by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf("UPI") } // "UPI" or "BANK"
    var upiIdText by remember { mutableStateOf("") }
    var accountNumText by remember { mutableStateOf("") }
    var ifscText by remember { mutableStateOf("") }
    var holderNameText by remember { mutableStateOf("") }
    var pinText by remember { mutableStateOf("") }

    // Pin Set state if user hasn't set pin yet
    var newPinText by remember { mutableStateOf("") }
    var showSetPinSection by remember { mutableStateOf(!hasPinSet) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BentoBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White, CircleShape)
                        .border(1.dp, BentoCardBorder, CircleShape)
                        .testTag("withdrawal_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = BentoDarkPurpleText
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Withdraw Funds",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = BentoDarkPurpleText
                        )
                    )
                    Text(
                        text = "Transfer earnings to UPI or Bank Account",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 12.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Wallet Balance Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BentoPrimaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Available Balance",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f))
                        )
                        Text(
                            text = "₹${String.format("%.2f", balance)}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Min ₹100",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // PIN Setup Card if not set
            if (!hasPinSet || showSetPinSection) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = "PIN", tint = BentoPrimaryContainer)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Set Security Withdrawal PIN",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BentoDarkPurpleText
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Required to authorize all future withdrawal requests (4 to 8 digits).",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 11.sp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = newPinText,
                            onValueChange = { newPinText = it.filter { c -> c.isDigit() }.take(8) },
                            label = { Text("New 4-8 Digit Security PIN") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("set_pin_input"),
                            shape = RoundedCornerShape(14.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                viewModel.setSecurityPin(newPinText) { success, msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    if (success) showSetPinSection = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("save_pin_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryContainer),
                            enabled = newPinText.length in 4..8
                        ) {
                            Text("Save Withdrawal Security PIN", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
            }

            // Withdrawal Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "New Withdrawal Request",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BentoDarkPurpleText
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Amount Input
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("Withdrawal Amount (Min ₹100)") },
                        leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = BentoPrimaryContainer) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("withdraw_amount_input"),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Select Payout Method",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BentoDarkPurpleText
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // UPI Method Option
                        MethodSelectChip(
                            title = "UPI Instant",
                            subtitle = "GPay, PhonePe, Paytm",
                            icon = Icons.Default.QrCode,
                            isSelected = selectedMethod == "UPI",
                            onClick = { selectedMethod = "UPI" },
                            modifier = Modifier.weight(1f)
                        )

                        // Bank Method Option
                        MethodSelectChip(
                            title = "Bank Account",
                            subtitle = "NEFT / IMPS Transfer",
                            icon = Icons.Default.AccountBalance,
                            isSelected = selectedMethod == "BANK",
                            onClick = { selectedMethod = "BANK" },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // UPI Input
                    if (selectedMethod == "UPI") {
                        OutlinedTextField(
                            value = upiIdText,
                            onValueChange = { upiIdText = it },
                            label = { Text("UPI ID (e.g. 9876543210@ybl / name@upi)") },
                            leadingIcon = { Icon(Icons.Default.Payment, contentDescription = "UPI") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("withdraw_upi_input"),
                            shape = RoundedCornerShape(14.dp)
                        )
                    } else {
                        // Bank Account Inputs
                        OutlinedTextField(
                            value = accountNumText,
                            onValueChange = { accountNumText = it },
                            label = { Text("Bank Account Number") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("withdraw_account_input"),
                            shape = RoundedCornerShape(14.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = ifscText,
                            onValueChange = { ifscText = it.uppercase(Locale.ROOT) },
                            label = { Text("Bank IFSC Code (e.g. SBIN0001234)") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("withdraw_ifsc_input"),
                            shape = RoundedCornerShape(14.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = holderNameText,
                            onValueChange = { holderNameText = it },
                            label = { Text("Account Holder Name") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("withdraw_holder_input"),
                            shape = RoundedCornerShape(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Security PIN Input
                    OutlinedTextField(
                        value = pinText,
                        onValueChange = { pinText = it.filter { c -> c.isDigit() }.take(8) },
                        label = { Text("4-8 Digit Withdrawal PIN") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("withdraw_pin_input"),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull() ?: 0.0
                            viewModel.submitWithdrawalRequest(
                                amount = amt,
                                method = selectedMethod,
                                upiId = upiIdText,
                                accountNumber = accountNumText,
                                ifscCode = ifscText,
                                holderName = holderNameText,
                                pin = pinText,
                                onResult = { success, msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                    if (success) {
                                        amountText = ""
                                        pinText = ""
                                    }
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_withdrawal_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryContainer)
                    ) {
                        Text(
                            text = "Submit Withdrawal Request",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Recent Requests Header
            Text(
                text = "Withdrawal Requests History",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = BentoDarkPurpleText
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (withdrawals.isEmpty()) {
                Text(
                    text = "No withdrawal requests submitted yet.",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    withdrawals.forEach { item ->
                        WithdrawalItemRow(item = item)
                    }
                }
            }
        }
    }
}

@Composable
fun MethodSelectChip(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderCol = if (isSelected) BentoPrimaryContainer else BentoCardBorder
    val bgCol = if (isSelected) BentoPrimaryContainer.copy(alpha = 0.08f) else Color.White

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgCol)
            .border(1.5.dp, borderCol, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isSelected) BentoPrimaryContainer else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = BentoDarkPurpleText
                    )
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontSize = 10.sp)
            )
        }
    }
}

@Composable
fun WithdrawalItemRow(item: WithdrawalRequestEntity) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val dateStr = dateFormat.format(Date(item.requestTimestamp))

    val (statusColor, statusBg, statusIcon) = when (item.status) {
        "Paid", "Approved" -> Triple(Color(0xFF2E7D32), Color(0xFFE8F5E9), Icons.Default.CheckCircle)
        "Rejected" -> Triple(Color(0xFFC62828), Color(0xFFFFEBEE), Icons.Default.Shield)
        else -> Triple(Color(0xFFE65100), Color(0xFFFFF3E0), Icons.Default.HourglassTop) // Pending
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Ref: ${item.requestId}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = BentoDarkPurpleText
                    )
                )
                Text(
                    text = "${item.method} • $dateStr",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 11.sp)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${String.format("%.2f", item.amount)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = BentoDarkPurpleText
                    )
                )
                Box(
                    modifier = Modifier
                        .background(statusBg, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.status,
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    )
                }
            }
        }
    }
}
