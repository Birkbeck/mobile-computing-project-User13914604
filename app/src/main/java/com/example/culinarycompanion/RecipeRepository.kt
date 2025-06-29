package com.example.culinarycompanion

import androidx.lifecycle.LiveData

/**
 * Repository that manages Recipe data operations.
 */

class RecipeRepository(private val recipeDao: RecipeDao) {

    // LiveData list of all recipes

    val allRecipes: LiveData<List<Recipe>> = recipeDao.getAllRecipes()

    suspend fun insert(recipe: Recipe) {
        recipeDao.insert(recipe)
    }

    suspend fun update(recipe: Recipe) {
        recipeDao.update(recipe)
    }

    suspend fun delete(recipe: Recipe) {
        recipeDao.delete(recipe)
    }
}