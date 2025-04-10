package com.example.nutritionapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.nutritionapp.database.entities.Meal

@Dao
interface MealDao {
    @Query("SELECT * FROM meals")
    fun getAllMeals(): LiveData<List<Meal>>

    @Query("SELECT * FROM meals WHERE id = :id")
    fun getMealById(id: Long): LiveData<Meal>

    @Query("SELECT * FROM meals WHERE meal_time = :mealTime")
    fun getMealsByTime(mealTime: String): LiveData<List<Meal>>

    @Query("SELECT * FROM meals WHERE meal_time = :mealTime AND name LIKE '%' || :query || '%'")
    fun searchMealsByTime(mealTime: String, query: String): LiveData<List<Meal>>

    @Query("SELECT * FROM meals WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchMeals(query: String): LiveData<List<Meal>>

    @Query("SELECT COUNT(*) FROM meals")
    fun getMealCount(): Int

    @Insert
    fun insert(meal: Meal): Long

    @Update
    fun update(meal: Meal)

    @Delete
    fun delete(meal: Meal)

    @Insert
    fun insertAll(meals: List<Meal>)

    @Query("SELECT * FROM meals WHERE id IN (:mealIds)")
    fun getMealsByIds(mealIds: List<Long>): LiveData<List<Meal>>
}