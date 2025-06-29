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
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * Unit tests for RecipeDao database operations.
 * This test class uses an in-memory Room database for isolated testing.
 */
class RecipeDaoTest {

    // Allows LiveData to emit values immediately during tests
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RecipeDatabase
    private lateinit var recipeDao: RecipeDao

    /**
     * Sets up a new in-memory database instance before each test.
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
     * Closes the database after each test to clean up resources.
     */
    @After
    fun teardown() {
        database.close()
    }

    /**
     * Test inserting a recipe and verifying it is stored correctly.
     */
    @Test
    fun insertRecipe_savesToDatabase() = runBlocking {
        // Arrange
        val recipe = Recipe(
            title = "Pancakes",
            ingredients = "Flour, Eggs, Milk",
            instructions = "Mix and cook",
            category = "Breakfast"
        )

        // Act
        recipeDao.insert(recipe)

        // Assert
        val allRecipes = recipeDao.getAllRecipes().getOrAwaitValue()
        assertEquals(1, allRecipes.size)
        assertEquals("Pancakes", allRecipes[0].title)
    }

    /**
     * Test updating a recipe and verifying the updated data is stored.
     */
    @Test
    fun updateRecipe_updatesFields() = runBlocking {
        // Arrange
        val recipe = Recipe(
            title = "Salad",
            ingredients = "Lettuce",
            instructions = "Mix",
            category = "Lunch"
        )
        recipeDao.insert(recipe)
        val inserted = recipeDao.getAllRecipes().getOrAwaitValue().first()

        // Act
        val updated = inserted.copy(title = "Greek Salad")
        recipeDao.update(updated)

        // Assert
        val allRecipes = recipeDao.getAllRecipes().getOrAwaitValue()
        assertEquals("Greek Salad", allRecipes[0].title)
    }

    /**
     * Test deleting a recipe and verifying it is removed.
     */
    @Test
    fun deleteRecipe_removesFromDatabase() = runBlocking {
        // Arrange
        val recipe = Recipe(
            title = "Soup",
            ingredients = "Water",
            instructions = "Boil",
            category = "Dinner"
        )
        recipeDao.insert(recipe)
        val inserted = recipeDao.getAllRecipes().getOrAwaitValue().first()

        // Act
        recipeDao.delete(inserted)

        // Assert
        val allRecipes = recipeDao.getAllRecipes().getOrAwaitValue()
        assertTrue(allRecipes.isEmpty())
    }

    /**
     * Test retrieving multiple recipes returns all entries.
     */
    @Test
    fun getAllRecipes_returnsAllRecipes() = runBlocking {
        // Arrange
        val recipe1 = Recipe(
            title = "Pasta",
            ingredients = "Noodles",
            instructions = "Cook",
            category = "Dinner"
        )
        val recipe2 = Recipe(
            title = "Cake",
            ingredients = "Flour, Sugar",
            instructions = "Bake",
            category = "Dessert"
        )
        recipeDao.insert(recipe1)
        recipeDao.insert(recipe2)

        // Act
        val allRecipes = recipeDao.getAllRecipes().getOrAwaitValue()

        // Assert
        assertEquals(2, allRecipes.size)
    }
}

/**
 * Helper extension function to get LiveData values in tests.
 * Waits for a value to be emitted or times out.
 */
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(t: T?) {
            data = t
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)
    if (!latch.await(time, timeUnit)) {
        throw TimeoutException("LiveData value was never set.")
    }
    @Suppress("UNCHECKED_CAST")
    return data as T
}