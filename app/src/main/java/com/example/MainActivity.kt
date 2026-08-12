package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.CategoryDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.OrdersHistoryScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SupportTicketScreen
import com.example.ui.screens.WithdrawalScreen
import com.example.ui.theme.BentoBg
import com.example.ui.theme.BentoCardBorder
import com.example.ui.theme.BentoDarkPurpleText
import com.example.ui.theme.BentoPrimaryContainer
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                OrderEarningApp(viewModel = mainViewModel)
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector?) {
    object Login : Screen("login", "Login", null)
    object Home : Screen("home", "Home", Icons.Default.Home)
    object CategoryDetail : Screen("category/{categoryId}", "Category", null) {
        fun createRoute(categoryId: String) = "category/$categoryId"
    }
    object OrdersHistory : Screen("orders", "History", Icons.Default.ReceiptLong)
    object Withdrawal : Screen("withdrawal", "Withdrawal", null)
    object Support : Screen("support", "Support", Icons.Default.SupportAgent)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object AdminPanel : Screen("admin", "Admin", null)
}

@Composable
fun OrderEarningApp(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val authState by viewModel.authUiState.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        Screen.Home,
        Screen.OrdersHistory,
        Screen.Support,
        Screen.Profile
    )

    val showBottomBar = authState.isLoggedIn && currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BentoBg,
        bottomBar = {
            if (showBottomBar) {
                BentoBottomBar(navController = navController, items = bottomNavItems, currentRoute = currentRoute)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onCategoryClick = { categoryId ->
                        navController.navigate(Screen.CategoryDetail.createRoute(categoryId))
                    },
                    onWithdrawClick = {
                        navController.navigate(Screen.Withdrawal.route)
                    },
                    onAdminClick = {
                        navController.navigate(Screen.AdminPanel.route)
                    },
                    onHistoryClick = {
                        navController.navigate(Screen.OrdersHistory.route)
                    }
                )
            }

            composable(
                route = Screen.CategoryDetail.route,
                arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
            ) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getString("categoryId") ?: "flipkart"
                CategoryDetailScreen(
                    categoryId = categoryId,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onOrderCreatedNavigate = {
                        navController.navigate(Screen.OrdersHistory.route)
                    }
                )
            }

            composable(Screen.OrdersHistory.route) {
                OrdersHistoryScreen(viewModel = viewModel)
            }

            composable(Screen.Withdrawal.route) {
                WithdrawalScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Support.route) {
                SupportTicketScreen(viewModel = viewModel)
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel,
                    onWithdrawClick = { navController.navigate(Screen.Withdrawal.route) },
                    onAdminClick = { navController.navigate(Screen.AdminPanel.route) }
                )
            }

            composable(Screen.AdminPanel.route) {
                AdminPanelScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun BentoBottomBar(
    navController: NavHostController,
    items: List<Screen>,
    currentRoute: String?
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(Color.White)
            .border(1.dp, BentoCardBorder, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        items.forEach { screen ->
            val isSelected = currentRoute == screen.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    screen.icon?.let { icon ->
                        Icon(
                            imageVector = icon,
                            contentDescription = screen.title,
                            tint = if (isSelected) BentoPrimaryContainer else Color(0xFF49454F).copy(alpha = 0.6f)
                        )
                    }
                },
                label = {
                    Text(
                        text = screen.title,
                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp,
                            color = if (isSelected) BentoPrimaryContainer else Color(0xFF49454F).copy(alpha = 0.6f)
                        )
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = BentoPrimaryContainer.copy(alpha = 0.12f)
                ),
                modifier = Modifier.testTag("nav_item_${screen.route}")
            )
        }
    }
}
