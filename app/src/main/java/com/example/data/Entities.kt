package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "pantry_items")
data class PantryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String, // e.g. Grains, Dairy, Meat, Spices, Vegetables
    val quantity: Double,
    val unit: String, // e.g. g, ml, units, packs
    val expiryDate: Long?, // Timestamp of expiry or null
    val notes: String = "",
    val autoReplenish: Boolean = false
)

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val overview: String,
    val category: String, // Breakfast, Supper, Baking, Roasts, Preserves, Tea & Desserts, Regional British, Anglo-Futurist Experimental
    val prepTimeMinutes: Int,
    val cookTimeMinutes: Int,
    val servings: Int,
    val difficulty: String, // e.g. Technical, Standard, Basic, Experimental
    val equipmentNeeded: String, // Newline or comma-separated
    val ingredientsList: String, // Newline-separated, e.g. "200g | Flour", "50ml | Whole Milk"
    val instructionSteps: String, // Newline-separated steps
    val industrialNotes: String = "", // Fun lore-styled technical tips
    val isFavorite: Boolean = false,
    val imageUrl: String = ""
) : Serializable

@Entity(tableName = "meal_plans")
data class MealPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateString: String, // e.g. "2026-05-24", "2026-05-25"
    val mealType: String, // Breakfast, Lunch, Supper, Tea
    val recipeId: Int?, // Linked recipe, null means custom note
    val customNotes: String = "" // In case recipeId is null or for extra info
)

@Entity(tableName = "shopping_items")
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val quantity: Double,
    val unit: String,
    val category: String, // Pantry category
    val isPurchased: Boolean = false,
    val sourceRecipeId: Int? = null,
    val predictionScore: Double = 0.0 // Predictive shopping list score
)

@Entity(tableName = "household_profiles")
data class HouseholdProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val role: String, // Senior Chef, Pantry Administrator, Domestic Engineer, Diner
    val allergies: String = "", // Comma-separated
    val preferences: String = "" // e.g. Low Carbon, Traditional, High Protein
)
