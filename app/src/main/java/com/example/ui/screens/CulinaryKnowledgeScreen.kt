package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CulinaryKnowledgeScreen() {
    var expandedChapter by remember { mutableStateOf<Int?>(null) }

    val chapters = listOf(
        KnowledgeChapter(
            id = 1,
            title = "01. THERMAL SEBERING & KNIFE SHEAR METRICS",
            category = "Physical Preparation Methods",
            synopsis = "The fluid dynamics of structural slicing. Standardizing steel alignments for kitchen safeties.",
            content = """
                KNIFE GRIP GEOMETRY (THE OPERATOR BIND):
                - The Primary Pinch: Grip the stainless steel blade root directly between thumb and bent index finger. This aligns the center of gravity directly beneath the palm, maximizing torque and structural control.
                - The Claw Retraction (Securing Target): Rest the fingertips of your guiding hand inward in a cat's claw formation. The knuckles serve as a vertical rail guiding the flat face of the steel knife blade safely downwards.
                
                VELOCITY SHEAR PROTOCOLS:
                - The Rocking Glide: Place the curved tip of the chef's knife in constant contact with the oak chopping board. Apply cyclic rocking motions, driving the blade down and forward to shear cell walls clean.
                - The Vertical Chop: Ideal for high-density tubers (e.g. Maris Piper potatoes). Drive the steel parallel to the vertical axis in single clean slices, avoiding bruising starch pockets.
            """.trimIndent()
        ),
        KnowledgeChapter(
            id = 2,
            title = "02. COLLOIDAL EMULSIONS & THE THEORY OF SAUCES",
            category = "Fluid Mechanics",
            synopsis = "Synthesizing lipid and liquid suspensions into high-viscosity culinary glazes.",
            content = """
                THE MECHANICS OF EMULSION:
                - An emulsion is an unstable dispersion of microscopic oil droplets suspended within an aqueous phase. To prevent thermodynamic splitting, we must utilize lecithin (phospholipids) or master starches.
                
                THE CORE INDUSTRIAL EMULSIONS:
                - The Egg-Yolk Protocol (Mayonnaise/Hollandaise): Egg yolk contains highly active lecithin surfactants. Dripping melted butter or canola fats slowly into the acidic yolk on low warmth (54°C) constructs a rigid hexagonal cell packing.
                - The Starch Gel (Gravy / Velouté): Sautéing equal mass of steel-milled flour and butter (a Roux Grid) expands starches. Adding hot bone broths under continuous shear whiskers aligns starch polymers into a glassy, smooth suspension.
            """.trimIndent()
        ),
        KnowledgeChapter(
            id = 3,
            title = "03. CHEMICAL EXPANSION & THERMAL BAKING PROTOCOLS",
            category = "Thermodynamic Baking Science",
            synopsis = "Carbon release mechanics and protein grid alignments inside heated baker chambers.",
            content = """
                THE YEAST ENVELOPE:
                - Saccharomyces cerevisiae (baker's yeast) metabolizes simple sucrose carbohydrates into carbon dioxide gastight bubbles and ethanol.
                - Optimal fermentation bounds: 26°C - 38°C. Beyond 54°C, yeast proteins experience denaturation, terminating the volume rise.
                
                GLUTEN CELLULAR TRUSSES:
                - Mixing water with wheat gliadin and glutenin forms strong disulfide bonds. Kneading aligns these random protein fibrils into a continuous elastic lattice that traps released gases.
                - High-density salts (1.5% to 2% flour mass) strengthen this cellular alignment, preventing cake or bread grid collapse during early crust crusting.
            """.trimIndent()
        ),
        KnowledgeChapter(
            id = 4,
            title = "04. CHRONO-ISOLATION: PRESERVATION AND VACUUM GEOMETRY",
            category = "Biological Food Security",
            synopsis = "Inhibiting pathogen expansion through high-acid, high-solute solutions and glass jar vacuum-seals.",
            content = """
                THE MECHANICS OF ANISE-PLUM JAM SYNC:
                - High concentration of sugar (60% to 65% total mass) binds water molecules, reducing 'water activity' below 0.85—a boundary where mold spores and bacteria cannot draw fluid for reproduction.
                - Natural pectin requires low pH (2.8 - 3.5) and high sucrose levels to aggregate into a rigid structural gel.
                
                SEALING TELEMETRY:
                - Fill glass vessels with boiling preserves to exactly 5mm headspace. Seal hermetically with a spring rubber cap. As the preserve cools, fluid volume contracts, forming a partial vacuum of negative pressure that locks out contaminants.
            """.trimIndent()
        ),
        KnowledgeChapter(
            id = 5,
            title = "05. METROPOLIS DECARBONIZATION & THE THEATER OF RAIL DINING",
            category = "Anglo-Futurist Historical Evolution",
            synopsis = "Tracing the growth of dining compartments from Victorian steam liners to the modern operational kitchen.",
            content = """
                THE SHIFT OF INFRASTRUCTURE:
                - In 1879, the Great Northern Railway introduced the world's first steam dining compartment on British lines. Kitchens moved from dark cellar fires to highly packed, steel-clad galleys with strict fluid conservation rules.
                - The Kitchen is no longer a site of decorative domestic excess. It is an operational engine. Culinaria OS honors this design history by treating stove ranges like boilers, ingredients like cargo manifestos, and cooking schedules like train schedules.
            """.trimIndent()
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CULINARY KNOWLEDGE PROTOCOLS", fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = FontFamily.Monospace, letterSpacing = 0.5.sp) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = "Manual info")
                        Column {
                            Text(
                                "OFFICIAL ENCYCLOPEDIA & ENGINEERING MANUALS",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "Explore scientific logs and historical documentation constructed to preserve the culinary legacy of the modern household.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            items(chapters) { chap ->
                val isExpanded = expandedChapter == chap.id
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedChapter = if (isExpanded) null else chap.id }
                        .testTag("knowledge_chapter_${chap.id}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isExpanded) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    border = BorderStroke(1.dp, if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = chap.category.uppercase(Locale.ROOT),
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = chap.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = chap.synopsis,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )

                        AnimatedVisibility(
                            visible = isExpanded,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column {
                                Divider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                )
                                Text(
                                    text = chap.content,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class KnowledgeChapter(
    val id: Int,
    val title: String,
    val category: String,
    val synopsis: String,
    val content: String
)
