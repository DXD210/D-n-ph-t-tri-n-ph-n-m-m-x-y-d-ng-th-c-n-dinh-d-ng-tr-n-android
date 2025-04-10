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
//import com.example.nutritionapp.models.MealTime
//import com.example.nutritionapp.utils.Constants
//import com.google.android.material.tabs.TabLayout
//import kotlinx.android.synthetic.main.fragment_daily_meal.*
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//class DailyMealFragment : Fragment(), MealAdapter.OnMealClickListener {
//
//    private val mealDao by lazy { App.getInstance().database.mealDao() }
//    private val favoriteDao by lazy { App.getInstance().database.favoriteDao() }
//    private val userId by lazy { App.getInstance().preferenceManager.getUserId() }
//
//    private lateinit var mealAdapter: MealAdapter
//    private var currentMealTime = MealTime.BREAKFAST.displayName
//    private var searchQuery = ""
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_daily_meal, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        setupRecyclerView()
//        setupTabLayout()
//        setupSearchView()
//
//        // Load initial data
//        loadMeals()
//    }
//
//    private fun setupRecyclerView() {
//        mealAdapter = MealAdapter(requireContext(), this)
//        recyclerViewMeals.apply {
//            layoutManager = LinearLayoutManager(requireContext())
//            adapter = mealAdapter
//        }
//    }
//
//    private fun setupTabLayout() {
//        // Initialize tabs
//        for (mealTime in MealTime.values()) {
//            tabLayout.addTab(tabLayout.newTab().setText(mealTime.displayName))
//        }
//
//        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab) {
//                currentMealTime = tab.text.toString()
//                loadMeals()
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab) {}
//
//            override fun onTabReselected(tab: TabLayout.Tab) {}
//        })
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
//                loadMeals()
//            }
//        })
//    }
//
//    private fun loadMeals() {
//        progressBar.visibility = View.VISIBLE
//
//        if (searchQuery.isEmpty()) {
//            // Load all meals for the current meal time
//            mealDao.getMealsByTime(currentMealTime).observe(viewLifecycleOwner, Observer { meals ->
//                updateMealsList(meals)
//            })
//        } else {
//            // Load filtered meals
//            mealDao.searchMealsByTime(currentMealTime, searchQuery).observe(viewLifecycleOwner, Observer { meals ->
//                updateMealsList(meals)
//            })
//        }
//    }
//
//    private fun updateMealsList(meals: List<Meal>) {
//        if (meals.isEmpty()) {
//            textViewEmpty.visibility = View.VISIBLE
//            recyclerViewMeals.visibility = View.GONE
//        } else {
//            textViewEmpty.visibility = View.GONE
//            recyclerViewMeals.visibility = View.VISIBLE
//
//            // Update favorites status for each meal
//            CoroutineScope(Dispatchers.IO).launch {
//                val mealsWithFavoriteStatus = meals.map { meal ->
//                    val isFavorite = favoriteDao.isMealFavorite(userId, meal.id)
//                    Pair(meal, isFavorite)
//                }
//
//                withContext(Dispatchers.Main) {
//                    mealAdapter.setMeals(mealsWithFavoriteStatus)
//                    progressBar.visibility = View.GONE
//                }
//            }
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
//        CoroutineScope(Dispatchers.IO).launch {
//            val isFavorite = favoriteDao.isMealFavorite(userId, meal.id)
//
//            if (isFavorite) {
//                // Remove from favorites
//                favoriteDao.deleteFavorite(userId, meal.id)
//            } else {
//                // Add to favorites
//                val favorite = com.example.nutritionapp.database.entities.Favorite(
//                    userId = userId,
//                    mealId = meal.id
//                )
//                favoriteDao.insert(favorite)
//            }
//
//            withContext(Dispatchers.Main) {
//                // Update adapter with new favorite status
//                mealAdapter.updateFavoriteStatus(position, !isFavorite)
//            }
//        }
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
import com.example.nutritionapp.databinding.FragmentDailyMealBinding
import com.example.nutritionapp.models.MealTime
import com.example.nutritionapp.utils.Constants
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DailyMealFragment : Fragment(), MealAdapter.OnMealClickListener {

    private lateinit var binding: FragmentDailyMealBinding
    private val mealDao by lazy { App.getInstance().database.mealDao() }
    private val favoriteDao by lazy { App.getInstance().database.favoriteDao() }
    private val userId by lazy { App.getInstance().preferenceManager.getUserId() }

    private lateinit var mealAdapter: MealAdapter
    private var currentMealTime = MealTime.BREAKFAST.displayName
    private var searchQuery = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDailyMealBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupTabLayout()
        setupSearchView()

        // Load initial data
        loadMeals()
    }

    private fun setupRecyclerView() {
        mealAdapter = MealAdapter(requireContext(), this)
        binding.recyclerViewMeals.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mealAdapter
        }
    }

    private fun setupTabLayout() {
        // Initialize tabs
        for (mealTime in MealTime.values()) {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(mealTime.displayName))
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                currentMealTime = tab.text.toString()
                loadMeals()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setupSearchView() {
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                searchQuery = s.toString().trim()
                loadMeals()
            }
        })
    }

    private fun loadMeals() {
        binding.progressBar.visibility = View.VISIBLE

        if (searchQuery.isEmpty()) {
            // Load all meals for the current meal time
            mealDao.getMealsByTime(currentMealTime).observe(viewLifecycleOwner, Observer { meals ->
                updateMealsList(meals)
            })
        } else {
            // Load filtered meals
            mealDao.searchMealsByTime(currentMealTime, searchQuery).observe(viewLifecycleOwner, Observer { meals ->
                updateMealsList(meals)
            })
        }
    }

    private fun updateMealsList(meals: List<Meal>) {
        if (meals.isEmpty()) {
            binding.textViewEmpty.visibility = View.VISIBLE
            binding.recyclerViewMeals.visibility = View.GONE
        } else {
            binding.textViewEmpty.visibility = View.GONE
            binding.recyclerViewMeals.visibility = View.VISIBLE

            // Update favorites status for each meal
            CoroutineScope(Dispatchers.IO).launch {
                val mealsWithFavoriteStatus = meals.map { meal ->
                    val isFavorite = favoriteDao.isMealFavorite(userId, meal.id)
                    Pair(meal, isFavorite)
                }

                withContext(Dispatchers.Main) {
                    mealAdapter.setMeals(mealsWithFavoriteStatus)
                    binding.progressBar.visibility = View.GONE
                }
            }
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
        CoroutineScope(Dispatchers.IO).launch {
            val isFavorite = favoriteDao.isMealFavorite(userId, meal.id)

            if (isFavorite) {
                // Remove from favorites
                favoriteDao.deleteFavorite(userId, meal.id)
            } else {
                // Add to favorites
                val favorite = com.example.nutritionapp.database.entities.Favorite(
                    userId = userId,
                    mealId = meal.id
                )
                favoriteDao.insert(favorite)
            }

            withContext(Dispatchers.Main) {
                // Update adapter with new favorite status
                mealAdapter.updateFavoriteStatus(position, !isFavorite)
            }
        }
    }
}