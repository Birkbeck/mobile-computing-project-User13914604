package com.example.culinarycompanion

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,  // Unique ID for each recipe
    val title: String, // Recipe name
    val ingredients: String,  // Ingredients list as a single string

    val instructions: String,
    val category: String
)


