package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class KitchenViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = KitchenRepository(database.kitchenDao())

    val recipes = repository.allRecipes.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val pantry = repository.pantryItems.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val mealPlans = repository.mealPlans.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val shoppingList = repository.shoppingList.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val profiles = repository.profiles.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Interactive States
    private val _selectedRecipe = MutableStateFlow<Recipe?>(null)
    val selectedRecipe = _selectedRecipe.asStateFlow()

    private val _activeCookingRecipe = MutableStateFlow<Recipe?>(null)
    val activeCookingRecipe = _activeCookingRecipe.asStateFlow()

    private val _activeStepIndex = MutableStateFlow(0)
    val activeStepIndex = _activeStepIndex.asStateFlow()

    private val _activeTimerSeconds = MutableStateFlow(0)
    val activeTimerSeconds = _activeTimerSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning = _isTimerRunning.asStateFlow()

    private val _activeTimerOriginalTotal = MutableStateFlow(0)
    val activeTimerOriginalTotal = _activeTimerOriginalTotal.asStateFlow()

    private val _aiRecommendation = MutableStateFlow("")
    val aiRecommendation = _aiRecommendation.asStateFlow()

    private val _aiLoading = MutableStateFlow(false)
    val aiLoading = _aiLoading.asStateFlow()

    private val _generatedRecipeResult = MutableStateFlow("")
    val generatedRecipeResult = _generatedRecipeResult.asStateFlow()

    private val _generatedRecipeLoading = MutableStateFlow(false)
    val generatedRecipeLoading = _generatedRecipeLoading.asStateFlow()

    private val _currentProfile = MutableStateFlow<HouseholdProfile?>(null)
    val currentProfile = _currentProfile.asStateFlow()

    private var timerJob: Job? = null

    init {
        // Automatically set first profile as active once profiles list is populated
        viewModelScope.launch {
            profiles.collect { list ->
                if (list.isNotEmpty() && _currentProfile.value == null) {
                    _currentProfile.value = list.first()
                }
            }
        }
    }

    // Navigation and detail selectors
    fun selectRecipe(recipe: Recipe?) {
        _selectedRecipe.value = recipe
    }

    fun selectProfile(profile: HouseholdProfile) {
        _currentProfile.value = profile
    }

    // Guided cooking workflows
    fun startCooking(recipe: Recipe) {
        _activeCookingRecipe.value = recipe
        _activeStepIndex.value = 0
        resetTimer()
    }

    fun nextStep() {
        val steps = _activeCookingRecipe.value?.instructionSteps?.split("\n")?.filter { it.isNotBlank() } ?: emptyList()
        if (_activeStepIndex.value < steps.size - 1) {
            _activeStepIndex.value++
            resetTimer()
        }
    }

    fun previousStep() {
        if (_activeStepIndex.value > 0) {
            _activeStepIndex.value--
            resetTimer()
        }
    }

    fun triggerTimer(minutes: Int) {
        stopTimer()
        _activeTimerOriginalTotal.value = minutes * 60
        _activeTimerSeconds.value = minutes * 60
        startTimer()
    }

    fun startTimer() {
        if (_activeTimerSeconds.value <= 0) return
        _isTimerRunning.value = true
        timerJob = viewModelScope.launch {
            while (_activeTimerSeconds.value > 0) {
                delay(1000)
                _activeTimerSeconds.value--
            }
            _isTimerRunning.value = false
        }
    }

    fun stopTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
    }

    fun resetTimer() {
        stopTimer()
        _activeTimerSeconds.value = 0
        _activeTimerOriginalTotal.value = 0
    }

    // Pantry, inventory, and logic-aware features
    fun matchPantryItems(recipe: Recipe): PantryMatch {
        return repository.matchPantryToRecipe(recipe, pantry.value)
    }

    fun addPantryItem(name: String, category: String, quantity: Double, unit: String, expiryDays: Int) {
        viewModelScope.launch {
            val expiry = if (expiryDays > 0) System.currentTimeMillis() + (86400000L * expiryDays) else null
            repository.insertPantry(PantryItem(name = name, category = category, quantity = quantity, unit = unit, expiryDate = expiry))
        }
    }

    fun consumePantryItem(item: PantryItem, amount: Double) {
        viewModelScope.launch {
            val remaining = item.quantity - amount
            if (remaining <= 0) {
                repository.deletePantry(item)
            } else {
                repository.insertPantry(item.copy(quantity = remaining))
            }
        }
    }

    fun deletePantryItem(item: PantryItem) {
        viewModelScope.launch {
            repository.deletePantry(item)
        }
    }

    // Shopping provisioning
    fun addShoppingItem(name: String, quantity: Double, unit: String, category: String) {
        viewModelScope.launch {
            repository.insertShoppingItem(ShoppingItem(name = name, quantity = quantity, unit = unit, category = category))
        }
    }

    fun toggleShoppingItemPurchased(item: ShoppingItem) {
        viewModelScope.launch {
            repository.updateShoppingItem(item.copy(isPurchased = !item.isPurchased))
        }
    }

    fun deleteShoppingItem(item: ShoppingItem) {
        viewModelScope.launch {
            repository.deleteShoppingItem(item)
        }
    }

    fun convertRecipeToShoppingList(recipe: Recipe) {
        viewModelScope.launch {
            val missingIngredients = matchPantryItems(recipe).missing
            for (missingText in missingIngredients) {
                val separatorIdx = missingText.indexOf("|")
                val quantityStr = if (separatorIdx != -1) missingText.substring(0, separatorIdx).trim() else "1"
                val name = if (separatorIdx != -1) missingText.substring(separatorIdx + 1).trim() else missingText
                
                val qtyVal = quantityStr.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 1.0
                val unit = quantityStr.filter { it.isLetter() }.trim()
                
                repository.insertShoppingItem(
                    ShoppingItem(
                        name = name,
                        quantity = qtyVal,
                        unit = unit,
                        category = recipe.category,
                        sourceRecipeId = recipe.id
                    )
                )
            }
        }
    }

    fun clearAllPurchased() {
        viewModelScope.launch {
            repository.clearPurchased()
        }
    }

    // Meal planner scheduling
    fun scheduleMeal(recipeId: Int, date: Date, mealType: String) {
        viewModelScope.launch {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
            val dateStr = formatter.format(date)
            repository.insertMealPlan(MealPlan(dateString = dateStr, mealType = mealType, recipeId = recipeId))
        }
    }

    fun unscheduleMeal(planId: Int) {
        viewModelScope.launch {
            repository.deleteMealPlanById(planId)
        }
    }

    // Custom Recipe Add
    fun addNewRecipe(
        title: String,
        overview: String,
        category: String,
        prepMins: Int,
        cookMins: Int,
        servings: Int,
        difficulty: String,
        equip: String,
        ingredients: String,
        steps: String,
        notes: String
    ) {
        viewModelScope.launch {
            repository.insertRecipe(
                Recipe(
                    title = title,
                    overview = overview,
                    category = category,
                    prepTimeMinutes = prepMins,
                    cookTimeMinutes = cookMins,
                    servings = servings,
                    difficulty = difficulty,
                    equipmentNeeded = equip,
                    ingredientsList = ingredients,
                    instructionSteps = steps,
                    industrialNotes = notes
                )
            )
        }
    }

    // Profiles Configuration
    fun createHouseholdProfile(name: String, role: String, allergies: String, preferences: String) {
        viewModelScope.launch {
            repository.insertProfile(
                HouseholdProfile(name = name, role = role, allergies = allergies, preferences = preferences)
            )
        }
    }

    // AI Gen actions
    fun loadSmartAdvisor(weather: String, season: String) {
        viewModelScope.launch {
            _aiLoading.value = true
            val advice = repository.getGeminiSmartMealPlanAdvice(weather, season, pantry.value)
            _aiRecommendation.value = advice
            _aiLoading.value = false
        }
    }

    fun generateRecipeFromMyPantry() {
        viewModelScope.launch {
            _generatedRecipeLoading.value = true
            val text = repository.generateRecipeFromPantry(pantry.value)
            _generatedRecipeResult.value = text
            _generatedRecipeLoading.value = false
        }
    }

    fun parseAndSaveGeneratedRecipe() {
        val text = _generatedRecipeResult.value
        if (text.isBlank() || text.contains("Error:")) return
        
        try {
            var title = "AI Generated Traditional Hash"
            var overview = "Optimized starch consolidation."
            var prepTime = 15
            var cookTime = 15
            val ingredients = mutableListOf<String>()
            val steps = mutableListOf<String>()
            var advice = ""
            
            val lines = text.split("\n")
            var currentSection = ""
            for (line in lines) {
                val trimmed = line.trim()
                if (trimmed.startsWith("TITLE:")) {
                    title = trimmed.replace("TITLE:", "").trim()
                } else if (trimmed.startsWith("OVERVIEW:")) {
                    overview = trimmed.replace("OVERVIEW:", "").trim()
                } else if (trimmed.startsWith("ESTIMATED TIME:")) {
                    val timeText = trimmed.replace("ESTIMATED TIME:", "").trim()
                    val numbers = timeText.filter { it.isDigit() || it == ' ' }.split(" ").filter { it.isNotBlank() }
                    if (numbers.isNotEmpty()) prepTime = numbers[0].toIntOrNull() ?: 15
                    if (numbers.size >= 2) cookTime = numbers[1].toIntOrNull() ?: 15
                } else if (trimmed.startsWith("INGREDIENTS:")) {
                    currentSection = "ingredients"
                } else if (trimmed.startsWith("INSTRUCTIONS:")) {
                    currentSection = "steps"
                } else if (trimmed.startsWith("TECHNICAL ADVICE:")) {
                    advice = trimmed.replace("TECHNICAL ADVICE:", "").trim()
                    currentSection = ""
                } else {
                    if (currentSection == "ingredients" && (trimmed.startsWith("-") || trimmed.startsWith("*"))) {
                        val ing = trimmed.substring(1).trim()
                        val separatorIdx = ing.indexOf(" of ")
                        if (separatorIdx != -1) {
                            val qty = ing.substring(0, separatorIdx).trim()
                            val name = ing.substring(separatorIdx + 4).trim()
                            ingredients.add("$qty | $name")
                        } else {
                            ingredients.add("1 unit | $ing")
                        }
                    } else if (currentSection == "steps" && trimmed.firstOrNull()?.isDigit() == true) {
                        steps.add(trimmed)
                    }
                }
            }

            if (ingredients.isEmpty()) {
                ingredients.add("1 unit | Available pantry elements")
            }
            if (steps.isEmpty()) {
                steps.add("1. Consolidate pantry assets in skillet. 2. Fry at 180°C until golden.")
            }

            addNewRecipe(
                title = title,
                overview = overview,
                category = "Anglo-Futurist Experimental",
                prepMins = prepTime,
                cookMins = cookTime,
                servings = 2,
                difficulty = "Experimental",
                equip = "Skillet, Digital Food Scale",
                ingredients = ingredients.joinToString("\n"),
                steps = steps.joinToString("\n"),
                notes = advice.ifEmpty { "HYPOTHESIS STABLE: Constructed via AI Pantry Sieve." }
            )
            
            _generatedRecipeResult.value = ""
        } catch (e: Exception) {
            addNewRecipe(
                title = "AI Pantry Settle",
                overview = "Constructed via AI Sieve fallback algorithm.",
                category = "Anglo-Futurist Experimental",
                prepMins = 12,
                cookMins = 12,
                servings = 2,
                difficulty = "Basic",
                equip = "Skillet",
                ingredients = "200g | Starches\n50g | Butter fats\n1 | Egg protein",
                steps = "1. Sauté ingredients on flatbed grill at high heat.\n2. Plate and consume warm.",
                notes = "EMERGENCY BINDING PROTOCOL ACCESSED due to parser mismatch."
            )
            _generatedRecipeResult.value = ""
        }
    }
}
