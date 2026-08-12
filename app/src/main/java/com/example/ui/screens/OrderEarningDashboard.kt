package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AmazonOrange
import com.example.ui.theme.BentoAccentButton
import com.example.ui.theme.BentoBg
import com.example.ui.theme.BentoCardBorder
import com.example.ui.theme.BentoDarkPurpleText
import com.example.ui.theme.BentoPrimaryContainer
import com.example.ui.theme.BentoSecondaryContainer
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.MeeshoPink
import com.example.ui.theme.MyntraCoral
import com.example.ui.viewmodel.MainViewModel

@Composable
fun OrderEarningDashboard(
    viewModel: MainViewModel,
    onCategoryClick: (categoryId: String) -> Unit,
    onWithdrawClick: () -> Unit,
    onAdminClick: () -> Unit,
    onHistoryClick: () -> Unit = {}
) {
    val user by viewModel.currentUser.collectAsState()
    val orders by viewModel.userOrders.collectAsState()
    val withdrawals by viewModel.userWithdrawals.collectAsState()
    val isAdminMode by viewModel.isAdminMode.collectAsState()

    val balance = user?.balance ?: 0.0
    val pendingCount = orders.count { it.status == "Reward Processing" }
    val pendingWithdrawals = withdrawals.count { it.status == "Pending" }
    val totalEarned = user?.totalEarned ?: 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BentoBg)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Oder Earning",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = BentoDarkPurpleText
                    )
                )
                Text(
                    text = "AFFILIATE REWARD DASHBOARD",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF49454F),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }

            Row {
                IconButton(
                    onClick = onAdminClick,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isAdminMode) Color(0xFFE8DEF8) else Color.White)
                        .border(1.dp, BentoCardBorder, RoundedCornerShape(14.dp))
                        .testTag("home_admin_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin Panel",
                        tint = BentoPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onHistoryClick,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .border(1.dp, BentoCardBorder, RoundedCornerShape(14.dp))
                        .testTag("home_notifications_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = BentoDarkPurpleText,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Hero Bento Balance Card
        Card(
            onClick = onWithdrawClick,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("home_balance_card"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = BentoPrimaryContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp)
            ) {
                Text(
                    text = "Available Wallet Balance",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Medium
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "₹${String.format("%.2f", balance)}",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontSize = 38.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                            .clickable { onHistoryClick() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "$pendingCount Orders Pending",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }

                    Button(
                        onClick = onWithdrawClick,
                        modifier = Modifier.testTag("home_withdraw_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BentoAccentButton)
                    ) {
                        Text(
                            text = "Withdraw",
                            color = BentoDarkPurpleText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Select Marketplace Category",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BentoDarkPurpleText
            )
        )

        Text(
            text = "Open affiliate links from Flipkart, Meesho, Amazon, or Myntra to earn rewards",
            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 12.sp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 2x2 Bento Grid for Categories
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Flipkart Card
                BentoCategoryCard(
                    title = "Order Flipkart",
                    rewardText = "Earn ₹4 to ₹100",
                    brandColor = FlipkartBlue,
                    drawableRes = R.drawable.cat_flipkart_1786473861983,
                    tag = "cat_flipkart",
                    modifier = Modifier.weight(1f),
                    onClick = { onCategoryClick("flipkart") }
                )

                // Meesho Card
                BentoCategoryCard(
                    title = "Order Meesho",
                    rewardText = "Earn ₹4 to ₹100",
                    brandColor = MeeshoPink,
                    drawableRes = R.drawable.cat_meesho_1786473875865,
                    tag = "cat_meesho",
                    modifier = Modifier.weight(1f),
                    onClick = { onCategoryClick("meesho") }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Amazon Card
                BentoCategoryCard(
                    title = "Amazon",
                    rewardText = "Earn ₹4 to ₹100",
                    brandColor = AmazonOrange,
                    drawableRes = R.drawable.cat_amazon_1786473888293,
                    tag = "cat_amazon",
                    modifier = Modifier.weight(1f),
                    onClick = { onCategoryClick("amazon") }
                )

                // Order Mantra Card
                BentoCategoryCard(
                    title = "Order Mantra",
                    rewardText = "Earn ₹4 to ₹100",
                    brandColor = MyntraCoral,
                    drawableRes = R.drawable.cat_myntra_1786473901623,
                    tag = "cat_myntra",
                    modifier = Modifier.weight(1f),
                    onClick = { onCategoryClick("myntra") }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Summary Bento Grid Section (Recent Rewards & Pending Withdrawals)
        Text(
            text = "Earnings & Activity Summary",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BentoDarkPurpleText
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Total Cashback Earned Card
            Card(
                onClick = onHistoryClick,
                modifier = Modifier
                    .weight(1f)
                    .testTag("summary_total_earned_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE8F5E9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = "Earned",
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Total Earned",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BentoDarkPurpleText
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "₹${String.format("%.2f", totalEarned)}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF2E7D32)
                        )
                    )
                    Text(
                        text = "Lifetime Verified",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontSize = 10.sp)
                    )
                }
            }

            // Pending Withdrawals Card
            Card(
                onClick = onWithdrawClick,
                modifier = Modifier
                    .weight(1f)
                    .testTag("summary_pending_payouts_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFF3E0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.HourglassTop,
                                contentDescription = "Pending",
                                tint = Color(0xFFE65100),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pending Payouts",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BentoDarkPurpleText
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$pendingWithdrawals Requests",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFE65100)
                        )
                    )
                    Text(
                        text = "UPI / Bank Transfer",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontSize = 10.sp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Reward Rules Card
        Card(
            onClick = onHistoryClick,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("reward_rules_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BentoSecondaryContainer)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(BentoPrimaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Reward Rules & Policy",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = BentoDarkPurpleText
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "• ₹100 to ₹999 verified delivered order = ₹4 Reward\n• ₹1000 or more verified delivered order = ₹100 Reward",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = BentoDarkPurpleText.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}
