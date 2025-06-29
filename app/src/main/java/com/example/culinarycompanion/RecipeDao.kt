package com.example.culinarycompanion

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * DAO for accessing Recipe data in the database.
 */

@Dao
interface RecipeDao {

    /**
     * Insert a new recipe.
     */

    @Insert
    suspend fun insert(recipe: Recipe)

    /**
     * Update an existing recipe.
     */

    @Update
    suspend fun update(recipe: Recipe)

    /**
     * Delete a recipe.
     */

    @Delete
    suspend fun delete(recipe: Recipe)

    /**
     * Get all recipes ordered by title.
     */

    @Query("SELECT * FROM recipes ORDER BY title ASC")
    fun getAllRecipes(): LiveData<List<Recipe>>
}