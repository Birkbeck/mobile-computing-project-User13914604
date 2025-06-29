package com.example.culinarycompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.culinarycompanion.ui.theme.CulinaryCompanionTheme
import androidx.compose.runtime.livedata.observeAsState




/**
 * Main activity that hosts the Compose screens.
 */
class MainActivity : ComponentActivity() {
    private val recipeViewModel: RecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CulinaryCompanionTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    CulinaryCompanionApp(recipeViewModel)
                }
            }
        }
    }
}

/**
 * Root Composable that shows either the recipe list or the recipe details screen.
 */
@Composable
fun CulinaryCompanionApp(recipeViewModel: RecipeViewModel) {
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }

    val recipes = recipeViewModel.allRecipes.observeAsState(emptyList()).value

    if (selectedRecipe == null) {
        RecipeListScreen(
            recipes = recipes,
            onAddClick = { selectedRecipe = Recipe(0, "", "", "", "") },
            onRecipeClick = { recipe -> selectedRecipe = recipe }
        )
    } else {
        RecipeDetailScreen(
            recipe = selectedRecipe!!,
            onSave = { recipe ->
                if (recipe.id == 0) {
                    recipeViewModel.insert(recipe)
                } else {
                    recipeViewModel.update(recipe)
                }
                selectedRecipe = null
            },
            onDelete = { recipe ->
                recipeViewModel.delete(recipe)
                selectedRecipe = null
            },
            onCancel = { selectedRecipe = null }
        )
    }
}

/**
 * Composable displaying the list of recipes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    recipes: List<Recipe>,
    onAddClick: () -> Unit,
    onRecipeClick: (Recipe) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Culinary Companion") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Text("+")
            }
        }
    ) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            items(recipes) { recipe ->
                ListItem(
                    headlineContent = { Text(recipe.title) },
                    supportingContent = { Text(recipe.category) },
                    modifier = Modifier
                        .clickable { onRecipeClick(recipe) }
                        .fillMaxWidth()
                )
                HorizontalDivider()
            }
        }
    }
}

/**
 * Composable displaying the form to add or edit a recipe.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RecipeDetailScreen(
    recipe: Recipe,
    onSave: (Recipe) -> Unit,
    onDelete: (Recipe) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf(recipe.title) }
    var ingredients by remember { mutableStateOf(recipe.ingredients) }
    var instructions by remember { mutableStateOf(recipe.instructions) }
    var category by remember { mutableStateOf(recipe.category) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recipe Details") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Text("<")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = ingredients,
                onValueChange = { ingredients = it },
                label = { Text("Ingredients") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = instructions,
                onValueChange = { instructions = it },
                label = { Text("Instructions") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Button(
                    onClick = {
                        onSave(
                            recipe.copy(
                                title = title,
                                ingredients = ingredients,
                                instructions = instructions,
                                category = category
                            )
                        )
                    }
                ) {
                    Text("Save")
                }
                Spacer(modifier = Modifier.width(8.dp))
                if (recipe.id != 0) {
                    Button(
                        onClick = { onDelete(recipe) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}
