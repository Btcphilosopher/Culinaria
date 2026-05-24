package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.window.Dialog
import com.example.data.Recipe
import com.example.ui.KitchenViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeLibraryScreen(
    viewModel: KitchenViewModel,
    onSelectRecipe: (Recipe) -> Unit
) {
    val recipes by viewModel.recipes.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var showsAddDialog by remember { mutableStateOf(false) }
    var inspectingRecipe by remember { mutableStateOf<Recipe?>(null) }

    val categories = listOf(
        "All", "Supper", "Baking", "Roasts", "Preserves", 
        "Breakfast", "Tea & Desserts", "Regional British", "Anglo-Futurist Experimental"
    )

    // Form states
    var formTitle by remember { mutableStateOf("") }
    var formOverview by remember { mutableStateOf("") }
    var formCategory by remember { mutableStateOf("Supper") }
    var formPrep by remember { mutableStateOf("15") }
    var formCook by remember { mutableStateOf("20") }
    var formDifficulty by remember { mutableStateOf("Standard") }
    var formEquip by remember { mutableStateOf("") }
    var formIngredients by remember { mutableStateOf("") }
    var formSteps by remember { mutableStateOf("") }
    var formNotes by remember { mutableStateOf("") }

    val filteredRecipes = recipes.filter { recipe ->
        val matchesSearch = recipe.title.contains(searchQuery, ignoreCase = true) ||
                recipe.overview.contains(searchQuery, ignoreCase = true) ||
                recipe.ingredientsList.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || recipe.category == selectedCategory
        matchesSearch && matchesCategory
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CORE RECIPE BLUEPRINTS", fontWeight = FontWeight.Bold, fontSize = 16.sp, letterSpacing = 1.sp, fontFamily = FontFamily.Monospace) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showsAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_recipe_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Recipe Blueprint")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // SEARCH COMMAND LINE
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("recipe_search_input"),
                placeholder = { Text("Enter recipe keyword or ingredient...", fontSize = 13.sp) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search icon") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // CATEGORY SCROLL FILTER
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    Button(
                        onClick = { selectedCategory = category },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(32.dp).testTag("category_pill_$category")
                    ) {
                        Text(
                            text = category.uppercase(Locale.ROOT),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // GRID / LIST OF BLUEPRINTS
            if (filteredRecipes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "NO COMPATIBLE CULINARY BLUEPRINTS SECURED.",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredRecipes) { recipe ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { inspectingRecipe = recipe }
                                .testTag("recipe_item_card_${recipe.id}"),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = recipe.category.uppercase(Locale.ROOT),
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Badge(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)) {
                                        Text(
                                            text = recipe.difficulty.uppercase(Locale.ROOT),
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = recipe.title,
                                    fontSize = 20.sp,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = recipe.overview,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Text(
                                        text = "⚡ PREP: ${recipe.prepTimeMinutes} MIN",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "🔥 COOK: ${recipe.cookTimeMinutes} MIN",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "🍽️ YIELD: ${recipe.servings} SERVING",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ADD NEW RECIPE BLUEPRINT DIALOG
    if (showsAddDialog) {
        Dialog(onDismissRequest = { showsAddDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "NEW CULINARY BLUEPRINT FORM",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            OutlinedTextField(
                                value = formTitle,
                                onValueChange = { formTitle = it },
                                label = { Text("Title", fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth().testTag("form_title_inp"),
                                shape = RoundedCornerShape(4.dp)
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = formOverview,
                                onValueChange = { formOverview = it },
                                label = { Text("Short Overview", fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth().testTag("form_overview_inp"),
                                shape = RoundedCornerShape(4.dp)
                            )
                        }
                        item {
                            Text("Category Vector:", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                items(categories.filter { it != "All" }) { cat ->
                                    val isSelected = formCategory == cat
                                    OutlinedButton(
                                        onClick = { formCategory = cat },
                                        shape = RoundedCornerShape(4.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
                                            contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        ),
                                        contentPadding = PaddingValues(horizontal = 8.dp),
                                        border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        Text(cat.uppercase(Locale.ROOT), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }
                        }
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = formPrep,
                                    onValueChange = { formPrep = it },
                                    label = { Text("Prep (mins)", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f).testTag("form_prep_inp"),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                OutlinedTextField(
                                    value = formCook,
                                    onValueChange = { formCook = it },
                                    label = { Text("Cook (mins)", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f).testTag("form_cook_inp"),
                                    shape = RoundedCornerShape(4.dp)
                                )
                            }
                        }
                        item {
                            OutlinedTextField(
                                value = formEquip,
                                onValueChange = { formEquip = it },
                                label = { Text("Equipment (comma separated)", fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth().testTag("form_equip_inp"),
                                shape = RoundedCornerShape(4.dp)
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = formIngredients,
                                onValueChange = { formIngredients = it },
                                label = { Text("Ingredients (Format: Qty | Descriptor)", fontSize = 12.sp) },
                                placeholder = { Text("200g | Flour\n2 | Egg yolks", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth().height(100.dp).testTag("form_ing_inp"),
                                shape = RoundedCornerShape(4.dp),
                                maxLines = 10
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = formSteps,
                                onValueChange = { formSteps = it },
                                label = { Text("Steps (NewLine separated, prefix step numbers)", fontSize = 12.sp) },
                                placeholder = { Text("1. Sift dry elements\n2. Squeeze fats into mix", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth().height(120.dp).testTag("form_steps_inp"),
                                shape = RoundedCornerShape(4.dp),
                                maxLines = 20
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = formNotes,
                                onValueChange = { formNotes = it },
                                label = { Text("Technical / Industrial Notes", fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth().testTag("form_notes_inp"),
                                shape = RoundedCornerShape(4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

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
                                if (formTitle.isNotBlank()) {
                                    viewModel.addNewRecipe(
                                        title = formTitle,
                                        overview = formOverview,
                                        category = formCategory,
                                        prepMins = formPrep.toIntOrNull() ?: 10,
                                        cookMins = formCook.toIntOrNull() ?: 15,
                                        servings = 2,
                                        difficulty = formDifficulty,
                                        equip = formEquip,
                                        ingredients = formIngredients,
                                        steps = formSteps,
                                        notes = formNotes
                                    )
                                    showsAddDialog = false
                                    // Reset fields
                                    formTitle = ""
                                    formOverview = ""
                                    formPrep = "15"
                                    formCook = "20"
                                    formIngredients = ""
                                    formSteps = ""
                                    formNotes = ""
                                }
                            },
                            modifier = Modifier.weight(1f).testTag("submit_recipe_button"),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("LOG BLUEPRINT", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }

    // INTERACTIVE BLUEPRINT INSPECTION DIALOG (WITH HIGH-ACCURACY SCALING)
    val recipeToInspect = inspectingRecipe
    if (recipeToInspect != null) {
        var multiplierServings by remember(recipeToInspect) { mutableStateOf(recipeToInspect.servings) }

        Dialog(onDismissRequest = { inspectingRecipe = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Header Area
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = recipeToInspect.category.uppercase(Locale.ROOT),
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = recipeToInspect.title.uppercase(Locale.ROOT),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(onClick = { inspectingRecipe = null }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Close", modifier = Modifier.size(24.dp).testTag("close_inspector_btn")) // Rotate can represent custom close, using Add but we can just use Back or simple close logic.
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.surfaceVariant)

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Section: Overview
                        item {
                            Text(
                                text = recipeToInspect.overview,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                            )
                        }

                        // Section: Servo Multiplex Slider
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.03f)),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "DYNAMIC SERVING CORRECTION",
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Scales volume ratios in real-time.",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        OutlinedButton(
                                            onClick = { if (multiplierServings > 1) multiplierServings-- },
                                            modifier = Modifier.size(36.dp).testTag("scale_down_servings"),
                                            contentPadding = PaddingValues(0.dp),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text("-", fontWeight = FontWeight.Bold)
                                        }
                                        Text(
                                            text = "$multiplierServings",
                                            modifier = Modifier.padding(horizontal = 12.dp).testTag("scale_servings_label"),
                                            fontSize = 15.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        )
                                        OutlinedButton(
                                            onClick = { if (multiplierServings < 20) multiplierServings++ },
                                            modifier = Modifier.size(36.dp).testTag("scale_up_servings"),
                                            contentPadding = PaddingValues(0.dp),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text("+", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // Section: Equipment Required
                        if (recipeToInspect.equipmentNeeded.isNotBlank()) {
                            item {
                                Text(
                                    text = "EQUIPMENT LOGISTICS REQUIREMENT",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    recipeToInspect.equipmentNeeded.split(",").forEach { equip ->
                                        if (equip.isNotBlank()) {
                                            Badge(
                                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                                modifier = Modifier.padding(2.dp)
                                            ) {
                                                Text(
                                                    text = equip.trim().uppercase(Locale.ROOT),
                                                    fontSize = 8.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.padding(4.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Section: Scaled Ingredients Checklist
                        item {
                            Text(
                                text = "CALCULATED RAW INGREDIENTS REGISTER",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            val ingredientsLines = recipeToInspect.ingredientsList.split("\n").filter { it.isNotBlank() }
                            ingredientsLines.forEach { ing ->
                                val parts = ing.split("|")
                                val displayText = if (parts.size >= 2) {
                                    val qtyPart = parts[0].trim()
                                    val descPart = parts[1].trim()
                                    val numberRegex = """^([0-9\.]+)""".toRegex()
                                    val match = numberRegex.find(qtyPart)
                                    if (match != null) {
                                        val numStr = match.groupValues[1]
                                        val restOfQty = qtyPart.substring(numStr.length)
                                        val baseVal = numStr.toDoubleOrNull() ?: 1.0
                                        val scaledVal = baseVal * (multiplierServings.toDouble() / recipeToInspect.servings.toDouble())
                                        val formattedVal = if (scaledVal % 1.0 == 0.0) scaledVal.toInt().toString() else String.format("%.1f", scaledVal)
                                        "• $formattedVal$restOfQty of $descPart"
                                    } else {
                                        "• $ing"
                                    }
                                } else {
                                    "• $ing"
                                }
                                Text(
                                    text = displayText,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }

                        // Section: Overview Warnings
                        if (recipeToInspect.industrialNotes.isNotEmpty()) {
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.03f)),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            "SAFETY PROTOCOL NOTE",
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            recipeToInspect.industrialNotes,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dialog Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.convertRecipeToShoppingList(recipeToInspect)
                                inspectingRecipe = null
                            },
                            modifier = Modifier.weight(1f).testTag("inspector_to_cart_btn"),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp)
                        ) {
                            Text("CART SYNC", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }

                        Button(
                            onClick = {
                                onSelectRecipe(recipeToInspect)
                                inspectingRecipe = null
                            },
                            modifier = Modifier.weight(1.3f).testTag("inspector_launch_cook_btn"),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("LAUNCH COOKING", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}
