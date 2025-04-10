package com.example.nutritionapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.nutritionapp.database.entities.Favorite
import com.example.nutritionapp.database.entities.Meal

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites WHERE userId = :userId")
    fun getFavoritesByUserId(userId: Long): LiveData<List<Favorite>>

    @Query("""
        SELECT m.* FROM meals m
        INNER JOIN favorites f ON m.id = f.mealId
        WHERE f.userId = :userId
    """)
    fun getFavoriteMealsByUserId(userId: Long): LiveData<List<Meal>>

    @Query("""
        SELECT m.* FROM meals m
        INNER JOIN favorites f ON m.id = f.mealId
        WHERE f.userId = :userId AND (m.name LIKE '%' || :query || '%' OR m.description LIKE '%' || :query || '%')
    """)
    fun searchFavoriteMealsByUserId(userId: Long, query: String): LiveData<List<Meal>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND mealId = :mealId)")
    fun isMealFavorite(userId: Long, mealId: Long): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(favorite: Favorite): Long

    @Delete
    fun delete(favorite: Favorite)

    @Query("DELETE FROM favorites WHERE userId = :userId AND mealId = :mealId")
    fun deleteFavorite(userId: Long, mealId: Long)
}