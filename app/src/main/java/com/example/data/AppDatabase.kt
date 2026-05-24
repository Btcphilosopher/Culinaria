package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        PantryItem::class,
        Recipe::class,
        MealPlan::class,
        ShoppingItem::class,
        HouseholdProfile::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun kitchenDao(): KitchenDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kitchen_os_database"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.kitchenDao())
                }
            }
        }

        private suspend fun populateInitialData(dao: KitchenDao) {
            // Initial Profiles
            dao.insertProfile(HouseholdProfile(name = "Master Engineer Tom", role = "Senior Chef", preferences = "Traditional British, High Protein"))
            dao.insertProfile(HouseholdProfile(name = "Domestic Admin Alice", role = "Pantry Administrator", preferences = "Low Carbon, Balanced"))

            // Initial Recipes
            dao.insertRecipe(
                Recipe(
                    title = "Cyber-Sausage and Meadow Mash",
                    overview = "An industrial-futurist refinement of a British home classic, using precise thermal profiling and rich herbs to perfect standard bangers and mash.",
                    category = "Supper",
                    prepTimeMinutes = 15,
                    cookTimeMinutes = 25,
                    servings = 2,
                    difficulty = "Standard",
                    equipmentNeeded = "Thermal Skillet, Food Processor, Mash Ricer",
                    ingredientsList = "4 | Cumberland Pork Bangers\n4 | Large Maris Piper Potatoes\n50ml | Fresh Whole Milk\n30g | Salted Butter\n1 | Large Yellow Onion (Slivered)\n100ml | Dark British Ale\n1 tsp | Bovril Beef Paste\n1 tsp | Dried Thyme",
                    instructionSteps = "1. Mash Construction: Peel and quarter Maris Piper potatoes. Boil under 100°C steam until absolute cellular structure collapses (approx. 15 mins). Drain, pass through a ricer twice, then blend in hot milk and butter at high speed for peak velvety suspension.\n2. Thermal Sausage Searing: Place Cumberland bangers on the cast iron pan. Crank base heat to 180°C. Settle on a uniform rotary rotate cycle every 3 mins to seal moisture and generate caramelization without split skins.\n3. Ale Gravy Reduction: Extract sausages to tray. Deglaze deep iron skillet with yellow onions and dried thyme. Heat until translucent. Drizzle Dark British Ale and dissolve Bovril paste. Whisk vigorously on high heat for 5 minutes until viscous dark liquor forms.\n4. Assembly Vector: Pool cream mash at base, embed crisp bangers at 45-degree angle, and drape with dark ale glaze.",
                    industrialNotes = "INFRASTRUCTURE WARNING: Do not allow mash to drop below 60°C before plating to maintain ideal starch viscosity.",
                    imageUrl = ""
                )
            )

            dao.insertRecipe(
                Recipe(
                    title = "Wartime Ration Grid-Sponge Cake",
                    overview = "A historical preservation recipe adapting the 1944 Ministry of Food sponge formula with modernized cybernetic heat-cycle protocols.",
                    category = "Baking",
                    prepTimeMinutes = 10,
                    cookTimeMinutes = 20,
                    servings = 6,
                    difficulty = "Basic",
                    equipmentNeeded = "Standard Baking Tin, Measuring Scales (Monospace-tuned)",
                    ingredientsList = "150g | Self-Raising Flour\n75g | Golden Caster Sugar\n50g | Margarine or Butter\n1 | Large Household Egg\n50ml | Water\n1 tbsp | Plum Preserve Jam",
                    instructionSteps = "1. Pre-Heat Blueprint: Calibrate baking oven strictly to 180°C. Grease a simple rectangular tin.\n2. Batter Formulation: Cream margarine and caster sugar together with a wooden spatula until light and aerated. Beat the single egg in slowly. Sift self-raising flour from a height to preserve air structure, then fold in gently while dripping water to loosen consistency.\n3. Bake Mechanics: Decant into the greased tin. Bake for exactly 20 minutes. Core temperature should show 95°C on a probe.\n4. Core Extraction: Squeeze plum preserve jam over base, fold sponge and enjoy the warm dense crumb.",
                    industrialNotes = "INTELLIGENCE UPDATE: Original formulas utilized powdered egg; fresh egg offers significantly improved protein binding.",
                    imageUrl = ""
                )
            )

            dao.insertRecipe(
                Recipe(
                    title = "Futurist Beef Wellington Plate",
                    overview = "The pinnacle of Anglo-Futurist dinner parties. A succulent beef tenderloin cooked to thermal perfection, wrapped in a mathematically layered puff pastry mesh.",
                    category = "Anglo-Futurist Experimental",
                    prepTimeMinutes = 40,
                    cookTimeMinutes = 35,
                    servings = 4,
                    difficulty = "Technical",
                    equipmentNeeded = "Digital Thermal Probe, Air Fryer or Convection Oven, Plastic Wrap",
                    ingredientsList = "500g | Premium British Beef Tenderloin\n200g | Mixed Woodland Mushrooms (Chopped)\n1 tbsp | Golden English Mustard\n6 slices | Prosciutto ham\n1 sheet | Butter Puff Pastry\n1 | Egg Yolk (For wash)\n1 sprig | Fresh Thyme",
                    instructionSteps = "1. Tenderloin Searing Protocol: Season beef liberally. Sear all surfaces in a super-hot pan for 30 seconds per zone to seal beef serum. Smear English mustard on cooked surface, then refrigerate.\n2. Mushroom Duxelles Reduction: Pulse woodland mushrooms to fine duxelles paste. Cook in dry pan with thyme until water content is fully vaporized (critical for preventing wet pastry).\n3. Structural Wrapping: Lay plastic sheets, draft prosciutto slices side-by-side. Spread the mushroom duxelles uniformly. Sit tenderloin at center Axis, roll tightly to construct a moisture barrier, and chill for 15 mins.\n4. Pastry Enclosure: Unroll puff pastry sheet. Envelop beef cylinder. Score geometric diagonal ventilation lines. Glaze with egg yolk. Convect-bake at 200°C until core probe senses exactly 54°C (rare/medium-rare). Let settle 10 minutes to stabilize.",
                    industrialNotes = "FLUID LOGISTICS: Slicing must occur with a dry, serrated knife using slow horizontal strokes to avoid collapsing the outer puff-pastry truss.",
                    imageUrl = ""
                )
            )

            dao.insertRecipe(
                Recipe(
                    title = "Victoria Preservation Plum Jam",
                    overview = "An operational manual for fruit preservation, maintaining long-term domestic security storage.",
                    category = "Preserves",
                    prepTimeMinutes = 15,
                    cookTimeMinutes = 45,
                    servings = 20,
                    difficulty = "Standard",
                    equipmentNeeded = "Stainless Steel Jam Pot, Sterile Glass Jars, Jelly Thermometer",
                    ingredientsList = "1kg | Victoria Plums (Stoned & halved)\n1kg | White Jam Sugar (With Pectin)\n150ml | Pure Well Water\n1 | Lemon (Juiced)",
                    instructionSteps = "1. Pectin Leaching: Soften Victorian plums in a large steel pot with well water and lemon juice. Stew on medium fire for 15 mins until skin fibers breakdown.\n2. Crystallization Wave: Pour in sugar. Stir on low heat until sugar crystals are completely dissolved. Do not boil early.\n3. Rolling Boil Process: Crank heat to maximum. Boil violently without stirring for 10-12 minutes until reaching the scientific gelation temperature of 104°C.\n4. Vacuum Seal: Skim froth. Ladle hot jam into sterile jars, leaving exactly 5mm headspace. Seal instantly to induce negative pressure vacuum locking.",
                    industrialNotes = "PANTY STATUS: Properly sealed jars will remain biologically stable in dark storage for up to 24 months.",
                    imageUrl = ""
                )
            )

            // Pantry Items Setup
            dao.insertPantryItem(PantryItem(name = "Self-Raising Flour", category = "Grains", quantity = 1500.0, unit = "g", expiryDate = System.currentTimeMillis() + 1000000000L))
            dao.insertPantryItem(PantryItem(name = "Maris Piper Potatoes", category = "Vegetables", quantity = 5.0, unit = "kg", expiryDate = System.currentTimeMillis() + 500000000L))
            dao.insertPantryItem(PantryItem(name = "Cumberland Pork Bangers", category = "Meat", quantity = 8.0, unit = "units", expiryDate = System.currentTimeMillis() + 86400000L * 3))
            dao.insertPantryItem(PantryItem(name = "Salted Butter", category = "Dairy", quantity = 250.0, unit = "g", expiryDate = System.currentTimeMillis() + 86400000L * 15))
            dao.insertPantryItem(PantryItem(name = "Dark British Ale", category = "Pantry", quantity = 4.0, unit = "bottles", expiryDate = System.currentTimeMillis() + 1000000000L))

            // Shopping list Items Setup
            dao.insertShoppingItem(ShoppingItem(name = "Premium British Beef Tenderloin", quantity = 500.0, unit = "g", category = "Meat", isPurchased = false))
            dao.insertShoppingItem(ShoppingItem(name = "White Jam Sugar", quantity = 1.0, unit = "kg", category = "Baking", isPurchased = false))
            dao.insertShoppingItem(ShoppingItem(name = "Victoria Plums", quantity = 1.5, unit = "kg", category = "Produce", isPurchased = true))
        }
    }
}
