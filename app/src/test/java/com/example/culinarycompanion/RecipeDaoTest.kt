package com.example.culinarycompanion

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for RecipeDao database operations.
 */
class RecipeDaoTest {

    // Allows LiveData to execute synchronously during tests
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RecipeDatabase
    private lateinit var recipeDao: RecipeDao

    /**
     * Sets up an in-memory Room database before each test.
     */
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RecipeDatabase::class.java
        ).allowMainThreadQueries().build()
        recipeDao = database.recipeDao()
    }

    /**
     * Closes the database after each test.
     */
    @After
    fun teardown() {
        database.close()
    }

    /**
     * Test inserting a recipe and verifying it is stored.
     */
    @Test
    fun insertRecipe_savesToDatabase() = runBlocking {
        val recipe = Recipe(
            title = "Pancakes",
            ingredients = "Flour, Eggs, Milk",
            instructions = "Mix and cook",
            category = "Breakfast"
        )
        recipeDao.insert(recipe)

        val allRecipes = recipeDao.getAllRecipes().getOrAwaitValue()
        assertEquals(1, allRecipes.size)
        assertEquals("Pancakes", allRecipes[0].title)
    }
}