package com.example.culinarycompanion

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch

/**
 * ViewModel that provides recipes to the UI and handles user actions.
 */
class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * Repository used to access data operations.
     */
    private val repository: RecipeRepository

    /**
     * LiveData list of all recipes to observe in the UI.
     */
    val allRecipes: LiveData<List<Recipe>>

    /**
     * Initializes the repository and loads all recipes.
     */
    init {
        val dao = RecipeDatabase.getDatabase(application).recipeDao()
        repository = RecipeRepository(dao)
        allRecipes = repository.allRecipes
    }

    /**
     * Inserts a new recipe.
     */
    fun insert(recipe: Recipe) = viewModelScope.launch {
        repository.insert(recipe)
    }

    /**
     * Updates an existing recipe.
     */
    fun update(recipe: Recipe) = viewModelScope.launch {
        repository.update(recipe)
    }

    /**
     * Deletes a recipe.
     */
    fun delete(recipe: Recipe) = viewModelScope.launch {
        repository.delete(recipe)
    }
}