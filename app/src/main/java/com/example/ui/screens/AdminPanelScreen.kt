package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.SupportTicketEntity
import com.example.data.local.entity.WithdrawalRequestEntity
import com.example.ui.theme.BentoBg
import com.example.ui.theme.BentoCardBorder
import com.example.ui.theme.BentoDarkPurpleText
import com.example.ui.theme.BentoPrimaryContainer
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Orders Verification", "Manage Links", "Withdrawals", "Support Tickets")

    val orders by viewModel.allOrders.collectAsState()
    val links by viewModel.allLinks.collectAsState()
    val withdrawals by viewModel.allWithdrawals.collectAsState()
    val tickets by viewModel.allTickets.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BentoBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
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
                        .testTag("admin_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = BentoDarkPurpleText
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Admin Control Panel",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = BentoDarkPurpleText
                        )
                    )
                    Text(
                        text = "Trusted Verification & Management Portal",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 12.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Navigation Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 0.dp,
                containerColor = Color.White,
                contentColor = BentoPrimaryContainer,
                modifier = Modifier.border(1.dp, BentoCardBorder, RoundedCornerShape(16.dp))
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> AdminOrderVerificationTab(orders, viewModel)
                1 -> AdminManageLinksTab(links, viewModel)
                2 -> AdminWithdrawalsTab(withdrawals, viewModel)
                3 -> AdminSupportTicketsTab(tickets, viewModel)
            }
        }
    }
}

@Composable
fun AdminOrderVerificationTab(orders: List<OrderEntity>, viewModel: MainViewModel) {
    val context = LocalContext.current

    if (orders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No customer orders recorded yet.", color = Color.Gray)
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(orders) { order ->
                var amountText by remember { mutableStateOf("") }
                var adminNoteText by remember { mutableStateOf("") }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Order #${order.orderNumber}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BentoDarkPurpleText
                                )
                            )
                            Text(
                                text = order.status,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (order.status == "Received") Color(0xFF2E7D32) else BentoPrimaryContainer
                                )
                            )
                        }

                        Text(
                            text = "Customer: ${order.userId} • Category: ${order.categoryName}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )
                        Text(
                            text = "Link: ${order.linkTitle}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        if (order.status == "Reward Processing") {
                            OutlinedTextField(
                                value = amountText,
                                onValueChange = { amountText = it },
                                label = { Text("Verified Purchase Amount (₹)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_amount_input_${order.id}"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = adminNoteText,
                                onValueChange = { adminNoteText = it },
                                label = { Text("Admin Verification Notes") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.adminRejectOrder(order.id, adminNoteText.ifEmpty { "Order rejected" })
                                        Toast.makeText(context, "Order rejected", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Reject Order")
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = {
                                        val amt = amountText.toDoubleOrNull() ?: 0.0
                                        if (amt > 0) {
                                            viewModel.adminMarkOrderReceived(order.id, amt, adminNoteText) { success ->
                                                if (success) {
                                                    Toast.makeText(context, "Order marked 'Received' & Reward Credited!", Toast.LENGTH_LONG).show()
                                                }
                                            }
                                        } else {
                                            Toast.makeText(context, "Please enter valid order amount", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.testTag("admin_verify_button_${order.id}"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Mark Received & Credit")
                                }
                            }
                        } else {
                            Text(
                                text = "Verified Amount: ₹${order.orderAmount} • Reward Credited: ₹${order.calculatedReward}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminManageLinksTab(links: List<com.example.data.local.entity.MarketplaceLinkEntity>, viewModel: MainViewModel) {
    val context = LocalContext.current
    var showAddForm by remember { mutableStateOf(false) }

    var categoryId by remember { mutableStateOf("flipkart") }
    var title by remember { mutableStateOf("") }
    var targetUrl by remember { mutableStateOf("") }
    var offerText by remember { mutableStateOf("Earn up to ₹100 Reward") }
    var terms by remember { mutableStateOf("Valid for genuine orders.") }

    val categories = listOf("flipkart", "meesho", "amazon", "myntra")
    var catExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = { showAddForm = !showAddForm },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_add_link_toggle"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryContainer)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add")
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (showAddForm) "Hide Add Form" else "Add New Category Link")
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (showAddForm) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = catExpanded,
                        onExpandedChange = { catExpanded = !catExpanded }
                    ) {
                        OutlinedTextField(
                            value = categoryId.uppercase(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = catExpanded,
                            onDismissRequest = { catExpanded = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.uppercase()) },
                                    onClick = {
                                        categoryId = cat
                                        catExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Link Title") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_link_title_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = targetUrl,
                        onValueChange = { targetUrl = it },
                        label = { Text("Target Affiliate URL") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_link_url_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (title.isNotEmpty() && targetUrl.isNotEmpty()) {
                                viewModel.adminAddLink(categoryId, title, targetUrl, offerText, terms) {
                                    Toast.makeText(context, "Category Link Added!", Toast.LENGTH_SHORT).show()
                                    title = ""
                                    targetUrl = ""
                                    showAddForm = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_submit_link_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryContainer)
                    ) {
                        Text("Save Link")
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(links) { link ->
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "[${link.categoryId.uppercase()}] ${link.title}",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BentoDarkPurpleText
                                )
                            )
                            Text(
                                text = link.targetUrl,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.Gray,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        IconButton(onClick = {
                            viewModel.adminDeleteLink(link.id)
                            Toast.makeText(context, "Link deleted", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminWithdrawalsTab(withdrawals: List<WithdrawalRequestEntity>, viewModel: MainViewModel) {
    val context = LocalContext.current

    if (withdrawals.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No withdrawal requests found.", color = Color.Gray)
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(withdrawals) { req ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Req ID: ${req.requestId}",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "₹${req.amount} • ${req.status}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (req.status == "Paid") Color(0xFF2E7D32) else Color(0xFFE65100)
                                )
                            )
                        }

                        Text(
                            text = "Customer: ${req.userId} • Method: ${req.method}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )

                        if (req.method == "UPI") {
                            Text("UPI ID: ${req.upiId}", style = MaterialTheme.typography.bodySmall)
                        } else {
                            Text("Acc: ${req.accountNumber} • IFSC: ${req.ifscCode}", style = MaterialTheme.typography.bodySmall)
                        }

                        if (req.status == "Pending") {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    viewModel.adminApproveWithdrawal(req.id) {
                                        Toast.makeText(context, "Withdrawal marked Paid", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.align(Alignment.End),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Approve & Mark Paid")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminSupportTicketsTab(tickets: List<SupportTicketEntity>, viewModel: MainViewModel) {
    val context = LocalContext.current

    if (tickets.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No support tickets found.", color = Color.Gray)
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(tickets) { tck ->
                var replyText by remember { mutableStateOf("") }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "${tck.ticketNumber} • ${tck.subject}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "From: ${tck.userId} • Category: ${tck.category} • Status: ${tck.status}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )
                        Text(
                            text = "Initial Message: ${tck.initialMessage}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (tck.status != "Closed") {
                            OutlinedTextField(
                                value = replyText,
                                onValueChange = { replyText = it },
                                label = { Text("Admin Reply") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.closeTicket(tck.id)
                                        Toast.makeText(context, "Ticket closed", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                                ) {
                                    Text("Close")
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = {
                                        if (replyText.isNotEmpty()) {
                                            viewModel.replyTicket(tck.id, "ADMIN", "Admin Support", replyText)
                                            Toast.makeText(context, "Admin reply sent", Toast.LENGTH_SHORT).show()
                                            replyText = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryContainer)
                                ) {
                                    Text("Send Reply")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
