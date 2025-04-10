//package com.example.nutritionapp.activities
//
//import android.os.Bundle
//import android.view.MenuItem
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.Observer
//import com.example.nutritionapp.App
//import com.example.nutritionapp.R
//import com.example.nutritionapp.database.entities.Favorite
//import com.example.nutritionapp.database.entities.Meal
//import com.example.nutritionapp.utils.Constants
//import kotlinx.android.synthetic.main.activity_meal_detail.*
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//class MealDetailActivity : AppCompatActivity() {
//
//    private val mealDao by lazy { App.getInstance().database.mealDao() }
//    private val favoriteDao by lazy { App.getInstance().database.favoriteDao() }
//    private val userId by lazy { App.getInstance().preferenceManager.getUserId() }
//
//    private var currentMeal: Meal? = null
//    private var isFavorite = false
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_meal_detail)
//
//        // Setup toolbar
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowHomeEnabled(true)
//
//        // Get meal ID from intent
//        val mealId = intent.getLongExtra(Constants.EXTRA_MEAL_ID, -1)
//        if (mealId == -1L) {
//            Toast.makeText(this, "Meal not found", Toast.LENGTH_SHORT).show()
//            finish()
//            return
//        }
//
//        // Load meal details
//        loadMeal(mealId)
//
//        // Setup favorite button
//        setupFavoriteButton(mealId)
//    }
//
//    private fun loadMeal(mealId: Long) {
//        mealDao.getMealById(mealId).observe(this, Observer { meal ->
//            if (meal != null) {
//                currentMeal = meal
//                displayMealDetails(meal)
//            } else {
//                Toast.makeText(this, "Meal not found", Toast.LENGTH_SHORT).show()
//                finish()
//            }
//        })
//    }
//
//    private fun displayMealDetails(meal: Meal) {
//        // Set collapsing toolbar title
//        collapsingToolbar.title = meal.name
//
//        // Load meal image if available
//        meal.image?.let { imageName ->
//            val resourceId = resources.getIdentifier(
//                imageName, "drawable", packageName
//            )
//            if (resourceId != 0) {
//                imageViewMeal.setImageResource(resourceId)
//            }
//        }
//
//        // Set meal details
//        textViewMealName.text = meal.name
//        textViewMealDescription.text = meal.description
//
//        // Set nutrition info
//        textViewCalories.text = meal.calories.toString()
//        textViewProteins.text = "${meal.proteins ?: 0}g"
//        textViewCarbs.text = "${meal.carbs ?: 0}g"
//        textViewFats.text = "${meal.fats ?: 0}g"
//
//        // Format ingredients (replace comma with new line)
//        val formattedIngredients = meal.ingredients
//            .replace(",", "\n")
//            .replace(";", "\n")
//        textViewIngredients.text = formattedIngredients
//
//        // Format instructions (add line numbers)
//        val instructions = meal.instructions
//            .split("\\. ".toRegex())
//            .filter { it.isNotBlank() }
//            .mapIndexed { index, instruction -> "${index + 1}. $instruction" }
//            .joinToString("\n")
//        textViewInstructions.text = instructions
//    }
//
//    private fun setupFavoriteButton(mealId: Long) {
//        // Check if meal is already a favorite
//        CoroutineScope(Dispatchers.IO).launch {
//            isFavorite = favoriteDao.isMealFavorite(userId, mealId)
//
//            withContext(Dispatchers.Main) {
//                updateFavoriteIcon()
//            }
//        }
//
//        // Set click listener for favorite button
//        fabFavorite.setOnClickListener {
//            toggleFavorite(mealId)
//        }
//
//        imageViewFavorite.setOnClickListener {
//            toggleFavorite(mealId)
//        }
//    }
//
//    private fun toggleFavorite(mealId: Long) {
//        CoroutineScope(Dispatchers.IO).launch {
//            if (isFavorite) {
//                // Remove from favorites
//                favoriteDao.deleteFavorite(userId, mealId)
//            } else {
//                // Add to favorites
//                val favorite = Favorite(userId = userId, mealId = mealId)
//                favoriteDao.insert(favorite)
//            }
//
//            // Update favorite status
//            isFavorite = !isFavorite
//
//            withContext(Dispatchers.Main) {
//                updateFavoriteIcon()
//                val message = if (isFavorite) "Added to favorites" else "Removed from favorites"
//                Toast.makeText(this@MealDetailActivity, message, Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun updateFavoriteIcon() {
//        val favoriteIcon = if (isFavorite) {
//            R.drawable.ic_favorite
//        } else {
//            R.drawable.ic_favorite_border
//        }
//
//        fabFavorite.setImageResource(favoriteIcon)
//        imageViewFavorite.setImageResource(favoriteIcon)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == android.R.id.home) {
//            onBackPressed()
//            return true
//        }
//        return super.onOptionsItemSelected(item)
//    }
//}

