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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.PantryItem
import com.example.ui.KitchenViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantryScreen(viewModel: KitchenViewModel) {
    val pantry by viewModel.pantry.collectAsState()
    val aiGeneratedRecipe by viewModel.generatedRecipeResult.collectAsState()
    val aiGeneratedLoading by viewModel.generatedRecipeLoading.collectAsState()

    var showsAddDialog by remember { mutableStateOf(false) }
    var showsAiResultDialog by remember { mutableStateOf(false) }

    // Form states
    var itemName by remember { mutableStateOf("") }
    var itemCategory by remember { mutableStateOf("Grains") }
    var itemQty by remember { mutableStateOf("500") }
    var itemUnit by remember { mutableStateOf("g") }
    var itemExpiryDays by remember { mutableStateOf("30") }

    val categories = listOf("Grains", "Vegetables", "Meat", "Dairy", "Baking", "Pantry", "Produce")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("STORES & INVENTORY HUB", fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = FontFamily.Monospace, letterSpacing = 0.5.sp) },
                actions = {
                    Button(
                        onClick = { showsAddDialog = true },
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        modifier = Modifier.padding(end = 8.dp).testTag("add_item_stock_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Item", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("LOG STOCK", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // INTEL INGREDIENTS SIEVE ACTION CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp).testTag("ai_pantry_sieve_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CULINARY INTELLIGENCE: ASSETS SIEVE",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "The AI sieve reads active pantry levels and constructs custom historic/progressive recipes to optimize household consumption.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (aiGeneratedLoading) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("COMPUTING HARVEST COEFFICIENTS...", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        }
                    } else {
                        Button(
                            onClick = {
                                viewModel.generateRecipeFromMyPantry()
                                showsAiResultDialog = true
                            },
                            modifier = Modifier.fillMaxWidth().testTag("launch_ai_sieve_btn"),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Settings, contentDescription = "Sieve Icon")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("RUN PANTRY SIEVE BLUEPRINT", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "CURRENT STORES LEDGER",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(4.dp))

            // INVENTORY LIST
            if (pantry.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "STORES ARE DEPLETED. PLOT PROVISIONS NOW.",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(pantry) { item ->
                        var consumeAmountStr by remember { mutableStateOf("") }
                        
                        Card(
                            modifier = Modifier.fillMaxWidth().testTag("inventory_item_${item.id}"),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column {
                                        Text(
                                            text = item.name.uppercase(Locale.ROOT),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "CATEGORY: ${item.category}",
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        
                                        if (item.expiryDate != null) {
                                            val dateFormatted = SimpleDateFormat("dd MMM yyyy", Locale.UK).format(Date(item.expiryDate))
                                            Text(
                                                text = "EXPIRY WARNING: $dateFormatted",
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = if (item.expiryDate < System.currentTimeMillis() + 86400000L * 3) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                    }

                                    // Metric Level Box
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "${item.quantity} ${item.unit}",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Stock manipulation controls
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = consumeAmountStr,
                                        onValueChange = { consumeAmountStr = it },
                                        placeholder = { Text("Qty", fontSize = 11.sp) },
                                        singleLine = true,
                                        modifier = Modifier.width(70.dp).height(44.dp).testTag("consume_qty_${item.id}"),
                                        shape = RoundedCornerShape(2.dp),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
                                    )
                                    
                                    Button(
                                        onClick = {
                                            val amount = consumeAmountStr.toDoubleOrNull() ?: 1.0
                                            viewModel.consumePantryItem(item, amount)
                                            consumeAmountStr = ""
                                        },
                                        shape = RoundedCornerShape(2.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp),
                                        modifier = Modifier.height(32.dp).testTag("consume_submit_${item.id}"),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                    ) {
                                        Text("CONSUME", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                    }

                                    Spacer(modifier = Modifier.weight(1f))

                                    IconButton(
                                        onClick = { viewModel.deletePantryItem(item) },
                                        modifier = Modifier.size(36.dp).testTag("delete_item_${item.id}")
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Decline item from records", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // LOG NEW PANTRY ITEM DIALOG
    if (showsAddDialog) {
        Dialog(onDismissRequest = { showsAddDialog = false }) {
            Surface(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "REGISTER STOCK UNIT",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        label = { Text("Item Name", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("add_stock_name"),
                        shape = RoundedCornerShape(4.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = itemQty,
                            onValueChange = { itemQty = it },
                            label = { Text("Quantity", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f).testTag("add_stock_qty"),
                            shape = RoundedCornerShape(4.dp)
                        )
                        OutlinedTextField(
                            value = itemUnit,
                            onValueChange = { itemUnit = it },
                            label = { Text("Unit (g/kg/ml/cans)", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f).testTag("add_stock_unit"),
                            shape = RoundedCornerShape(4.dp)
                        )
                    }

                    OutlinedTextField(
                        value = itemExpiryDays,
                        onValueChange = { itemExpiryDays = it },
                        label = { Text("Expiry (days from now, e.g. 14)", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("add_stock_expiry"),
                        shape = RoundedCornerShape(4.dp)
                    )

                    Text("Asset Category:", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(categories) { cat ->
                            val isSelected = itemCategory == cat
                            OutlinedButton(
                                onClick = { itemCategory = cat },
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

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showsAddDialog = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("ABORT", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        }
                        Button(
                            onClick = {
                                if (itemName.isNotBlank() && itemQty.toDoubleOrNull() != null) {
                                    viewModel.addPantryItem(
                                        name = itemName,
                                        category = itemCategory,
                                        quantity = itemQty.toDouble(),
                                        unit = itemUnit,
                                        expiryDays = itemExpiryDays.toIntOrNull() ?: 14
                                    )
                                    showsAddDialog = false
                                    itemName = ""
                                    itemQty = "500"
                                }
                            },
                            modifier = Modifier.weight(1f).testTag("submit_stock_btn"),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("LOG UNIT", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }

    // AI PANTRY SIEVE RESULT DISPLAY DIALOG
    if (showsAiResultDialog) {
        Dialog(onDismissRequest = { showsAiResultDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "AI HARVEST RECIPE BLUEPRINT",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (aiGeneratedLoading) {
                        Box(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("COMPUTING FORMATION...", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            item {
                                Text(
                                    text = aiGeneratedRecipe,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showsAiResultDialog = false },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text("DISMISS", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                            Button(
                                onClick = {
                                    viewModel.parseAndSaveGeneratedRecipe()
                                    showsAiResultDialog = false
                                },
                                modifier = Modifier.weight(1.5f).testTag("save_ai_recipe_btn"),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text("LOG TO PERMANENT BLUEPRINTS", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }
        }
    }
}
