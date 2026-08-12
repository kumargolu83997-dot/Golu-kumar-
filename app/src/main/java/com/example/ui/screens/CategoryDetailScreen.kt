package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.MarketplaceLinkEntity
import com.example.ui.theme.AmazonOrange
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
fun CategoryDetailScreen(
    categoryId: String,
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    onOrderCreatedNavigate: () -> Unit
) {
    val context = LocalContext.current
    val categories by viewModel.categories.collectAsState()
    val linksState by viewModel.repository.getLinksForCategoryFlow(categoryId).collectAsState(initial = emptyList())

    val category = categories.find { it.id == categoryId }
    val categoryName = category?.name ?: when (categoryId) {
        "flipkart" -> "Order Flipkart"
        "meesho" -> "Order Meesho"
        "amazon" -> "Amazon"
        "myntra" -> "Order Mantra"
        else -> categoryId.replaceFirstChar { it.uppercase() }
    }

    val brandColor = when (categoryId) {
        "flipkart" -> FlipkartBlue
        "meesho" -> MeeshoPink
        "amazon" -> AmazonOrange
        "myntra" -> MyntraCoral
        else -> BentoPrimaryContainer
    }

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
                        .testTag("category_back_button")
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
                        text = categoryName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = BentoDarkPurpleText
                        )
                    )
                    Text(
                        text = "Merchant Affiliate Links & Offers",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 12.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notice Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = brandColor.copy(alpha = 0.1f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, brandColor.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Notice",
                        tint = brandColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "When you open a link below, an order is automatically registered with status 'Reward Processing'.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = BentoDarkPurpleText,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Links List
            if (linksState.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No active links available for $categoryName.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(linksState) { link ->
                        LinkCardItem(
                            link = link,
                            categoryName = categoryName,
                            brandColor = brandColor,
                            onOpenLink = {
                                viewModel.openMarketplaceLink(
                                    categoryId = categoryId,
                                    categoryName = categoryName,
                                    link = link,
                                    onOrderCreated = { order ->
                                        Toast.makeText(
                                            context,
                                            "Order #${order.orderNumber} created! Status: Reward Processing",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        // Launch browser or URL
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link.targetUrl))
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            // Fallback if no browser
                                        }

                                        onOrderCreatedNavigate()
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LinkCardItem(
    link: MarketplaceLinkEntity,
    categoryName: String,
    brandColor: Color,
    onOpenLink: () -> Unit
) {
    Card(
        onClick = onOpenLink,
        modifier = Modifier.fillMaxWidth().testTag("link_card_${link.id}"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(brandColor.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = link.cashbackOfferText,
                        color = brandColor,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Active",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Active Offer",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF2E7D32))
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = link.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = BentoDarkPurpleText
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "T&C: ${link.termsAndConditions}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onOpenLink,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("open_link_button_${link.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = brandColor)
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = "Open",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Open Link & Record Order",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