package com.example.nutritionapp.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.nutritionapp.App
import com.example.nutritionapp.R
import com.example.nutritionapp.database.entities.Favorite
import com.example.nutritionapp.database.entities.Meal
import com.example.nutritionapp.databinding.ActivityMealDetailBinding
import com.example.nutritionapp.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MealDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMealDetailBinding
    private val mealDao by lazy { App.getInstance().database.mealDao() }
    private val favoriteDao by lazy { App.getInstance().database.favoriteDao() }
    private val userId by lazy { App.getInstance().preferenceManager.getUserId() }

    private var currentMeal: Meal? = null
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Get meal ID from intent
        val mealId = intent.getLongExtra(Constants.EXTRA_MEAL_ID, -1)
        if (mealId == -1L) {
            Toast.makeText(this, "Meal not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Load meal details
        loadMeal(mealId)

        // Setup favorite button
        setupFavoriteButton(mealId)
    }

    private fun loadMeal(mealId: Long) {
        mealDao.getMealById(mealId).observe(this, Observer { meal ->
            if (meal != null) {
                currentMeal = meal
                displayMealDetails(meal)
            } else {
                Toast.makeText(this, "Meal not found", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    private fun displayMealDetails(meal: Meal) {
        // Set collapsing toolbar title
        binding.collapsingToolbar.title = meal.name

        // Load meal image if available
        meal.image?.let { imageName ->
            val resourceId = resources.getIdentifier(
                imageName, "drawable", packageName
            )
            if (resourceId != 0) {
                binding.imageViewMeal.setImageResource(resourceId)
            }
        }

        // Set meal details
        binding.textViewMealName.text = meal.name
        binding.textViewMealDescription.text = meal.description

        // Set nutrition info
        binding.textViewCalories.text = meal.calories.toString()
        binding.textViewProteins.text = "${meal.proteins ?: 0}g"
        binding.textViewCarbs.text = "${meal.carbs ?: 0}g"
        binding.textViewFats.text = "${meal.fats ?: 0}g"

        // Format ingredients (replace comma with new line)
        val formattedIngredients = meal.ingredients
            .replace(",", "\n")
            .replace(";", "\n")
        binding.textViewIngredients.text = formattedIngredients

        // Format instructions (add line numbers)
        val instructions = meal.instructions
            .split("\\. ".toRegex())
            .filter { it.isNotBlank() }
            .mapIndexed { index, instruction -> "${index + 1}. $instruction" }
            .joinToString("\n")
        binding.textViewInstructions.text = instructions
    }

    private fun setupFavoriteButton(mealId: Long) {
        // Check if meal is already a favorite
        CoroutineScope(Dispatchers.IO).launch {
            isFavorite = favoriteDao.isMealFavorite(userId, mealId)

            withContext(Dispatchers.Main) {
                updateFavoriteIcon()
            }
        }

        // Set click listener for favorite button
        binding.fabFavorite.setOnClickListener {
            toggleFavorite(mealId)
        }
    }

    private fun toggleFavorite(mealId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            if (isFavorite) {
                // Remove from favorites
                favoriteDao.deleteFavorite(userId, mealId)
            } else {
                // Add to favorites
                val favorite = Favorite(userId = userId, mealId = mealId)
                favoriteDao.insert(favorite)
            }

            // Update favorite status
            isFavorite = !isFavorite

            withContext(Dispatchers.Main) {
                updateFavoriteIcon()
                val message = if (isFavorite) "Added to favorites" else "Removed from favorites"
                Toast.makeText(this@MealDetailActivity, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateFavoriteIcon() {
        val favoriteIcon = if (isFavorite) {
            R.drawable.ic_favorite
        } else {
            R.drawable.ic_favorite_border
        }

        binding.fabFavorite.setImageResource(favoriteIcon)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}