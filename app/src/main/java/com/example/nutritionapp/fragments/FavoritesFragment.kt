//package com.example.nutritionapp.fragments
//
//import android.content.Intent
//import android.os.Bundle
//import android.text.Editable
//import android.text.TextWatcher
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.Observer
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.nutritionapp.App
//import com.example.nutritionapp.R
//import com.example.nutritionapp.activities.MealDetailActivity
//import com.example.nutritionapp.adapters.MealAdapter
//import com.example.nutritionapp.database.entities.Meal
//import com.example.nutritionapp.utils.Constants
//import kotlinx.android.synthetic.main.fragment_favorites.*
//
//class FavoritesFragment : Fragment(), MealAdapter.OnMealClickListener {
//
//    private val favoriteDao by lazy { App.getInstance().database.favoriteDao() }
//    private val userId by lazy { App.getInstance().preferenceManager.getUserId() }
//
//    private lateinit var mealAdapter: MealAdapter
//    private var searchQuery = ""
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_favorites, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        setupRecyclerView()
//        setupSearchView()
//
//        // Load initial data
//        loadFavorites()
//    }
//
//    private fun setupRecyclerView() {
//        mealAdapter = MealAdapter(requireContext(), this)
//        recyclerViewFavorites.apply {
//            layoutManager = LinearLayoutManager(requireContext())
//            adapter = mealAdapter
//        }
//    }
//
//    private fun setupSearchView() {
//        editTextSearch.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//
//            override fun afterTextChanged(s: Editable?) {
//                searchQuery = s.toString().trim()
//                loadFavorites()
//            }
//        })
//    }
//
//    private fun loadFavorites() {
//        progressBar.visibility = View.VISIBLE
//
//        if (searchQuery.isEmpty()) {
//            // Load all favorites
//            favoriteDao.getFavoriteMealsByUserId(userId).observe(viewLifecycleOwner, Observer { meals ->
//                updateMealsList(meals)
//            })
//        } else {
//            // Load filtered favorites
//            favoriteDao.searchFavoriteMealsByUserId(userId, searchQuery).observe(viewLifecycleOwner, Observer { meals ->
//                updateMealsList(meals)
//            })
//        }
//    }
//
//    private fun updateMealsList(meals: List<Meal>) {
//        progressBar.visibility = View.GONE
//
//        if (meals.isEmpty()) {
//            textViewEmpty.visibility = View.VISIBLE
//            recyclerViewFavorites.visibility = View.GONE
//        } else {
//            textViewEmpty.visibility = View.GONE
//            recyclerViewFavorites.visibility = View.VISIBLE
//
//            // All meals are favorites here
//            val mealsWithFavoriteStatus = meals.map { meal ->
//                Pair(meal, true)
//            }
//
//            mealAdapter.setMeals(mealsWithFavoriteStatus)
//        }
//    }
//
//    override fun onMealClick(meal: Meal) {
//        // Navigate to meal detail screen
//        val intent = Intent(requireContext(), MealDetailActivity::class.java).apply {
//            putExtra(Constants.EXTRA_MEAL_ID, meal.id)
//        }
//        startActivity(intent)
//    }
//
//    override fun onFavoriteClick(meal: Meal, position: Int) {
//        // Remove from favorites
//        favoriteDao.deleteFavorite(userId, meal.id)
//
//        // No need to update adapter, as the LiveData observer will reload the list
//    }
//
//    override fun onResume() {
//        super.onResume()
//        // Reload favorites in case they've changed (e.g., from meal detail screen)
//        loadFavorites()
//    }
//}

package com.example.nutritionapp.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nutritionapp.App
import com.example.nutritionapp.R
import com.example.nutritionapp.activities.MealDetailActivity
import com.example.nutritionapp.adapters.MealAdapter
import com.example.nutritionapp.database.entities.Meal
import com.example.nutritionapp.databinding.FragmentFavoritesBinding
import com.example.nutritionapp.utils.Constants

class FavoritesFragment : Fragment(), MealAdapter.OnMealClickListener {

    private lateinit var binding: FragmentFavoritesBinding
    private val favoriteDao by lazy { App.getInstance().database.favoriteDao() }
    private val userId by lazy { App.getInstance().preferenceManager.getUserId() }

    private lateinit var mealAdapter: MealAdapter
    private var searchQuery = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchView()

        // Load initial data
        loadFavorites()
    }

    private fun setupRecyclerView() {
        mealAdapter = MealAdapter(requireContext(), this)
        binding.recyclerViewFavorites.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mealAdapter
        }
    }

    private fun setupSearchView() {
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                searchQuery = s.toString().trim()
                loadFavorites()
            }
        })
    }

    private fun loadFavorites() {
        binding.progressBar.visibility = View.VISIBLE

        if (searchQuery.isEmpty()) {
            // Load all favorites
            favoriteDao.getFavoriteMealsByUserId(userId).observe(viewLifecycleOwner, Observer { meals ->
                updateMealsList(meals)
            })
        } else {
            // Load filtered favorites
            favoriteDao.searchFavoriteMealsByUserId(userId, searchQuery).observe(viewLifecycleOwner, Observer { meals ->
                updateMealsList(meals)
            })
        }
    }

    private fun updateMealsList(meals: List<Meal>) {
        binding.progressBar.visibility = View.GONE

        if (meals.isEmpty()) {
            binding.textViewEmpty.visibility = View.VISIBLE
            binding.recyclerViewFavorites.visibility = View.GONE
        } else {
            binding.textViewEmpty.visibility = View.GONE
            binding.recyclerViewFavorites.visibility = View.VISIBLE

            // All meals are favorites here
            val mealsWithFavoriteStatus = meals.map { meal ->
                Pair(meal, true)
            }

            mealAdapter.setMeals(mealsWithFavoriteStatus)
        }
    }

    override fun onMealClick(meal: Meal) {
        // Navigate to meal detail screen
        val intent = Intent(requireContext(), MealDetailActivity::class.java).apply {
            putExtra(Constants.EXTRA_MEAL_ID, meal.id)
        }
        startActivity(intent)
    }

    override fun onFavoriteClick(meal: Meal, position: Int) {
        // Remove from favorites
        favoriteDao.deleteFavorite(userId, meal.id)

        // No need to update adapter, as the LiveData observer will reload the list
    }

    override fun onResume() {
        super.onResume()
        // Reload favorites in case they've changed (e.g., from meal detail screen)
        loadFavorites()
    }
}