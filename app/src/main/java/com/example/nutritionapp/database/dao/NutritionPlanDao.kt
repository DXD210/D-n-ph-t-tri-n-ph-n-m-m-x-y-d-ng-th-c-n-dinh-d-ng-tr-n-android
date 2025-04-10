package com.example.nutritionapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.nutritionapp.database.entities.NutritionPlan

@Dao
interface NutritionPlanDao {
    @Query("SELECT * FROM nutrition_plans")
    fun getAllNutritionPlans(): LiveData<List<NutritionPlan>>

    @Query("SELECT * FROM nutrition_plans WHERE id = :id")
    fun getNutritionPlanById(id: Long): LiveData<NutritionPlan>

    @Query("SELECT * FROM nutrition_plans WHERE target_gender = :gender AND :age BETWEEN min_age AND max_age")
    fun getNutritionPlansByGenderAndAge(gender: String, age: Int): LiveData<List<NutritionPlan>>

    @Query("SELECT * FROM nutrition_plans WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchNutritionPlans(query: String): LiveData<List<NutritionPlan>>

    @Query("SELECT COUNT(*) FROM nutrition_plans")
    fun getPlanCount(): Int

    @Insert
    fun insert(nutritionPlan: NutritionPlan): Long

    @Insert
    fun insertAll(plans: List<NutritionPlan>)

    @Update
    fun update(nutritionPlan: NutritionPlan)

    @Delete
    fun delete(nutritionPlan: NutritionPlan)
}