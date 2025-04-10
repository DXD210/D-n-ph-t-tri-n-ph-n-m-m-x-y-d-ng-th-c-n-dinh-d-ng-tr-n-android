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
//import com.example.nutritionapp.adapters.NutritionPlanAdapter
//import com.example.nutritionapp.database.entities.Meal
//import com.example.nutritionapp.database.entities.NutritionPlan
//import com.example.nutritionapp.utils.Constants
//import com.google.android.material.chip.Chip
//import kotlinx.android.synthetic.main.fragment_nutrition_plan.*
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//class NutritionPlanFragment : Fragment(), NutritionPlanAdapter.OnPlanClickListener {
//
//    private val nutritionPlanDao by lazy { App.getInstance().database.nutritionPlanDao() }
//    private val mealDao by lazy { App.getInstance().database.mealDao() }
//    private val preferenceManager by lazy { App.getInstance().preferenceManager }
//
//    private lateinit var planAdapter: NutritionPlanAdapter
//    private var currentGenderFilter: String? = null
//    private var searchQuery = ""
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_nutrition_plan, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        setupRecyclerView()
//        setupChipGroup()
//        setupSearchView()
//
//        // Load initial data
//        loadNutritionPlans()
//    }
//
//    private fun setupRecyclerView() {
//        planAdapter = NutritionPlanAdapter(requireContext(), this)
//        recyclerViewPlans.apply {
//            layoutManager = LinearLayoutManager(requireContext())
//            adapter = planAdapter
//        }
//    }
//
//    private fun setupChipGroup() {
//        chipGroupFilter.setOnCheckedChangeListener { _, checkedId ->
//            currentGenderFilter = when (checkedId) {
//                R.id.chipMale -> "Male"
//                R.id.chipFemale -> "Female"
//                else -> null
//            }
//            loadNutritionPlans()
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
//                loadNutritionPlans()
//            }
//        })
//    }
//
//    private fun loadNutritionPlans() {
//        progressBar.visibility = View.VISIBLE
//
//        // Apply gender filter if selected
//        if (currentGenderFilter != null && preferenceManager.getUserId() > 0) {
//            // Get user's age from preferences or database
//            val userAge = 30 // Default age, can be retrieved from user profile
//
//            nutritionPlanDao.getNutritionPlansByGenderAndAge(currentGenderFilter!!, userAge)
//                .observe(viewLifecycleOwner, Observer { plans ->
//                    handlePlansResult(plans)
//                })
//        } else if (searchQuery.isNotEmpty()) {
//            nutritionPlanDao.searchNutritionPlans(searchQuery)
//                .observe(viewLifecycleOwner, Observer { plans ->
//                    handlePlansResult(plans)
//                })
//        } else {
//            nutritionPlanDao.getAllNutritionPlans()
//                .observe(viewLifecycleOwner, Observer { plans ->
//                    handlePlansResult(plans)
//                })
//        }
//    }
//
//    private fun handlePlansResult(plans: List<NutritionPlan>) {
//        if (plans.isEmpty()) {
//            textViewEmpty.visibility = View.VISIBLE
//            recyclerViewPlans.visibility = View.GONE
//            progressBar.visibility = View.GONE
//        } else {
//            textViewEmpty.visibility = View.GONE
//            recyclerViewPlans.visibility = View.VISIBLE
//
//            // Prepare plans with meal counts
//            processPlansWithMealCounts(plans)
//        }
//    }
//
//    private fun processPlansWithMealCounts(plans: List<NutritionPlan>) {
//        CoroutineScope(Dispatchers.IO).launch {
//            val plansWithMealCounts = plans.map { plan ->
//                val mealIds = plan.meals.split(",").mapNotNull { it.trim().toLongOrNull() }
//                Pair(plan, mealIds.size)
//            }
//
//            withContext(Dispatchers.Main) {
//                planAdapter.setPlans(plansWithMealCounts)
//                progressBar.visibility = View.GONE
//            }
//        }
//    }
//
//    override fun onPlanClick(plan: NutritionPlan) {
//        // Show meals for this nutrition plan
//        showPlanMeals(plan)
//    }
//
//    private fun showPlanMeals(plan: NutritionPlan) {
//        // Get meal IDs from plan
//        val mealIds = plan.meals.split(",").mapNotNull { it.trim().toLongOrNull() }
//
//        if (mealIds.isEmpty()) {
//            return
//        }
//
//        // Load meals for this plan
//        mealDao.getMealsByIds(mealIds).observe(viewLifecycleOwner, Observer { meals ->
//            // Show dialog with meals from this plan
//            // Here you could navigate to a new screen to show the meals
//            // or show a bottom sheet with the meal list
//
//            // For simplicity, we'll just show the first meal detail
//            if (meals.isNotEmpty()) {
//                navigateToMealDetail(meals.first())
//            }
//        })
//    }
//
//    private fun navigateToMealDetail(meal: Meal) {
//        val intent = Intent(requireContext(), MealDetailActivity::class.java).apply {
//            putExtra(Constants.EXTRA_MEAL_ID, meal.id)
//        }
//        startActivity(intent)
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
import com.example.nutritionapp.adapters.NutritionPlanAdapter
import com.example.nutritionapp.database.entities.Meal
import com.example.nutritionapp.database.entities.NutritionPlan
import com.example.nutritionapp.databinding.FragmentNutritionPlanBinding
import com.example.nutritionapp.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NutritionPlanFragment : Fragment(), NutritionPlanAdapter.OnPlanClickListener {

    private val nutritionPlanDao by lazy { App.getInstance().database.nutritionPlanDao() }
    private val mealDao by lazy { App.getInstance().database.mealDao() }
    private val preferenceManager by lazy { App.getInstance().preferenceManager }

    private lateinit var binding: FragmentNutritionPlanBinding
    private lateinit var planAdapter: NutritionPlanAdapter
    private var currentGenderFilter: String? = null
    private var searchQuery = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNutritionPlanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupChipGroup()
        setupSearchView()

        // Load initial data
        loadNutritionPlans()
    }

    private fun setupRecyclerView() {
        planAdapter = NutritionPlanAdapter(requireContext(), this)
        binding.recyclerViewPlans.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = planAdapter
        }
    }

    private fun setupChipGroup() {
        binding.chipGroupFilter.setOnCheckedChangeListener { _, checkedId ->
            currentGenderFilter = when (checkedId) {
                R.id.chipMale -> "Male"
                R.id.chipFemale -> "Female"
                else -> null
            }
            loadNutritionPlans()
        }
    }

    private fun setupSearchView() {
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                searchQuery = s.toString().trim()
                loadNutritionPlans()
            }
        })
    }

    private fun loadNutritionPlans() {
        binding.progressBar.visibility = View.VISIBLE

        // Apply gender filter if selected
        if (currentGenderFilter != null && preferenceManager.getUserId() > 0) {
            // Get user's age from preferences or database
            val userAge = 30 // Default age, can be retrieved from user profile

            nutritionPlanDao.getNutritionPlansByGenderAndAge(currentGenderFilter!!, userAge)
                .observe(viewLifecycleOwner, Observer { plans ->
                    handlePlansResult(plans)
                })
        } else if (searchQuery.isNotEmpty()) {
            nutritionPlanDao.searchNutritionPlans(searchQuery)
                .observe(viewLifecycleOwner, Observer { plans ->
                    handlePlansResult(plans)
                })
        } else {
            nutritionPlanDao.getAllNutritionPlans()
                .observe(viewLifecycleOwner, Observer { plans ->
                    handlePlansResult(plans)
                })
        }
    }

    private fun handlePlansResult(plans: List<NutritionPlan>) {
        if (plans.isEmpty()) {
            binding.textViewEmpty.visibility = View.VISIBLE
            binding.recyclerViewPlans.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
        } else {
            binding.textViewEmpty.visibility = View.GONE
            binding.recyclerViewPlans.visibility = View.VISIBLE

            // Prepare plans with meal counts
            processPlansWithMealCounts(plans)
        }
    }

    private fun processPlansWithMealCounts(plans: List<NutritionPlan>) {
        CoroutineScope(Dispatchers.IO).launch {
            val plansWithMealCounts = plans.map { plan ->
                val mealIds = plan.meals.split(",").mapNotNull { it.trim().toLongOrNull() }
                Pair(plan, mealIds.size)
            }

            withContext(Dispatchers.Main) {
                planAdapter.setPlans(plansWithMealCounts)
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onPlanClick(plan: NutritionPlan) {
        // Show meals for this nutrition plan
        showPlanMeals(plan)
    }

    private fun showPlanMeals(plan: NutritionPlan) {
        // Get meal IDs from plan
        val mealIds = plan.meals.split(",").mapNotNull { it.trim().toLongOrNull() }

        if (mealIds.isEmpty()) {
            return
        }

        // Load meals for this plan
        mealDao.getMealsByIds(mealIds).observe(viewLifecycleOwner, Observer { meals ->
            // Show dialog with meals from this plan
            // Here you could navigate to a new screen to show the meals
            // or show a bottom sheet with the meal list

            // For simplicity, we'll just show the first meal detail
            if (meals.isNotEmpty()) {
                navigateToMealDetail(meals.first())
            }
        })
    }

    private fun navigateToMealDetail(meal: Meal) {
        val intent = Intent(requireContext(), MealDetailActivity::class.java).apply {
            putExtra(Constants.EXTRA_MEAL_ID, meal.id)
        }
        startActivity(intent)
    }
}