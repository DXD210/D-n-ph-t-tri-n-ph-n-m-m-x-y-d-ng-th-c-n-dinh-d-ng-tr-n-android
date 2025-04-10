//package com.example.nutritionapp.adapters
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.example.nutritionapp.R
//import com.example.nutritionapp.database.entities.Meal
//import kotlinx.android.synthetic.main.item_meal.view.*
//
//class MealAdapter(
//    private val context: Context,
//    private val listener: OnMealClickListener
//) : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {
//
//    private val meals = mutableListOf<Pair<Meal, Boolean>>() // Meal and its favorite status
//
//    interface OnMealClickListener {
//        fun onMealClick(meal: Meal)
//        fun onFavoriteClick(meal: Meal, position: Int)
//    }
//
//    fun setMeals(newMeals: List<Pair<Meal, Boolean>>) {
//        meals.clear()
//        meals.addAll(newMeals)
//        notifyDataSetChanged()
//    }
//
//    fun updateFavoriteStatus(position: Int, isFavorite: Boolean) {
//        if (position in 0 until meals.size) {
//            meals[position] = Pair(meals[position].first, isFavorite)
//            notifyItemChanged(position)
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
//        val view = LayoutInflater.from(context).inflate(R.layout.item_meal, parent, false)
//        return MealViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
//        val (meal, isFavorite) = meals[position]
//        holder.bind(meal, isFavorite)
//    }
//
//    override fun getItemCount(): Int = meals.size
//
//    inner class MealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        fun bind(meal: Meal, isFavorite: Boolean) {
//            // Set meal name
//            itemView.textViewMealName.text = meal.name
//
//            // Set meal description
//            itemView.textViewMealDescription.text = meal.description
//
//            // Set calories
//            itemView.textViewCalories.text = "${meal.calories} kcal"
//
//            // Set category
//            itemView.textViewCategory.text = meal.category
//
//            // Set meal image if available
//            meal.image?.let { imageName ->
//                val resourceId = context.resources.getIdentifier(
//                    imageName, "drawable", context.packageName
//                )
//                if (resourceId != 0) {
//                    itemView.imageViewMeal.setImageResource(resourceId)
//                } else {
//                    // Set a default image if the specified image is not found
//                    itemView.imageViewMeal.setImageResource(R.drawable.default_meal_image)
//                }
//            } ?: run {
//                // Set a default image if no image is specified
//                itemView.imageViewMeal.setImageResource(R.drawable.default_meal_image)
//            }
//
//            // Set favorite icon
//            val favoriteIcon = if (isFavorite) {
//                R.drawable.ic_favorite
//            } else {
//                R.drawable.ic_favorite_border
//            }
//            itemView.imageViewFavorite.setImageResource(favoriteIcon)
//
//            // Set category tag color based on meal time
//            val categoryColor = when (meal.mealTime) {
//                "Breakfast" -> R.color.breakfast_color
//                "Lunch" -> R.color.lunch_color
//                "Dinner" -> R.color.dinner_color
//                "Snack" -> R.color.snack_color
//                else -> R.color.primary
//            }
//            itemView.textViewCategory.setBackgroundColor(context.getColor(categoryColor))
//
//            // Set click listeners
//            itemView.setOnClickListener {
//                listener.onMealClick(meal)
//            }
//
//            itemView.imageViewFavorite.setOnClickListener {
//                listener.onFavoriteClick(meal, adapterPosition)
//            }
//        }
//    }
//}

package com.example.nutritionapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nutritionapp.R
import com.example.nutritionapp.database.entities.Meal
import com.example.nutritionapp.databinding.ItemMealBinding

class MealAdapter(
    private val context: Context,
    private val listener: OnMealClickListener
) : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    private val meals = mutableListOf<Pair<Meal, Boolean>>() // Meal and its favorite status

    interface OnMealClickListener {
        fun onMealClick(meal: Meal)
        fun onFavoriteClick(meal: Meal, position: Int)
    }

    fun setMeals(newMeals: List<Pair<Meal, Boolean>>) {
        meals.clear()
        meals.addAll(newMeals)
        notifyDataSetChanged()
    }

    fun updateFavoriteStatus(position: Int, isFavorite: Boolean) {
        if (position in 0 until meals.size) {
            meals[position] = Pair(meals[position].first, isFavorite)
            notifyItemChanged(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val binding = ItemMealBinding.inflate(LayoutInflater.from(context), parent, false)
        return MealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val (meal, isFavorite) = meals[position]
        holder.bind(meal, isFavorite)
    }

    override fun getItemCount(): Int = meals.size

    inner class MealViewHolder(private val binding: ItemMealBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(meal: Meal, isFavorite: Boolean) {
            // Set meal name
            binding.textViewMealName.text = meal.name

            // Set meal description
            binding.textViewMealDescription.text = meal.description

            // Set calories
            binding.textViewCalories.text = "${meal.calories} kcal"

            // Set category
            binding.textViewCategory.text = meal.category

            // Set meal image if available
            meal.image?.let { imageName ->
                val resourceId = context.resources.getIdentifier(
                    imageName, "drawable", context.packageName
                )
                if (resourceId != 0) {
                    binding.imageViewMeal.setImageResource(resourceId)
                } else {
                    // Set a default image if the specified image is not found
                    binding.imageViewMeal.setImageResource(R.drawable.default_meal_image)
                }
            } ?: run {
                // Set a default image if no image is specified
                binding.imageViewMeal.setImageResource(R.drawable.default_meal_image)
            }

            // Set favorite icon
            val favoriteIcon = if (isFavorite) {
                R.drawable.ic_favorite
            } else {
                R.drawable.ic_favorite_border
            }
            binding.imageViewFavorite.setImageResource(favoriteIcon)

            // Set category tag color based on meal time
            val categoryColor = when (meal.mealTime) {
                "Breakfast" -> R.color.breakfast_color
                "Lunch" -> R.color.lunch_color
                "Dinner" -> R.color.dinner_color
                "Snack" -> R.color.snack_color
                else -> R.color.primary
            }
            binding.textViewCategory.setBackgroundColor(context.getColor(categoryColor))

            // Set click listeners
            binding.root.setOnClickListener {
                listener.onMealClick(meal)
            }

            binding.imageViewFavorite.setOnClickListener {
                listener.onFavoriteClick(meal, adapterPosition)
            }
        }
    }
}