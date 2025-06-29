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
