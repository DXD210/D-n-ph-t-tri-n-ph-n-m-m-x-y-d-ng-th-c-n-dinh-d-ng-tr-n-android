package com.example.nutritionapp.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.nutritionapp.database.dao.*
import com.example.nutritionapp.database.entities.*
import com.example.nutritionapp.utils.PreloadedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

@Database(
    entities = [User::class, Meal::class, Favorite::class, Note::class, NutritionPlan::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun mealDao(): MealDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun noteDao(): NoteDao
    abstract fun nutritionPlanDao(): NutritionPlanDao

    companion object {
        private const val TAG = "AppDatabase"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DatabaseHelper.DATABASE_NAME
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    prepopulateDatabase(database)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private fun prepopulateDatabase(database: AppDatabase) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Kiểm tra xem đã có dữ liệu chưa
                    val mealCount = database.mealDao().getMealCount()
                    val planCount = database.nutritionPlanDao().getPlanCount()

                    Log.d(TAG, "Database initialization - Current meal count: $mealCount, plan count: $planCount")

                    if (mealCount == 0) {
                        // Thêm dữ liệu món ăn
                        val meals = PreloadedData.getAllMeals()
                        database.mealDao().insertAll(meals)
                        Log.d(TAG, "Added ${meals.size} meals to database")
                    }

                    if (planCount == 0) {
                        // Thêm dữ liệu kế hoạch dinh dưỡng
                        val plans = PreloadedData.getAllNutritionPlans()
                        database.nutritionPlanDao().insertAll(plans)
                        Log.d(TAG, "Added ${plans.size} nutrition plans to database")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error pre-populating database", e)
                }
            }
        }

        fun exportDatabase(context: Context, destinationFile: File): Boolean {
            return try {
                // Close the database to ensure all changes are flushed
                INSTANCE?.close()

                val databaseFile = context.getDatabasePath(DatabaseHelper.DATABASE_NAME)
                val src = FileInputStream(databaseFile).channel
                val dst = FileOutputStream(destinationFile).channel
                dst.transferFrom(src, 0, src.size())
                src.close()
                dst.close()

                // Reopen the database
                INSTANCE = null
                getDatabase(context)
                true
            } catch (e: IOException) {
                Log.e(TAG, "Error exporting database", e)
                false
            }
        }

        fun importDatabase(context: Context, sourceFile: File): Boolean {
            return try {
                // Close the database to ensure it's not in use
                INSTANCE?.close()

                val databaseFile = context.getDatabasePath(DatabaseHelper.DATABASE_NAME)

                // Ensure the database directory exists
                databaseFile.parentFile?.mkdirs()

                val src = FileInputStream(sourceFile).channel
                val dst = FileOutputStream(databaseFile).channel
                dst.transferFrom(src, 0, src.size())
                src.close()
                dst.close()

                // Reopen the database
                INSTANCE = null
                getDatabase(context)
                true
            } catch (e: IOException) {
                Log.e(TAG, "Error importing database", e)
                false
            }
        }
    }
}