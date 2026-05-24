package com.example.data

import com.example.network.GeminiService
import kotlinx.coroutines.flow.Flow
import java.util.Locale

class KitchenRepository(private val kitchenDao: KitchenDao) {

    // Database access flows
    val allRecipes: Flow<List<Recipe>> = kitchenDao.getAllRecipes()
    val pantryItems: Flow<List<PantryItem>> = kitchenDao.getAllPantryItems()
    val mealPlans: Flow<List<MealPlan>> = kitchenDao.getAllMealPlans()
    val shoppingList: Flow<List<ShoppingItem>> = kitchenDao.getShoppingList()
    val profiles: Flow<List<HouseholdProfile>> = kitchenDao.getProfiles()

    // Recipe mutations
    suspend fun insertRecipe(recipe: Recipe) = kitchenDao.insertRecipe(recipe)
    suspend fun updateRecipe(recipe: Recipe) = kitchenDao.updateRecipe(recipe)
    suspend fun deleteRecipe(id: Int) = kitchenDao.deleteRecipeById(id)
    suspend fun getRecipeById(id: Int): Recipe? = kitchenDao.getRecipeById(id)

    // Pantry mutations
    suspend fun insertPantry(item: PantryItem) = kitchenDao.insertPantryItem(item)
    suspend fun deletePantry(item: PantryItem) = kitchenDao.deletePantryItem(item)
    suspend fun deletePantryById(id: Int) = kitchenDao.deletePantryItemById(id)

    // Meal planning mutations
    suspend fun insertMealPlan(plan: MealPlan) = kitchenDao.insertMealPlan(plan)
    suspend fun deleteMealPlan(plan: MealPlan) = kitchenDao.deleteMealPlan(plan)
    suspend fun deleteMealPlanById(id: Int) = kitchenDao.deleteMealPlanById(id)

    // Shopping List mutations
    suspend fun insertShoppingItem(item: ShoppingItem) = kitchenDao.insertShoppingItem(item)
    suspend fun updateShoppingItem(item: ShoppingItem) = kitchenDao.updateShoppingItem(item)
    suspend fun deleteShoppingItem(item: ShoppingItem) = kitchenDao.deleteShoppingItem(item)
    suspend fun clearPurchased() = kitchenDao.clearPurchasedShoppingItems()

    // Household Profile mutations
    suspend fun insertProfile(profile: HouseholdProfile) = kitchenDao.insertProfile(profile)
    suspend fun deleteProfile(profile: HouseholdProfile) = kitchenDao.deleteProfile(profile)

    // Match pantry stock to a recipe to find available and missing items
    fun matchPantryToRecipe(recipe: Recipe, pantry: List<PantryItem>): PantryMatch {
        val recipeIngredients = parseRecipeIngredients(recipe.ingredientsList)
        val matched = mutableListOf<String>()
        val missing = mutableListOf<String>()
        val suggestions = mutableMapOf<String, String>()

        for (reqIng in recipeIngredients) {
            val isAvailable = pantry.any { pantryItem ->
                pantryItem.quantity > 0 && 
                (pantryItem.name.contains(reqIng.name, ignoreCase = true) || 
                 reqIng.name.contains(pantryItem.name, ignoreCase = true))
            }

            if (isAvailable) {
                matched.add(reqIng.fullName)
            } else {
                missing.add(reqIng.fullName)
                val sub = getFallbackSubstitute(reqIng.name)
                if (sub.isNotEmpty()) {
                    suggestions[reqIng.fullName] = sub
                }
            }
        }

        val percentage = if (recipeIngredients.isEmpty()) 100 else (matched.size * 100) / recipeIngredients.size
        return PantryMatch(matched, missing, suggestions, percentage)
    }

    // Static historical and culinary substitutes
    private fun getFallbackSubstitute(ingredientName: String): String {
        val lower = ingredientName.lowercase(Locale.ROOT)
        return when {
            lower.contains("bangers") || lower.contains("sausage") -> "Vegetarian plant-based sausages or seasoned pork mince patties"
            lower.contains("butter") -> "Margarine (75% yield check) or lard (for baking density)"
            lower.contains("milk") -> "Reconstituted powdered milk or watered cream (1:1)"
            lower.contains("cream") -> "Evaporated whole milk or butter whipped into warm milk"
            lower.contains("ale") || lower.contains("beer") -> "Beef stock infused with malt extract or stout brew"
            lower.contains("beef") || lower.contains("tenderloin") -> "Roast venison cut or slow-braised chuck steak"
            lower.contains("sugar") -> "Golden syrup, beet sucrose, or honey reserves"
            lower.contains("piper") || lower.contains("potato") -> "King Edward, Maris Peer, or sweet potato tubers"
            lower.contains("bovril") -> "Marmite yeast extract or dense vegetable bullion"
            lower.contains("flour") -> "Plain flour sifted with baking powder (1 tsp per 100g)"
            lower.contains("plum") -> "Damson preserve, stewed apples, or rhubarb stocks"
            else -> "Alternative savory seasoning or dry pantry reserves"
        }
    }

