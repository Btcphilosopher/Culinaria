package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.KitchenViewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppHost()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppHost() {
    val viewModel: KitchenViewModel = viewModel()
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    val currentProfile by viewModel.currentProfile.collectAsState()
    
    // Bottom navigation index trackers
    var activeTabRoute by remember { mutableStateOf("dashboard") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        modifier = Modifier.testTag("app_navigation_drawer"),
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp),
                drawerShape = RoundedCornerShape(0.dp) // Industrial sharp slate
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "CULINARIA OS",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "MASTER CONSOLE DRIVER",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary Pages inside Hamburger Drawer
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profiles Core") },
                        label = { Text("OPERATOR PROFILES", fontSize = 13.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) },
                        selected = activeTabRoute == "profiles",
                        onClick = {
                            activeTabRoute = "profiles"
                            scope.launch { drawerState.close() }
                            navController.navigate("profiles") {
                                popUpTo("dashboard") { saveState = true }
                                launchSingleTop = true
                            }
                        },
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.testTag("drawer_profiles_btn")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Manuals Core") },
                        label = { Text("TECHNICAL MANUALS", fontSize = 13.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) },
                        selected = activeTabRoute == "encyclopedia",
                        onClick = {
                            activeTabRoute = "encyclopedia"
                            scope.launch { drawerState.close() }
                            navController.navigate("encyclopedia") {
                                popUpTo("dashboard") { saveState = true }
                                launchSingleTop = true
                            }
                        },
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.testTag("drawer_encyclopedia_btn")
                    )

                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Driver warning
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                "NOMINAL MATRIX STABLE",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "Profile: ${currentProfile?.name ?: "UNKNOWN"}",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                // We let each screen hold their personalized topBar, but if we need a drawer trigger:
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "CULINARIA OS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.testTag("drawer_trigger")
                        ) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Open operating deck")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.testTag("bottom_navigation_bar"),
                    tonalElevation = 8.dp
                ) {
                    // TAB 1: DASHBOARD
                    NavigationBarItem(
                        selected = activeTabRoute == "dashboard",
                        onClick = {
                            activeTabRoute = "dashboard"
                            navController.navigate("dashboard") {
                                popUpTo("dashboard") { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard Core") },
                        label = { Text("DASH", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("tab_dashboard")
                    )

                    // TAB 2: RECIPE LIBRARY
                    NavigationBarItem(
                        selected = activeTabRoute == "recipes",
                        onClick = {
                            activeTabRoute = "recipes"
                            navController.navigate("recipes") {
                                popUpTo("dashboard") { saveState = true }
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(Icons.Default.List, contentDescription = "Recipe Core") },
                        label = { Text("RECIPES", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("tab_recipes")
                    )

                    // TAB 3: GUIDED COOKING
                    NavigationBarItem(
                        selected = activeTabRoute == "cook",
                        onClick = {
                            activeTabRoute = "cook"
                            navController.navigate("cook") {
                                popUpTo("dashboard") { saveState = true }
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Cooking Core") },
                        label = { Text("COOK", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("tab_cook")
                    )

                    // TAB 4: MEAL SCHEDULER
                    NavigationBarItem(
                        selected = activeTabRoute == "plan",
                        onClick = {
                            activeTabRoute = "plan"
                            navController.navigate("plan") {
                                popUpTo("dashboard") { saveState = true }
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(Icons.Default.DateRange, contentDescription = "Scheduler Core") },
                        label = { Text("PLAN", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("tab_plan")
                    )

                    // TAB 5: PANTRY / STORES (Double pane toggled screen)
                    NavigationBarItem(
                        selected = activeTabRoute == "stores",
                        onClick = {
                            activeTabRoute = "stores"
                            navController.navigate("stores") {
                                popUpTo("dashboard") { saveState = true }
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Stores Core") },
                        label = { Text("STORES", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("tab_stores")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                NavHost(
                    navController = navController,
                    startDestination = "dashboard",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("dashboard") {
                        KitchenDashboardScreen(
                            viewModel = viewModel,
                            onNavigateToRecipe = { recipe ->
                                viewModel.startCooking(recipe)
                                activeTabRoute = "cook"
                                navController.navigate("cook") {
                                    popUpTo("dashboard") { saveState = true }
                                    launchSingleTop = true
                                }
                            },
                            onNavigateToPantry = {
                                activeTabRoute = "stores"
                                navController.navigate("stores") {
                                    popUpTo("dashboard") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    composable("recipes") {
                        RecipeLibraryScreen(
                            viewModel = viewModel,
                            onSelectRecipe = { recipe ->
                                viewModel.startCooking(recipe)
                                activeTabRoute = "cook"
                                navController.navigate("cook") {
                                    popUpTo("dashboard") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    composable("cook") {
                        GuidedCookingScreen(
                            viewModel = viewModel,
                            onNavigateBack = {
                                activeTabRoute = "recipes"
                                navController.navigate("recipes") {
                                    popUpTo("dashboard") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    composable("plan") {
                        MealPlanningScreen(
                            viewModel = viewModel,
                            onNavigateToRecipe = { recipe ->
                                viewModel.startCooking(recipe)
                                activeTabRoute = "cook"
                                navController.navigate("cook") {
                                    popUpTo("dashboard") { saveState = true }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    composable("stores") {
                        // Double pane showing both Pantry Stores and Provisions list
                        var selectedStoresPane by remember { mutableStateOf("pantry") }
                        
                        Column(modifier = Modifier.fillMaxSize()) {
                            TabRow(selectedTabIndex = if (selectedStoresPane == "pantry") 0 else 1) {
                                Tab(
                                    selected = selectedStoresPane == "pantry",
                                    onClick = { selectedStoresPane = "pantry" },
                                    modifier = Modifier.testTag("subtab_pantry")
                                ) {
                                    Text("PANTRY STOCK", modifier = Modifier.padding(12.dp), fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                }
                                Tab(
                                    selected = selectedStoresPane == "cart",
                                    onClick = { selectedStoresPane = "cart" },
                                    modifier = Modifier.testTag("subtab_cart")
                                ) {
                                    Text("CART PROVISIONS", modifier = Modifier.padding(12.dp), fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                }
                            }
                            
                            Box(modifier = Modifier.weight(1f)) {
                                if (selectedStoresPane == "pantry") {
                                    PantryScreen(viewModel = viewModel)
                                } else {
                                    ShoppingScreen(viewModel = viewModel)
                                }
                            }
                        }
                    }

                    composable("profiles") {
                        HouseholdProfilesScreen(viewModel = viewModel)
                    }

                    composable("encyclopedia") {
                        CulinaryKnowledgeScreen()
                    }
                }
            }
        }
    }
}
