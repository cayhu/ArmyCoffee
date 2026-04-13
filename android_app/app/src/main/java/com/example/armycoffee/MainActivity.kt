package com.example.armycoffee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.armycoffee.ui.screens.*
import com.example.armycoffee.ui.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val authViewModel: AuthViewModel = viewModel()
            val currentUser = authViewModel.currentUser

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") { 
                            LoginScreen(
                                viewModel = authViewModel,
                                onLoginSuccess = { 
                                    navController.navigate("menu") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = {
                                    navController.navigate("register")
                                }
                            ) 
                        }
                        composable("register") {
                            RegisterScreen(
                                viewModel = authViewModel,
                                onRegisterSuccess = {
                                    navController.popBackStack()
                                },
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("menu") { 
                            MenuScreen(
                                userRole = currentUser?.role ?: "STAFF",
                                onNavigateToOrders = { navController.navigate("orders") },
                                onNavigateToManagement = { navController.navigate("management") },
                                onLogout = {
                                    authViewModel.logout()
                                    navController.navigate("login") {
                                        popUpTo("menu") { inclusive = true }
                                    }
                                }
                            ) 
                        }
                        composable("orders") {
                            OrdersScreen(
                                onNavigateToMenu = { navController.navigate("menu") },
                                onNavigateToManagement = { navController.navigate("management") },
                                onLogout = {
                                    authViewModel.logout()
                                    navController.navigate("login") {
                                        popUpTo("orders") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("management") {
                            ManagementScreen(
                                userRole = currentUser?.role ?: "STAFF",
                                onNavigateToMenu = { navController.navigate("menu") },
                                onNavigateToOrders = { navController.navigate("orders") },
                                onLogout = {
                                    authViewModel.logout()
                                    navController.navigate("login") {
                                        popUpTo("management") { inclusive = true }
                                    }
                                },
                                onNavigateToProducts = { navController.navigate("products") },
                                onNavigateToCategories = { navController.navigate("categories") },
                                onNavigateToReports = { navController.navigate("reports") },
                                onNavigateToEmployees = { navController.navigate("employees") },
                                onNavigateToSettings = { navController.navigate("settings") },
                                onNavigateToInventory = { navController.navigate("inventory_check") },
                                onNavigateToPriceSettings = { navController.navigate("price_settings") }
                            )
                        }
                        composable("products") {
                            ProductManagementScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("categories") {
                            CategoryManagementScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("reports") {
                            ReportScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("employees") {
                            EmployeeManagementScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("settings") {
                            StoreSettingsScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("inventory_check") {
                            InventoryCheckScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("price_settings") {
                            PriceSettingsScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
