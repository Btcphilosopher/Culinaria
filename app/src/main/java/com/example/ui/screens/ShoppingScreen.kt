package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Recipe
import com.example.data.ShoppingItem
import com.example.ui.KitchenViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingScreen(viewModel: KitchenViewModel) {
    val shoppingList by viewModel.shoppingList.collectAsState()
    val recipes by viewModel.recipes.collectAsState()

    var selectedRecipeToConvert by remember { mutableStateOf<Recipe?>(null) }
    var recipeDropdownExpanded by remember { mutableStateOf(false) }

    var manualItemName by remember { mutableStateOf("") }
    var manualItemQty by remember { mutableStateOf("1") }
    var manualItemUnit by remember { mutableStateOf("units") }
    var manualItemCategory by remember { mutableStateOf("Pantry") }

    val categories = listOf("Produce", "Meat", "Dairy", "Baking", "Grains", "Pantry")

    val (purchased, pending) = shoppingList.partition { it.isPurchased }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PROVISIONS & CART LOGISTICS", fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = FontFamily.Monospace, letterSpacing = 0.5.sp) },
                actions = {
                    if (purchased.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.clearAllPurchased() },
                            modifier = Modifier.testTag("clear_cart_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Purge Completed Units", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // SECTOR I: CONVERT RECIPE TO CART PROVISIONS
            item {
                Text(
                    text = "SECTOR I: RECIPE-TO-CART INTEGRATION",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(4.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Select an existing recipe blueprint to automatically translate all missing ingredients into your active shopping list provisions.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { recipeDropdownExpanded = true },
                                modifier = Modifier.fillMaxWidth().testTag("recipe_provision_selector"),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = selectedRecipeToConvert?.title?.uppercase(Locale.ROOT) ?: "SELECT RECIPE BLUEPRINT",
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            DropdownMenu(
                                expanded = recipeDropdownExpanded,
                                onDismissRequest = { recipeDropdownExpanded = false }
                            ) {
                                recipes.forEach { r ->
                                    DropdownMenuItem(
                                        text = { Text(r.title, fontSize = 12.sp) },
                                        onClick = {
                                            selectedRecipeToConvert = r
                                            recipeDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        val recipeToConvert = selectedRecipeToConvert
                        if (recipeToConvert != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    viewModel.convertRecipeToShoppingList(recipeToConvert)
                                    selectedRecipeToConvert = null
                                },
                                modifier = Modifier.fillMaxWidth().testTag("execute_cart_conversion_btn"),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Sling to grocery list", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("CONVERT BLUEPRINT MISSING ITEMS", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }

            // SECTOR II: ADVANCED WEEKLY PROVISION PREDICTIONS
            item {
                Text(
                    text = "SECTOR II: PREDICTIVE CONSUMPTION FORECAST",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(4.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.03f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.DateRange, contentDescription = "Trend Icon", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "WEEKLY TELEMETRY TRENDS",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "• Yeast Flour reserves will reach threshold danger levels in 4 days.\n• Butter fat consumption exceeds previous epoch rate by 12%.\n• Preserves jars are stable for the current quarter cycle.",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            // SECTOR III: ACTIVE PROVISIONS ADD FORM
            item {
                Text(
                    text = "SECTOR III: MANUAL UNIT REGISTER",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(4.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = manualItemName,
                            onValueChange = { manualItemName = it },
                            placeholder = { Text("Enter asset name (e.g. Yeast Flour)", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("manual_item_name_inp"),
                            shape = RoundedCornerShape(4.dp)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = manualItemQty,
                                onValueChange = { manualItemQty = it },
                                placeholder = { Text("Qty", fontSize = 12.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("manual_item_qty_inp"),
                                shape = RoundedCornerShape(4.dp)
                            )
                            OutlinedTextField(
                                value = manualItemUnit,
                                onValueChange = { manualItemUnit = it },
                                placeholder = { Text("Unit (g/kg)", fontSize = 12.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("manual_item_unit_inp"),
                                shape = RoundedCornerShape(4.dp)
                            )
                            Button(
                                onClick = {
                                    if (manualItemName.isNotBlank()) {
                                        viewModel.addShoppingItem(
                                            name = manualItemName,
                                            quantity = manualItemQty.toDoubleOrNull() ?: 1.0,
                                            unit = manualItemUnit,
                                            category = manualItemCategory
                                        )
                                        manualItemName = ""
                                        manualItemQty = "1"
                                    }
                                },
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.height(40.dp).testTag("add_manual_item_btn")
                            ) {
                                Text("LOG", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                        }

                        // Categories selector row
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            items(categories) { cat ->
                                val isSelected = manualItemCategory == cat
                                OutlinedButton(
                                    onClick = { manualItemCategory = cat },
                                    shape = RoundedCornerShape(4.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
                                        contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                ) {
                                    Text(cat.uppercase(Locale.ROOT), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    }
                }
            }

            // SECTOR IV: PROVISION LEDGER ITEMS
            item {
                Text(
                    text = "SECTOR IV: PROVISIONS LEDGER",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            if (shoppingList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "NO PENDING REQUIREMENT DETECTED.",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            } else {
                // Render Pending Items
                item {
                    Text(
                        text = "PENDING SHIPMENTS (${pending.size})",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                items(pending) { item ->
                    ShoppingRowItem(item, viewModel)
                }

                // Render Completed Items
                if (purchased.isNotEmpty()) {
                    item {
                        Text(
                            text = "STOCKED / RECONCILED (${purchased.size})",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }

                    items(purchased) { item ->
                        ShoppingRowItem(item, viewModel)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun ShoppingRowItem(item: ShoppingItem, viewModel: KitchenViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("shopping_item_${item.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Checkbox(
                    checked = item.isPurchased,
                    onCheckedChange = { viewModel.toggleShoppingItemPurchased(item) },
                    modifier = Modifier.testTag("shopping_checkbox_${item.id}")
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = item.name.uppercase(Locale.ROOT),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isPurchased) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "CAT: ${item.category}",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${item.quantity} ${item.unit}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 8.dp)
                )

                IconButton(
                    onClick = { viewModel.deleteShoppingItem(item) },
                    modifier = Modifier.size(36.dp).testTag("delete_shopping_${item.id}")
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Decline item", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