    // Parse ingredients from standard format "4 | Cumberland Pork Bangers"
    fun parseRecipeIngredients(ingredientsText: String): List<ParsedIngredient> {
        return ingredientsText.split("\n")
            .filter { it.isNotBlank() }
            .map { line ->
                val parts = line.split("|")
                if (parts.size >= 2) {
                    val amount = parts[0].trim()
                    val name = parts[1].trim()
                    ParsedIngredient(amount, name, "$amount $name")
                } else {
                    ParsedIngredient("", line.trim(), line.trim())
                }
            }
    }

    // Call Gemini API for smart seasonal meal planning suggestions based on weather and season
    suspend fun getGeminiSmartMealPlanAdvice(weather: String, season: String, pantryList: List<PantryItem>): String {
        val pantrySummary = pantryList.joinToString { "${it.quantity} ${it.unit} of ${it.name}" }
        val prompt = """
            We are drawing up domestic culinary plans under Anglo-Futurist principles.
            Current Season: $season
            Current Weather Climate: $weather
            Pantry Assets: $pantrySummary
            
            Provide a authoritative, structured culinary advisory for tonight's supper in 2 short paragraphs:
            1. Recommend a cohesive British culinary pairing that fits the current external environment (e.g. warming pies for cold rains, jellies or preserve desserts for hot sun).
            2. Suggest how they can optimize their pantry assets, proposing smart substitutions for typical traditional ingredients.
            
            Keep the tone deeply technical, structured, calm, and inspired by high-end British public service typography (Ministry of Food / BBC editorial style).
        """.trimIndent()

        val systemPrompt = "You are the Culinaria OS Culinary Intelligence Engine, a highly structured operating system for modern British kitchens."
        
        val response = GeminiService.generateResponse(prompt, systemPrompt)
        return response.ifEmpty {
            // High quality fallback in case API key is empty
            "METEOROLOGICAL REPORT CALIBRATED: As it is $season with $weather, the optimal domestic protocol recommends thermal consolidation. We advise preparing Cyber-Sausage and Meadow Mash to stabilize household energy. Utilizing potatoes and pork bangers from stock minimises resource leakage. Substitute salted butter with pantry liquid fats if dairy level is declining."
        }
    }

    // Call Gemini API to suggest a custom recipe based on current pantry assets
    suspend fun generateRecipeFromPantry(pantryList: List<PantryItem>): String {
        val pantrySummary = pantryList.joinToString { "${it.quantity} ${it.unit} of ${it.name}" }
        val prompt = """
            Pantry Inventory Available: $pantrySummary
            
            Generate ONE unique, highly structured Anglo-Futurist recipe inspired by British traditions that can be constructed utilizing these ingredients.
            
            Format your response STRICTLY with these section headers (do not use markdown formatting inside, keep headers simple):
            TITLE: [Recipe Name]
            OVERVIEW: [One sentence description]
            ESTIMATED TIME: Prep [X] mins | Cook [Y] mins
            INGREDIENTS:
            - [Qty] of [Ingredient]
            INSTRUCTIONS:
            1. [Step 1]
            TECHNICAL ADVICE: [One tactical advice]
            
            Ensure the recipe feels realistic, authentic to tradition, yet structured like an engineering manual.
        """.trimIndent()

        val systemPrompt = "You are Culinaria OS Culinary Intelligence Engine, generating precise recipe blueprints and technical documentation."
        val response = GeminiService.generateResponse(prompt, systemPrompt)
        return response.ifEmpty {
            "TITLE: Savory British Pantry Hash\nOVERVIEW: A mathematical consolidation of remaining pantry starches and protein assets sieved and fried to absolute golden crispness.\nESTIMATED TIME: Prep 10 mins | Cook 15 mins\nINGREDIENTS:\n- 200g of Maris Piper Potatoes\n- 2 Cumberland Pork Bangers\n- 20g of Salted Butter\nINSTRUCTIONS:\n1. Dice parboiled potatoes into 1cm cubes.\n2. Slice the Cumberland bangers into fine circles.\n3. Melt salted butter in an iron skillet at 160°C, fold potatoes and bangers, and fry undisturbed for 4 minutes to induce crisp caramel casing.\nTECHNICAL ADVICE: Do not crowd the skillet; starch gridlock will prevent caramelization."
        }
    }
}

// Support structures
data class ParsedIngredient(
    val amount: String,
    val name: String,
    val fullName: String
)

data class PantryMatch(
    val matched: List<String>,
    val missing: List<String>,
    val substitutes: Map<String, String>,
    val matchPercentage: Int
)
