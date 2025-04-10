//package com.example.nutritionapp.database.entities
//
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//import com.example.nutritionapp.database.DatabaseHelper.Companion.TABLE_NUTRITION_PLANS
//import java.util.*
//
//@Entity(tableName = TABLE_NUTRITION_PLANS)
//data class NutritionPlan(
//    @PrimaryKey(autoGenerate = true)
//    val id: Long = 0,
//    val name: String,
//    val description: String? = null,
//    val targetGender: String,
//    val minAge: Int,
//    val maxAge: Int,
//    val meals: String, // Comma-separated list of meal IDs
//    val createdAt: Date = Date(),
//    val updatedAt: Date = Date()
//)

package com.example.nutritionapp.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nutritionapp.database.DatabaseHelper.Companion.TABLE_NUTRITION_PLANS
import java.util.*

@Entity(tableName = TABLE_NUTRITION_PLANS)
data class NutritionPlan(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "target_gender")  // Explicitly set column name
    val targetGender: String,

    @ColumnInfo(name = "min_age")  // Explicitly set column name
    val minAge: Int,

    @ColumnInfo(name = "max_age")  // Explicitly set column name
    val maxAge: Int,

    @ColumnInfo(name = "meals")
    val meals: String,  // Comma-separated list of meal IDs

    @ColumnInfo(name = "calories_goal")
    val caloriesGoal: Double? = null,

    @ColumnInfo(name = "proteins_goal")
    val proteinsGoal: Double? = null,

    @ColumnInfo(name = "carbs_goal")
    val carbsGoal: Double? = null,

    @ColumnInfo(name = "fats_goal")
    val fatsGoal: Double? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Date = Date()
)