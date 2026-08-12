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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.SupportTicketEntity
import com.example.data.local.entity.TicketReplyEntity
import com.example.ui.theme.BentoBg
import com.example.ui.theme.BentoCardBorder
import com.example.ui.theme.BentoDarkPurpleText
import com.example.ui.theme.BentoPrimaryContainer
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportTicketScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val tickets by viewModel.userTickets.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedTicket by remember { mutableStateOf<SupportTicketEntity?>(null) }

    // Form states
    var categoryText by remember { mutableStateOf("Missing Reward") }
    var subjectText by remember { mutableStateOf("") }
    var messageText by remember { mutableStateOf("") }
    var orderIdText by remember { mutableStateOf("") }

    val categories = listOf("Missing Reward", "Withdrawal Issue", "Order Status", "General Query")
    var categoryExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BentoBg
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Customer Care & Support",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = BentoDarkPurpleText
                    )
                )

                Text(
                    text = "Create support tickets and track resolution status",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedTicket != null) {
                    // Ticket Thread View
                    TicketDetailThreadView(
                        ticket = selectedTicket!!,
                        viewModel = viewModel,
                        onBack = { selectedTicket = null }
                    )
                } else {
                    if (showCreateDialog) {
                        // Create Ticket Form
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "Create Support Ticket",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = BentoDarkPurpleText
                                    )
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                ExposedDropdownMenuBox(
                                    expanded = categoryExpanded,
                                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                                ) {
                                    OutlinedTextField(
                                        value = categoryText,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Category") },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    ExposedDropdownMenu(
                                        expanded = categoryExpanded,
                                        onDismissRequest = { categoryExpanded = false }
                                    ) {
                                        categories.forEach { item ->
                                            DropdownMenuItem(
                                                text = { Text(item) },
                                                onClick = {
                                                    categoryText = item
                                                    categoryExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = subjectText,
                                    onValueChange = { subjectText = it },
                                    label = { Text("Subject / Issue Title") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("ticket_subject_input"),
                                    shape = RoundedCornerShape(14.dp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = orderIdText,
                                    onValueChange = { orderIdText = it },
                                    label = { Text("Related Order ID (Optional e.g. ORD-123456)") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = messageText,
                                    onValueChange = { messageText = it },
                                    label = { Text("Describe your issue in detail...") },
                                    minLines = 3,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("ticket_message_input"),
                                    shape = RoundedCornerShape(14.dp)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Button(
                                        onClick = { showCreateDialog = false },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                                    ) {
                                        Text("Cancel", color = Color.Black)
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Button(
                                        onClick = {
                                            if (subjectText.isNotEmpty() && messageText.isNotEmpty()) {
                                                viewModel.createTicket(
                                                    category = categoryText,
                                                    subject = subjectText,
                                                    message = messageText,
                                                    orderId = orderIdText.ifEmpty { null },
                                                    onCreated = {
                                                        Toast.makeText(context, "Support Ticket Created!", Toast.LENGTH_SHORT).show()
                                                        subjectText = ""
                                                        messageText = ""
                                                        orderIdText = ""
                                                        showCreateDialog = false
                                                    }
                                                )
                                            }
                                        },
                                        modifier = Modifier.testTag("submit_ticket_button"),
                                        colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryContainer)
                                    ) {
                                        Text("Submit Ticket")
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Ticket List
                    if (tickets.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.SupportAgent,
                                    contentDescription = "Support",
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No support tickets submitted.",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(tickets) { ticket ->
                                SupportTicketCardItem(
                                    ticket = ticket,
                                    onClick = { selectedTicket = ticket }
                                )
                            }
                        }
                    }
                }
            }

            // FAB for creating new ticket
            if (selectedTicket == null && !showCreateDialog) {
                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .testTag("create_ticket_fab"),
                    containerColor = BentoPrimaryContainer,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "New Ticket")
                }
            }
        }
    }
}

@Composable
fun SupportTicketCardItem(
    ticket: SupportTicketEntity,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val dateStr = dateFormat.format(Date(ticket.updatedAt))

    val (statusColor, statusBg) = when (ticket.status) {
        "Closed" -> Pair(Color.Gray, Color(0xFFEEEEEE))
        "Replied" -> Pair(Color(0xFF2E7D32), Color(0xFFE8F5E9))
        else -> Pair(Color(0xFFE65100), Color(0xFFFFF3E0))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ticket_card_${ticket.id}")
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ticket.ticketNumber,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = BentoDarkPurpleText
                    )
                )

                Box(
                    modifier = Modifier
                        .background(statusBg, RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = ticket.status,
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = ticket.subject,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = BentoDarkPurpleText
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${ticket.category} • Updated $dateStr",
                style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontSize = 11.sp)
            )
        }
    }
}

@Composable
fun TicketDetailThreadView(
    ticket: SupportTicketEntity,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val replies by viewModel.getTicketReplies(ticket.id).collectAsState()
    var replyMsgText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                Text("< Back to List", color = BentoDarkPurpleText)
            }

            if (ticket.status != "Closed") {
                Button(
                    onClick = {
                        viewModel.closeTicket(ticket.id)
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                ) {
                    Text("Close Ticket")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "${ticket.ticketNumber}: ${ticket.subject}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Category: ${ticket.category} • Status: ${ticket.status}",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(replies) { reply ->
                ReplyBubbleItem(reply = reply)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (ticket.status != "Closed") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = replyMsgText,
                    onValueChange = { replyMsgText = it },
                    placeholder = { Text("Type reply...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (replyMsgText.isNotEmpty()) {
                            viewModel.replyTicket(
                                ticketId = ticket.id,
                                senderRole = "USER",
                                senderName = "Customer",
                                message = replyMsgText
                            )
                            replyMsgText = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(BentoPrimaryContainer, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ReplyBubbleItem(reply: TicketReplyEntity) {
    val isAdmin = reply.senderRole == "ADMIN"
    val align = if (isAdmin) Alignment.Start else Alignment.End
    val bgCol = if (isAdmin) Color(0xFFEADDFF) else BentoPrimaryContainer
    val txtCol = if (isAdmin) BentoDarkPurpleText else Color.White

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = align
    ) {
        Text(
            text = "${reply.senderName} (${reply.senderRole})",
            style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontSize = 10.sp)
        )
        Box(
            modifier = Modifier
                .background(bgCol, RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            Text(
                text = reply.message,
                color = txtCol,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
