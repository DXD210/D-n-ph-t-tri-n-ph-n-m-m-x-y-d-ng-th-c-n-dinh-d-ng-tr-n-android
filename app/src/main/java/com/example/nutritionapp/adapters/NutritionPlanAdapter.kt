//package com.example.nutritionapp.adapters
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.example.nutritionapp.R
//import com.example.nutritionapp.database.entities.NutritionPlan
//import kotlinx.android.synthetic.main.item_nutrition_plan.view.*
//
//class NutritionPlanAdapter(
//    private val context: Context,
//    private val listener: OnPlanClickListener
//) : RecyclerView.Adapter<NutritionPlanAdapter.PlanViewHolder>() {
//
//    private val plans = mutableListOf<Pair<NutritionPlan, Int>>() // Plan and meal count
//
//    interface OnPlanClickListener {
//        fun onPlanClick(plan: NutritionPlan)
//    }
//
//    fun setPlans(newPlans: List<Pair<NutritionPlan, Int>>) {
//        plans.clear()
//        plans.addAll(newPlans)
//        notifyDataSetChanged()
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
//        val view = LayoutInflater.from(context).inflate(R.layout.item_nutrition_plan, parent, false)
//        return PlanViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
//        val (plan, mealCount) = plans[position]
//        holder.bind(plan, mealCount)
//    }
//
//    override fun getItemCount(): Int = plans.size
//
//    inner class PlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        fun bind(plan: NutritionPlan, mealCount: Int) {
//            // Set plan name
//            itemView.textViewPlanName.text = plan.name
//
//            // Set target group info
//            val targetGroupText = "${plan.targetGender}, ${plan.minAge}-${plan.maxAge} years"
//            itemView.textViewTargetGroup.text = targetGroupText
//
//            // Set meal count
//            itemView.textViewMealCount.text = "$mealCount meals"
//
//            // Set background color based on gender
//            val backgroundColor = if (plan.targetGender == "Male") {
//                R.color.blue
//            } else {
//                R.color.accent
//            }
//            itemView.imageViewPlan.setBackgroundResource(backgroundColor)
//
//            // Set click listener
//            itemView.setOnClickListener {
//                listener.onPlanClick(plan)
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
import com.example.nutritionapp.database.entities.NutritionPlan
import com.example.nutritionapp.databinding.ItemNutritionPlanBinding

class NutritionPlanAdapter(
    private val context: Context,
    private val listener: OnPlanClickListener
) : RecyclerView.Adapter<NutritionPlanAdapter.PlanViewHolder>() {

    private val plans = mutableListOf<Pair<NutritionPlan, Int>>() // Plan and meal count

    interface OnPlanClickListener {
        fun onPlanClick(plan: NutritionPlan)
    }

    fun setPlans(newPlans: List<Pair<NutritionPlan, Int>>) {
        plans.clear()
        plans.addAll(newPlans)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val binding = ItemNutritionPlanBinding.inflate(LayoutInflater.from(context), parent, false)
        return PlanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        val (plan, mealCount) = plans[position]
        holder.bind(plan, mealCount)
    }

    override fun getItemCount(): Int = plans.size

    inner class PlanViewHolder(private val binding: ItemNutritionPlanBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(plan: NutritionPlan, mealCount: Int) {
            // Set plan name
            binding.textViewPlanName.text = plan.name

            // Set target group info
            val targetGroupText = "${plan.targetGender}, ${plan.minAge}-${plan.maxAge} years"
            binding.textViewTargetGroup.text = targetGroupText

            // Set meal count
            binding.textViewMealCount.text = "$mealCount meals"

            // Set background color based on gender
            val backgroundColor = if (plan.targetGender == "Male") {
                R.color.blue
            } else {
                R.color.accent
            }
            binding.imageViewPlan.setBackgroundResource(backgroundColor)

            // Set click listener
            binding.root.setOnClickListener {
                listener.onPlanClick(plan)
            }
        }
    }
}