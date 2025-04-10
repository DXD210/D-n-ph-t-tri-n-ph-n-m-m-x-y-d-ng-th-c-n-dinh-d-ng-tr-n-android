//package com.example.nutritionapp.database.entities
//
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//import com.example.nutritionapp.database.DatabaseHelper.Companion.TABLE_MEALS
//import java.util.*
//
//@Entity(tableName = TABLE_MEALS)
//data class Meal(
//    @PrimaryKey(autoGenerate = true)
//    val id: Long = 0,
//    val name: String,
//    val image: String? = null,
//    val description: String? = null,
//    val ingredients: String,
//    val instructions: String,
//    val calories: Double,
//    val proteins: Double? = null,
//    val carbs: Double? = null,
//    val fats: Double? = null,
//    val category: String,
//    val mealTime: String,
//    val createdAt: Date = Date(),
//    val updatedAt: Date = Date()
//)

package com.example.nutritionapp.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nutritionapp.database.DatabaseHelper.Companion.TABLE_MEALS
import java.util.*

@Entity(tableName = TABLE_MEALS)
data class Meal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "image")
    val image: String? = null,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "ingredients")
    val ingredients: String,

    @ColumnInfo(name = "instructions")
    val instructions: String,

    @ColumnInfo(name = "calories")
    val calories: Double,

    @ColumnInfo(name = "proteins")
    val proteins: Double? = null,

    @ColumnInfo(name = "carbs")
    val carbs: Double? = null,

    @ColumnInfo(name = "fats")
    val fats: Double? = null,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "meal_time")  // Explicitly set column name to match query
    val mealTime: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Date = Date()
)