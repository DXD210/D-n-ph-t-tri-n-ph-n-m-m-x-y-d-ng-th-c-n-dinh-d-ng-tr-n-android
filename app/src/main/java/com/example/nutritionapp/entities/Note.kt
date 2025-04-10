package com.example.nutritionapp.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.nutritionapp.database.DatabaseHelper.Companion.TABLE_NOTES
import java.util.*

@Entity(
    tableName = TABLE_NOTES,
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val title: String,
    val content: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)