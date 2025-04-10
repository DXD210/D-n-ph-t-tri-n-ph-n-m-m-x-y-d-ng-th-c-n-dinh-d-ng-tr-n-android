package com.example.nutritionapp.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nutritionapp.database.DatabaseHelper.Companion.TABLE_USERS
import java.util.*

@Entity(tableName = TABLE_USERS)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val email: String,
    val password: String,
    val gender: String? = null,
    val age: Int? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)