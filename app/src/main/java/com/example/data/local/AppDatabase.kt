package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.CategoryDao
import com.example.data.local.dao.MarketplaceLinkDao
import com.example.data.local.dao.OrderDao
import com.example.data.local.dao.SupportTicketDao
import com.example.data.local.dao.UserDao
import com.example.data.local.dao.WalletTransactionDao
import com.example.data.local.dao.WithdrawalRequestDao
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.MarketplaceLinkEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.SupportTicketEntity
import com.example.data.local.entity.TicketReplyEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.WalletTransactionEntity
import com.example.data.local.entity.WithdrawalRequestEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        CategoryEntity::class,
        MarketplaceLinkEntity::class,
        OrderEntity::class,
        WalletTransactionEntity::class,
        WithdrawalRequestEntity::class,
        SupportTicketEntity::class,
        TicketReplyEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun marketplaceLinkDao(): MarketplaceLinkDao
    abstract fun orderDao(): OrderDao
    abstract fun walletTransactionDao(): WalletTransactionDao
    abstract fun withdrawalRequestDao(): WithdrawalRequestDao
    abstract fun supportTicketDao(): SupportTicketDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "oder_earning_db"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate seed default categories and marketplace links
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                seedInitialData(database)
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedInitialData(db: AppDatabase) {
            val defaultCategories = listOf(
                CategoryEntity(
                    id = "flipkart",
                    name = "Order Flipkart",
                    slug = "flipkart",
                    description = "Shop electronics, fashion & appliances on Flipkart",
                    brandColorHex = "#2874F0",
                    iconName = "shopping_bag",
                    rewardNotice = "₹4 on orders ₹100–₹999 | ₹100 on orders ₹1000+"
                ),
                CategoryEntity(
                    id = "meesho",
                    name = "Order Meesho",
                    slug = "meesho",
                    description = "Shop lowest price fashion & home goods on Meesho",
                    brandColorHex = "#E52D87",
                    iconName = "checkroom",
                    rewardNotice = "₹4 on orders ₹100–₹999 | ₹100 on orders ₹1000+"
                ),
                CategoryEntity(
                    id = "amazon",
                    name = "Amazon",
                    slug = "amazon",
                    description = "Shop everything from books to tech on Amazon India",
                    brandColorHex = "#FF9900",
                    iconName = "inventory_2",
                    rewardNotice = "₹4 on orders ₹100–₹999 | ₹100 on orders ₹1000+"
                ),
                CategoryEntity(
                    id = "myntra",
                    name = "Order Mantra",
                    slug = "myntra",
                    description = "Shop top branded clothing & footwear on Myntra",
                    brandColorHex = "#FF3F6C",
                    iconName = "sticker",
                    rewardNotice = "₹4 on orders ₹100–₹999 | ₹100 on orders ₹1000+"
                )
            )
            db.categoryDao().insertCategories(defaultCategories)

            val initialLinks = listOf(
                MarketplaceLinkEntity(
                    categoryId = "flipkart",
                    title = "Flipkart Mega Deals & Tech Sale",
                    targetUrl = "https://www.flipkart.com",
                    cashbackOfferText = "Earn up to ₹100 Cashback",
                    termsAndConditions = "Must complete purchase via link. Order value ₹100+ required."
                ),
                MarketplaceLinkEntity(
                    categoryId = "meesho",
                    title = "Meesho Weekly Fashion & Home Bazaar",
                    targetUrl = "https://www.meesho.com",
                    cashbackOfferText = "Earn up to ₹100 Cashback",
                    termsAndConditions = "Reward verified upon order delivery confirmation."
                ),
                MarketplaceLinkEntity(
                    categoryId = "amazon",
                    title = "Amazon Great Indian Deals",
                    targetUrl = "https://www.amazon.in",
                    cashbackOfferText = "Earn up to ₹100 Cashback",
                    termsAndConditions = "Valid for genuine orders delivered without cancellation or return."
                ),
                MarketplaceLinkEntity(
                    categoryId = "myntra",
                    title = "Order Mantra Brand Fashion Festive Sale",
                    targetUrl = "https://www.myntra.com",
                    cashbackOfferText = "Earn up to ₹100 Cashback",
                    termsAndConditions = "Verified after 15-day return window completion."
                )
            )
            for (link in initialLinks) {
                db.marketplaceLinkDao().insertLink(link)
            }
        }
    }
}
