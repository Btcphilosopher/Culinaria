package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface KitchenDao {
    // Recipes
    @Query("SELECT * FROM recipes ORDER BY id DESC")
    fun getAllRecipes(): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: Int): Recipe?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe)

    @Update
    suspend fun updateRecipe(recipe: Recipe)

    @Query("DELETE FROM recipes WHERE id = :id")
    suspend fun deleteRecipeById(id: Int)

    // Pantry
    @Query("SELECT * FROM pantry_items ORDER BY expiryDate ASC, name ASC")
    fun getAllPantryItems(): Flow<List<PantryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPantryItem(item: PantryItem)

    @Delete
    suspend fun deletePantryItem(item: PantryItem)

    @Query("DELETE FROM pantry_items WHERE id = :id")
    suspend fun deletePantryItemById(id: Int)

    // Meal Plan
    @Query("SELECT * FROM meal_plans ORDER BY dateString ASC")
    fun getAllMealPlans(): Flow<List<MealPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealPlan(plan: MealPlan)

    @Delete
    suspend fun deleteMealPlan(plan: MealPlan)

    @Query("DELETE FROM meal_plans WHERE id = :id")
    suspend fun deleteMealPlanById(id: Int)

    // Shopping List
    @Query("SELECT * FROM shopping_items ORDER BY isPurchased ASC, category ASC, name ASC")
    fun getShoppingList(): Flow<List<ShoppingItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingItem(item: ShoppingItem)

    @Update
    suspend fun updateShoppingItem(item: ShoppingItem)

    @Delete
    suspend fun deleteShoppingItem(item: ShoppingItem)

    @Query("DELETE FROM shopping_items WHERE isPurchased = 1")
    suspend fun clearPurchasedShoppingItems()

    // Household Profiles
    @Query("SELECT * FROM household_profiles ORDER BY id ASC")
    fun getProfiles(): Flow<List<HouseholdProfile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: HouseholdProfile)

    @Delete
    suspend fun deleteProfile(profile: HouseholdProfile)
}
