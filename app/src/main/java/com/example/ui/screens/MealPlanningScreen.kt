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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
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
import com.example.data.MealPlan
import com.example.data.Recipe
import com.example.ui.KitchenViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlanningScreen(
    viewModel: KitchenViewModel,
    onNavigateToRecipe: (Recipe) -> Unit
) {
    val mealPlans by viewModel.mealPlans.collectAsState()
    val recipes by viewModel.recipes.collectAsState()

    var selectedDayOffset by remember { mutableStateOf(0) }
    var showsAddPlanDialog by remember { mutableStateOf(false) }
    var targetMealType by remember { mutableStateOf("Supper") }

    // Dropdown/Dialog form states
    var selectedRecipeId by remember { mutableIntStateOf(-1) }
    var customPlanningNotes by remember { mutableStateOf("") }
    var recipeExpanded by remember { mutableStateOf(false) }

    // Generate dates for the next 7 days starting from today
    val calendar = Calendar.getInstance()
    val formatDayName = SimpleDateFormat("EEE", Locale.UK)
    val formatDateNum = SimpleDateFormat("dd MMM", Locale.UK)
    val formatQuery = SimpleDateFormat("yyyy-MM-dd", Locale.UK)

    val datesList = List(7) { offset ->
        calendar.time = Date()
        calendar.add(Calendar.DAY_OF_YEAR, offset)
        val title = if (offset == 0) "Today" else formatDayName.format(calendar.time)
        val sub = formatDateNum.format(calendar.time)
        val queryStr = formatQuery.format(calendar.time)
        val actualDate = calendar.time
        PlannerDate(title, sub, queryStr, actualDate)
    }

    val activeDay = datesList[selectedDayOffset]
    
    // Filter meal plan for the selected day
    val dayPlans = mealPlans.filter { it.dateString == activeDay.queryStr }

    val mealTypes = listOf("Breakfast", "Lunch", "Supper")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WEEKLY PROVISIONING SCHEDULER", fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = FontFamily.Monospace, letterSpacing = 0.5.sp) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // TIMELINE CALENDAR ROW
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(datesList.size) { index ->
                    val dateObj = datesList[index]
                    val isSelected = selectedDayOffset == index
                    
                    Card(
                        modifier = Modifier
                            .width(66.dp)
                            .clickable { selectedDayOffset = index }.testTag("planner_day_offset_$index"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = dateObj.title.uppercase(Locale.ROOT),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = dateObj.subtitle.uppercase(Locale.ROOT),
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${activeDay.title.uppercase(Locale.ROOT)}'S OPERATIONAL MEAL PROTOCOL",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            // SCHEDULING TIMELINE FOR SLOTS
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(mealTypes) { mType ->
                    val linkedPlan = dayPlans.firstOrNull { it.mealType == mType }
                    val plannedRecipe = linkedPlan?.recipeId?.let { rId ->
                        recipes.firstOrNull { it.id == rId }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("meal_slot_$mType"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.width(90.dp)) {
                                Text(
                                    text = mType.uppercase(Locale.ROOT),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "SLOT LOADED",
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            Divider(
                                modifier = Modifier
                                    .height(36.dp)
                                    .width(1.dp)
                                    .padding(horizontal = 8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            )

                            if (linkedPlan != null) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            if (plannedRecipe != null) {
                                                onNavigateToRecipe(plannedRecipe)
                                            }
                                        }
                                ) {
                                    Text(
                                        text = plannedRecipe?.title?.uppercase(Locale.ROOT) ?: "CUSTOM FUEL ASSEMBLY",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (plannedRecipe != null) {
                                        Text(
                                            text = "${plannedRecipe.prepTimeMinutes + plannedRecipe.cookTimeMinutes} Mins | ${plannedRecipe.difficulty}",
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    } else {
                                        Text(
                                            text = linkedPlan.customNotes,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.unscheduleMeal(linkedPlan.id) },
                                    modifier = Modifier.testTag("unschedule_btn_${linkedPlan.id}")
                                ) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Decline planning slot", tint = MaterialTheme.colorScheme.error)
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            targetMealType = mType
                                            selectedRecipeId = -1
                                            customPlanningNotes = ""
                                            showsAddPlanDialog = true
                                        },
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add slot", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "CONSTRUCT SLOT SCHEDULING",
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // CONSTRUCT PLAN SCHEDULER DIALOG
    if (showsAddPlanDialog) {
        Dialog(onDismissRequest = { showsAddPlanDialog = false }) {
            Surface(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "CONSTRUCT MEAL ASSEMBLY SLOT",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Scheduling for: ${targetMealType.uppercase(Locale.ROOT)} on ${activeDay.title.uppercase(Locale.ROOT)}",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    // Method Selector
                    Text("Select Existing Recipe Blueprint:", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { recipeExpanded = true },
                            modifier = Modifier.fillMaxWidth().testTag("plan_recipe_selector"),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            val matchingRec = recipes.firstOrNull { it.id == selectedRecipeId }
                            Text(
                                text = matchingRec?.title?.uppercase(Locale.ROOT) ?: "TAP TO EXPLORE RECIPES",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        DropdownMenu(
                            expanded = recipeExpanded,
                            onDismissRequest = { recipeExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("[ NO RECIPE - USE MANUAL NOTES ]", fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                                onClick = {
                                    selectedRecipeId = -1
                                    recipeExpanded = false
                                }
                            )
                            recipes.forEach { rec ->
                                DropdownMenuItem(
                                    text = { Text(rec.title, fontSize = 11.sp) },
                                    onClick = {
                                        selectedRecipeId = rec.id
                                        recipeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    if (selectedRecipeId == -1) {
                        OutlinedTextField(
                            value = customPlanningNotes,
                            onValueChange = { customPlanningNotes = it },
                            label = { Text("Manual Food Logistics / Notes", fontSize = 12.sp) },
                            placeholder = { Text("e.g. Traditional Sunday Pub Lunch out with Family", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth().testTag("plan_custom_notes_inp"),
                            shape = RoundedCornerShape(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showsAddPlanDialog = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("ABORT", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        }
                        Button(
                            onClick = {
                                viewModel.scheduleMeal(
                                    recipeId = if (selectedRecipeId != -1) selectedRecipeId else 0,
                                    date = activeDay.actualDate,
                                    mealType = targetMealType
                                )
                                showsAddPlanDialog = false
                            },
                            modifier = Modifier.weight(1.2f).testTag("submit_plan_btn"),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("LOG PROTOCOL", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}

data class PlannerDate(
    val title: String,
    val subtitle: String,
    val queryStr: String,
    val actualDate: Date
)
