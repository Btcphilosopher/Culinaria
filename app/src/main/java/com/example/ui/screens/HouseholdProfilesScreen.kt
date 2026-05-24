package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
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
import com.example.data.HouseholdProfile
import com.example.ui.KitchenViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseholdProfilesScreen(viewModel: KitchenViewModel) {
    val profiles by viewModel.profiles.collectAsState()
    val activeProfile by viewModel.currentProfile.collectAsState()

    var showsAddDialog by remember { mutableStateOf(false) }

    // Form states
    var formName by remember { mutableStateOf("") }
    var formRole by remember { mutableStateOf("Senior Chef") }
    var formAllergies by remember { mutableStateOf("") }
    var formPreferences by remember { mutableStateOf("") }

    val roleOptions = listOf("Senior Chef", "Pantry Administrator", "Domestic Engineer", "Household Member")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("HOUSEHOLD OPERATOR PROFILES", fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = FontFamily.Monospace, letterSpacing = 0.5.sp) },
                actions = {
                    Button(
                        onClick = { showsAddDialog = true },
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        modifier = Modifier.padding(end = 8.dp).testTag("open_add_profile_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Profile")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("NEW CORE", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
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
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.03f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(6.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "SAFETY WARNING STATUTORY DICTATE",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Active profiles automatically map household chemical/allergen limits into Culinaria OS. Select your active core before compiling grocery carts.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ACTIVE HOUSEHOLD CORES",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (profiles.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "NO PROFILES REGISTERED. REGISTER CORES.",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(profiles) { profile ->
                        val isActive = activeProfile?.id == profile.id
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectProfile(profile) }.testTag("profile_item_${profile.id}"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isActive) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ),
                            border = BorderStroke(
                                1.5.dp, 
                                if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, 
                                            RoundedCornerShape(4.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person, 
                                        contentDescription = "Portrait",
                                        tint = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = profile.name.uppercase(Locale.ROOT),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "COMMAND ROLE: ${profile.role.uppercase(Locale.ROOT)}",
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    
                                    if (profile.allergies.isNotEmpty()) {
                                        Text(
                                            text = "⚠️ ALLERGIES: ${profile.allergies}",
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                    if (profile.preferences.isNotEmpty()) {
                                        Text(
                                            text = "🍽️ PREF: ${profile.preferences}",
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }

                                if (isActive) {
                                    Icon(
                                        imageVector = Icons.Default.Check, 
                                        contentDescription = "Active Core",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ADD HOUSEHOLD CORE DIALOG
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
                        text = "REGISTER HOUSEHOLD CORE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = formName,
                        onValueChange = { formName = it },
                        label = { Text("Operator Name", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("add_profile_name"),
                        shape = RoundedCornerShape(4.dp)
                    )

                    OutlinedTextField(
                        value = formAllergies,
                        onValueChange = { formAllergies = it },
                        label = { Text("Active Allergen Risks (e.g. Nuts, Dairy)", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("add_profile_allergies"),
                        shape = RoundedCornerShape(4.dp)
                    )

                    OutlinedTextField(
                        value = formPreferences,
                        onValueChange = { formPreferences = it },
                        label = { Text("Culinary / Fuel Preferences", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("add_profile_preferences"),
                        shape = RoundedCornerShape(4.dp)
                    )

                    Text("Operating Role:", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(roleOptions) { rOption ->
                            val isSelected = formRole == rOption
                            OutlinedButton(
                                onClick = { formRole = rOption },
                                shape = RoundedCornerShape(4.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
                                    contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Text(rOption.uppercase(Locale.ROOT), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
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
                                if (formName.isNotBlank()) {
                                    viewModel.createHouseholdProfile(
                                        name = formName,
                                        role = formRole,
                                        allergies = formAllergies,
                                        preferences = formPreferences
                                    )
                                    showsAddDialog = false
                                    formName = ""
                                    formAllergies = ""
                                    formPreferences = ""
                                }
                            },
                            modifier = Modifier.weight(1.2f).testTag("submit_profile_btn"),
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
