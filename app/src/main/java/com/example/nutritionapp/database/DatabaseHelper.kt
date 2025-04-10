package com.example.nutritionapp.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val databasePath: String = context.getDatabasePath(DATABASE_NAME).path
    private val databaseDir: File = context.getDatabasePath(DATABASE_NAME).parentFile!!

    companion object {
        private const val TAG = "DatabaseHelper"
        const val DATABASE_NAME = "nutrition_app.db"
        const val DATABASE_VERSION = 1

        // Tables
        const val TABLE_USERS = "users"
        const val TABLE_MEALS = "meals"
        const val TABLE_FAVORITES = "favorites"
        const val TABLE_NOTES = "notes"
        const val TABLE_NUTRITION_PLANS = "nutrition_plans"

        // Common columns
        const val COLUMN_ID = "id"
        const val COLUMN_CREATED_AT = "created_at"
        const val COLUMN_UPDATED_AT = "updated_at"

        // Users table columns
        const val COLUMN_USERNAME = "username"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_GENDER = "gender"
        const val COLUMN_AGE = "age"

        // Meals table columns
        const val COLUMN_MEAL_NAME = "name"
        const val COLUMN_MEAL_IMAGE = "image"
        const val COLUMN_MEAL_DESCRIPTION = "description"
        const val COLUMN_MEAL_INGREDIENTS = "ingredients"
        const val COLUMN_MEAL_INSTRUCTIONS = "instructions"
        const val COLUMN_MEAL_CALORIES = "calories"
        const val COLUMN_MEAL_PROTEINS = "proteins"
        const val COLUMN_MEAL_CARBS = "carbs"
        const val COLUMN_MEAL_FATS = "fats"
        const val COLUMN_MEAL_CATEGORY = "category"
        const val COLUMN_MEAL_TIME = "meal_time"

        // Favorites table columns
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_MEAL_ID = "meal_id"

        // Notes table columns
        const val COLUMN_NOTE_TITLE = "title"
        const val COLUMN_NOTE_CONTENT = "content"

        // Nutrition plans table columns
        const val COLUMN_PLAN_NAME = "name"
        const val COLUMN_PLAN_DESCRIPTION = "description"
        const val COLUMN_PLAN_TARGET_GENDER = "target_gender"
        const val COLUMN_PLAN_MIN_AGE = "min_age"
        const val COLUMN_PLAN_MAX_AGE = "max_age"
        const val COLUMN_PLAN_MEALS = "meals"
    }



    override fun onCreate(db: SQLiteDatabase) {
        // Users table
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT NOT NULL,
                $COLUMN_EMAIL TEXT NOT NULL UNIQUE,
                $COLUMN_PASSWORD TEXT NOT NULL,
                $COLUMN_GENDER TEXT,
                $COLUMN_AGE INTEGER,
                $COLUMN_CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                $COLUMN_UPDATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP
            );
        """.trimIndent()

        // Meals table
        val createMealsTable = """
            CREATE TABLE $TABLE_MEALS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_MEAL_NAME TEXT NOT NULL,
                $COLUMN_MEAL_IMAGE TEXT,
                $COLUMN_MEAL_DESCRIPTION TEXT,
                $COLUMN_MEAL_INGREDIENTS TEXT NOT NULL,
                $COLUMN_MEAL_INSTRUCTIONS TEXT NOT NULL,
                $COLUMN_MEAL_CALORIES REAL NOT NULL,
                $COLUMN_MEAL_PROTEINS REAL,
                $COLUMN_MEAL_CARBS REAL,
                $COLUMN_MEAL_FATS REAL,
                $COLUMN_MEAL_CATEGORY TEXT NOT NULL,
                $COLUMN_MEAL_TIME TEXT NOT NULL,
                $COLUMN_CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                $COLUMN_UPDATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP
            );
        """.trimIndent()

        // Favorites table
        val createFavoritesTable = """
            CREATE TABLE $TABLE_FAVORITES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_ID INTEGER NOT NULL,
                $COLUMN_MEAL_ID INTEGER NOT NULL,
                $COLUMN_CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY ($COLUMN_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID) ON DELETE CASCADE,
                FOREIGN KEY ($COLUMN_MEAL_ID) REFERENCES $TABLE_MEALS($COLUMN_ID) ON DELETE CASCADE,
                UNIQUE($COLUMN_USER_ID, $COLUMN_MEAL_ID)
            );
        """.trimIndent()

        // Notes table
        val createNotesTable = """
            CREATE TABLE $TABLE_NOTES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_ID INTEGER NOT NULL,
                $COLUMN_NOTE_TITLE TEXT NOT NULL,
                $COLUMN_NOTE_CONTENT TEXT,
                $COLUMN_CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                $COLUMN_UPDATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY ($COLUMN_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID) ON DELETE CASCADE
            );
        """.trimIndent()

        // Nutrition plans table
        val createNutritionPlansTable = """
            CREATE TABLE $TABLE_NUTRITION_PLANS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PLAN_NAME TEXT NOT NULL,
                $COLUMN_PLAN_DESCRIPTION TEXT,
                $COLUMN_PLAN_TARGET_GENDER TEXT NOT NULL,
                $COLUMN_PLAN_MIN_AGE INTEGER NOT NULL,
                $COLUMN_PLAN_MAX_AGE INTEGER NOT NULL,
                $COLUMN_PLAN_MEALS TEXT NOT NULL,
                $COLUMN_CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                $COLUMN_UPDATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP
            );
        """.trimIndent()

        db.execSQL(createUsersTable)
        db.execSQL(createMealsTable)
        db.execSQL(createFavoritesTable)
        db.execSQL(createNotesTable)
        db.execSQL(createNutritionPlansTable)

        // Insert initial data
        insertInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_FAVORITES")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NUTRITION_PLANS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_MEALS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
            onCreate(db)
        }
    }

    private fun prepopulateDatabase(database: AppDatabase, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Kiểm tra xem đã có dữ liệu chưa
                val mealCount = database.mealDao().getMealCount()
                val planCount = database.nutritionPlanDao().getPlanCount()

                Log.d(TAG, "Database initialization - Current meal count: $mealCount, plan count: $planCount")

                if (mealCount == 0) {
                    // Sử dụng SupportSQLiteDatabase để thực thi SQL trực tiếp
                    val db = database.openHelper.writableDatabase

                    // Lấy các câu lệnh SQL từ DatabaseHelper
                    val databaseHelper = DatabaseHelper(context)
                    val breakfastMeals = databaseHelper.getBreakfastMeals()
                    val lunchMeals = databaseHelper.getLunchMeals()
                    val dinnerMeals = databaseHelper.getDinnerMeals()
                    val snackMeals = databaseHelper.getSnackMeals()

                    // Thực thi từng câu lệnh SQL
                    for (sql in breakfastMeals + lunchMeals + dinnerMeals + snackMeals) {
                        db.execSQL(sql)
                    }

                    Log.d(TAG, "Added ${breakfastMeals.size + lunchMeals.size + dinnerMeals.size + snackMeals.size} meals to database")
                }

                // Tương tự cho nutrition plans

            } catch (e: Exception) {
                Log.e(TAG, "Error pre-populating database", e)
            }
        }
    }

    private fun insertInitialData(db: SQLiteDatabase) {
        // This function will insert all initial data
        // It will be implemented separately due to the large amount of data
        insertInitialMeals(db)
        insertInitialNutritionPlans(db)
    }

    private fun insertInitialMeals(db: SQLiteDatabase) {
        // Insert breakfast meals for a week
        val breakfastMeals = getBreakfastMeals()
        for (meal in breakfastMeals) {
            db.execSQL(meal)
        }

        // Insert lunch meals for a week
        val lunchMeals = getLunchMeals()
        for (meal in lunchMeals) {
            db.execSQL(meal)
        }

        // Insert dinner meals for a week
        val dinnerMeals = getDinnerMeals()
        for (meal in dinnerMeals) {
            db.execSQL(meal)
        }

        // Insert snack meals for a week
        val snackMeals = getSnackMeals()
        for (meal in snackMeals) {
            db.execSQL(meal)
        }
    }

    private fun getBreakfastMeals(): List<String> {
        val meals = mutableListOf<String>()

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Classic Oatmeal with Fruits',
            'breakfast_oatmeal',
            'A nutritious and filling breakfast bowl of oatmeal topped with fresh fruits and honey',
            'Rolled oats - 1/2 cup, Milk - 1 cup, Banana - 1, Berries - 1/2 cup, Honey - 1 tbsp, Cinnamon - 1/4 tsp',
            '1. Bring milk to a simmer in a small pot. 2. Add oats and cook for 5 minutes, stirring occasionally. 3. Transfer to a bowl and top with sliced banana, berries, honey, and cinnamon.',
            310,
            10,
            56,
            6,
            'Healthy',
            'Breakfast'
        );
    """.trimIndent())
        // Breakfast 2: Avocado Toast with Eggs
        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Avocado Toast with Eggs',
            'breakfast_avocado_toast',
            'Whole grain toast topped with mashed avocado and poached eggs',
            'Whole grain bread - 2 slices, Avocado - 1, Eggs - 2, Salt - to taste, Pepper - to taste, Red pepper flakes - a pinch',
            '1. Toast the bread slices. 2. Mash the avocado and spread on toast. 3. Poach or fry the eggs. 4. Place eggs on top of avocado toast. 5. Season with salt, pepper, and red pepper flakes.',
            420,
            18,
            30,
            26,
            'Healthy',
            'Breakfast'
        );
    """.trimIndent())

        // Add more breakfast meals (about 15 total for variety)
        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Greek Yogurt Parfait',
            'breakfast_yogurt_parfait',
            'Creamy Greek yogurt layered with granola and fresh berries',
            'Greek yogurt - 1 cup, Granola - 1/4 cup, Mixed berries - 1/2 cup, Honey - 1 tbsp',
            '1. In a glass or bowl, add a layer of Greek yogurt. 2. Add a layer of granola. 3. Add a layer of mixed berries. 4. Repeat the layers. 5. Drizzle with honey.',
            280,
            18,
            38,
            8,
            'Healthy',
            'Breakfast'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Vegetable Omelette',
            'breakfast_omelette',
            'Fluffy omelette filled with sautéed vegetables and cheese',
            'Eggs - 3, Bell pepper - 1/4, Onion - 1/4, Spinach - 1/2 cup, Cheddar cheese - 1/4 cup, Salt - to taste, Pepper - to taste, Olive oil - 1 tsp',
            '1. Beat eggs in a bowl with salt and pepper. 2. Heat oil in a pan and sauté vegetables until soft. 3. Pour beaten eggs over vegetables. 4. Once the bottom is set, sprinkle cheese on top. 5. Fold omelette in half and cook until eggs are fully set.',
            350,
            24,
            8,
            24,
            'Protein-rich',
            'Breakfast'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Protein Smoothie Bowl',
            'breakfast_smoothie_bowl',
            'Thick smoothie topped with nutritious toppings for a satisfying breakfast',
            'Banana - 1, Frozen berries - 1 cup, Protein powder - 1 scoop, Almond milk - 1/2 cup, Chia seeds - 1 tbsp, Sliced fruits and nuts for topping',
            '1. Blend banana, frozen berries, protein powder, and almond milk until smooth. 2. Pour into a bowl. 3. Top with chia seeds, sliced fruits, and nuts.',
            380,
            25,
            45,
            10,
            'Protein-rich',
            'Breakfast'
        );
    """.trimIndent())

        // Add more breakfast options to reach 15
        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Pancakes with Maple Syrup',
            'breakfast_pancakes',
            'Fluffy homemade pancakes served with pure maple syrup',
            'Flour - 1 cup, Baking powder - 2 tsp, Sugar - 2 tbsp, Salt - 1/4 tsp, Milk - 1 cup, Egg - 1, Butter - 2 tbsp (melted), Maple syrup - 2 tbsp',
            '1. Mix dry ingredients in a bowl. 2. In another bowl, whisk together milk, egg, and melted butter. 3. Combine wet and dry ingredients until just mixed. 4. Heat a pan and pour 1/4 cup batter for each pancake. 5. Cook until bubbles form, then flip. 6. Serve with maple syrup.',
            450,
            10,
            75,
            12,
            'Classic',
            'Breakfast'
        );
    """.trimIndent())

        // Thêm 13 món bữa sáng mới (cộng với 6 món đã có = 19 món bữa sáng)
        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Breakfast Burrito',
            'breakfast_burrito',
            'Hearty breakfast burrito filled with scrambled eggs, beans, and vegetables',
            'Flour tortilla - 1 large, Eggs - 2, Black beans - 1/4 cup, Bell pepper - 1/4, Onion - 1/4, Cheese - 1/4 cup shredded, Salsa - 2 tbsp, Avocado - 1/4, Salt - to taste, Pepper - to taste',
            '1. Scramble eggs in a pan. 2. Sauté diced bell pepper and onion. 3. Warm the tortilla. 4. Layer beans, scrambled eggs, vegetables, cheese, salsa, and avocado on the tortilla. 5. Fold and roll into a burrito.',
            450,
            20,
            42,
            22,
            'Protein-rich',
            'Breakfast'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Chia Seed Pudding',
            'breakfast_chia_pudding',
            'Overnight chia seed pudding with coconut milk and berries',
            'Chia seeds - 3 tbsp, Coconut milk - 1 cup, Honey - 1 tbsp, Vanilla extract - 1/4 tsp, Mixed berries - 1/2 cup, Sliced almonds - 1 tbsp',
            '1. In a jar, mix chia seeds, coconut milk, honey, and vanilla. 2. Stir well and refrigerate overnight. 3. Top with mixed berries and sliced almonds before serving.',
            320,
            8,
            30,
            20,
            'Vegetarian',
            'Breakfast'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Egg and Vegetable Muffins',
            'breakfast_egg_muffins',
            'Protein-packed egg muffins with vegetables, perfect for grab-and-go breakfasts',
            'Eggs - 6, Spinach - 1 cup chopped, Bell pepper - 1/2, Onion - 1/4, Mushrooms - 1/2 cup, Cheese - 1/4 cup shredded, Salt - 1/4 tsp, Pepper - 1/4 tsp',
            '1. Preheat oven to 350°F. 2. Whisk eggs in a bowl with salt and pepper. 3. Sauté vegetables until soft. 4. Fill muffin tin with vegetable mixture. 5. Pour egg mixture over vegetables. 6. Sprinkle cheese on top. 7. Bake for 20 minutes.',
            220,
            18,
            4,
            15,
            'Protein-rich',
            'Breakfast'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Whole Grain Toast with Nut Butter',
            'breakfast_toast_nut_butter',
            'Simple and nutritious breakfast of whole grain toast with natural nut butter and banana',
            'Whole grain bread - 2 slices, Almond butter - 2 tbsp, Banana - 1, Cinnamon - a pinch, Honey - 1 tsp (optional)',
            '1. Toast the bread slices. 2. Spread almond butter on each slice. 3. Top with sliced banana. 4. Sprinkle with cinnamon and drizzle with honey if desired.',
            350,
            12,
            42,
            16,
            'Vegetarian',
            'Breakfast'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Breakfast Quinoa Bowl',
            'breakfast_quinoa_bowl',
            'Warm quinoa breakfast bowl with milk, fruit, and nuts',
            'Quinoa - 1/2 cup (cooked), Milk - 1/2 cup, Apple - 1/2 (diced), Raisins - 2 tbsp, Walnuts - 1 tbsp (chopped), Cinnamon - 1/4 tsp, Maple syrup - 1 tsp',
            '1. Cook quinoa according to package instructions. 2. Mix with milk in a bowl. 3. Top with diced apple, raisins, and walnuts. 4. Sprinkle with cinnamon and drizzle with maple syrup.',
            320,
            10,
            52,
            8,
            'Healthy',
            'Breakfast'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Green Smoothie',
            'breakfast_green_smoothie',
            'Nutrient-dense green smoothie with spinach, banana, and protein',
            'Spinach - 2 cups, Banana - 1, Greek yogurt - 1/2 cup, Almond milk - 1 cup, Chia seeds - 1 tbsp, Honey - 1 tsp, Ice cubes - 4',
            '1. Add all ingredients to a blender. 2. Blend until smooth and creamy. 3. Add more almond milk if needed to reach desired consistency.',
            250,
            14,
            35,
            6,
            'Healthy',
            'Breakfast'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Cottage Cheese with Fruit',
            'breakfast_cottage_cheese',
            'Protein-rich cottage cheese topped with fresh fruits and nuts',
            'Cottage cheese - 1 cup, Mixed berries - 1/2 cup, Peach - 1 (sliced), Almonds - 1 tbsp (sliced), Honey - 1 tsp',
            '1. Place cottage cheese in a bowl. 2. Top with mixed berries and sliced peach. 3. Sprinkle with sliced almonds. 4. Drizzle with honey.',
            280,
            24,
            28,
            8,
            'Protein-rich',
            'Breakfast'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Overnight Oats',
            'breakfast_overnight_oats',
            'Easy overnight oats with yogurt and fruit, prepared the night before',
            'Rolled oats - 1/2 cup, Greek yogurt - 1/2 cup, Milk - 1/2 cup, Chia seeds - 1 tbsp, Honey - 1 tbsp, Banana - 1/2 (sliced), Blueberries - 1/4 cup',
            '1. In a jar, mix oats, yogurt, milk, chia seeds, and honey. 2. Stir well, cover, and refrigerate overnight. 3. In the morning, top with sliced banana and blueberries.',
            340,
            16,
            52,
            7,
            'Healthy',
            'Breakfast'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'English Breakfast',
            'breakfast_english',
            'Traditional English breakfast with eggs, bacon, beans, and toast',
            'Eggs - 2, Bacon strips - 2, Baked beans - 1/2 cup, Tomato - 1/2 (grilled), Mushrooms - 1/2 cup (sautéed), Whole grain toast - 1 slice, Butter - 1 tsp',
            '1. Cook bacon in a pan until crispy. 2. In the same pan, fry the eggs. 3. Grill the tomato halves. 4. Sauté mushrooms. 5. Heat the baked beans. 6. Toast the bread and spread with butter. 7. Arrange all components on a plate.',
            520,
            28,
            30,
            32,
            'Classic',
            'Breakfast'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Breakfast Sandwich',
            'breakfast_sandwich',
            'Hearty breakfast sandwich with egg, cheese, and avocado on an English muffin',
            'Whole grain English muffin - 1, Egg - 1, Cheese slice - 1, Avocado - 1/4, Spinach - handful, Tomato - 2 slices, Salt - to taste, Pepper - to taste',
            '1. Toast the English muffin. 2. Fry or poach the egg. 3. Layer spinach, tomato, egg, cheese, and avocado on the muffin. 4. Season with salt and pepper. 5. Close the sandwich with the top half of the muffin.',
            380,
            16,
            30,
            22,
            'Protein-rich',
            'Breakfast'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Banana Bread',
            'breakfast_banana_bread',
            'Homemade banana bread slice with a spread of butter',
            'Banana bread slice - 1 thick slice, Butter - 1 tsp, Honey - 1 tsp (optional)',
            '1. Toast the banana bread slice if desired. 2. Spread butter on top. 3. Drizzle with honey if desired.',
            280,
            4,
            42,
            10,
            'Comfort',
            'Breakfast'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Fruit and Granola Bowl',
            'breakfast_fruit_granola',
            'Fresh seasonal fruit bowl with granola and honey',
            'Mixed fruits (berries, banana, apple) - 2 cups, Granola - 1/4 cup, Honey - 1 tbsp, Mint leaves - for garnish',
            '1. Wash and chop the fruits. 2. Place them in a bowl. 3. Top with granola. 4. Drizzle with honey. 5. Garnish with mint leaves.',
            290,
            5,
            62,
            5,
            'Vegetarian',
            'Breakfast'
        );
    """.trimIndent())

        return meals
    }

    private fun getLunchMeals(): List<String> {
        val meals = mutableListOf<String>()

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Quinoa Salad with Grilled Chicken',
            'lunch_quinoa_chicken',
            'Protein-rich quinoa salad with grilled chicken breast and vegetables',
            'Quinoa - 1 cup (cooked), Chicken breast - 4 oz, Cherry tomatoes - 1/2 cup, Cucumber - 1/2, Red onion - 1/4, Feta cheese - 2 tbsp, Olive oil - 1 tbsp, Lemon juice - 1 tbsp, Salt - to taste, Pepper - to taste',
            '1. Season chicken with salt and pepper, then grill until cooked through. 2. In a bowl, combine cooked quinoa, diced tomatoes, cucumber, and red onion. 3. Add crumbled feta cheese. 4. Slice the grilled chicken and place on top. 5. Drizzle with olive oil and lemon juice, then toss to combine.',
            420,
            30,
            38,
            16,
            'Protein-rich',
            'Lunch'
        );
    """.trimIndent())

        // Add more lunch meals
        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Vegetable Stir-Fry with Tofu',
            'lunch_stirfry_tofu',
            'Colorful vegetable stir-fry with crispy tofu cubes served over brown rice',
            'Firm tofu - 8 oz, Bell peppers - 1, Broccoli - 1 cup, Carrots - 1, Snap peas - 1/2 cup, Garlic - 2 cloves, Ginger - 1 tbsp (minced), Soy sauce - 2 tbsp, Sesame oil - 1 tbsp, Brown rice - 1 cup (cooked)',
            '1. Press and drain tofu, then cut into cubes. 2. Heat oil in a wok or large pan and cook tofu until golden. 3. Remove tofu and add vegetables, garlic, and ginger. 4. Stir-fry until vegetables are tender-crisp. 5. Add tofu back to the pan with soy sauce. 6. Serve over brown rice.',
            380,
            18,
            45,
            14,
            'Vegetarian',
            'Lunch'
        );
    """.trimIndent())

        // Thêm 18 món bữa trưa mới (cộng với 2 món đã có = 20 món bữa trưa)
        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Mediterranean Chickpea Salad',
            'lunch_chickpea_salad',
            'Refreshing chickpea salad with Mediterranean flavors and olive oil dressing',
            'Chickpeas - 1 can (drained), Cucumber - 1, Cherry tomatoes - 1 cup, Red onion - 1/4, Feta cheese - 1/4 cup, Kalamata olives - 10, Parsley - 1/4 cup (chopped), Olive oil - 2 tbsp, Lemon juice - 1 tbsp, Salt - to taste, Pepper - to taste',
            '1. Combine chickpeas, diced cucumber, halved cherry tomatoes, diced red onion, crumbled feta, and olives in a bowl. 2. In a small bowl, whisk together olive oil, lemon juice, salt, and pepper. 3. Pour dressing over salad and toss. 4. Garnish with chopped parsley before serving.',
            380,
            15,
            42,
            18,
            'Vegetarian',
            'Lunch'
        );
    """.trimIndent())
        // Tiếp tục từ món Turkey and Avocado Wrap
        meals.add("""
    INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
    $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
    $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
    VALUES (
        'Turkey and Avocado Wrap',
        'lunch_turkey_wrap',
        'Whole grain wrap filled with lean turkey, avocado, and vegetables',
        'Whole grain tortilla - 1 large, Turkey breast slices - 4 oz, Avocado - 1/2, Lettuce - 1 cup, Tomato - 1/2, Cucumber - 1/4, Red onion - thin slices, Mustard - 1 tsp, Salt - to taste, Pepper - to taste',
        '1. Lay out the tortilla and spread mustard on it. 2. Layer turkey slices, lettuce, sliced tomato, cucumber, and red onion. 3. Add sliced avocado and season with salt and pepper. 4. Roll tightly and cut in half.',
        420,
        28,
        35,
        18,
        'Protein-rich',
        'Lunch'
    );
""".trimIndent())

        meals.add("""
    INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
    $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
    $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
    VALUES (
        'Tuna Salad Sandwich',
        'lunch_tuna_sandwich',
        'Classic tuna salad sandwich on whole grain bread with lettuce and tomato',
        'Whole grain bread - 2 slices, Canned tuna - 1 can (drained), Greek yogurt - 2 tbsp, Dijon mustard - 1 tsp, Celery - 1 stalk (diced), Red onion - 2 tbsp (diced), Lettuce - 2 leaves, Tomato - 2 slices, Salt - to taste, Pepper - to taste',
        '1. In a bowl, mix tuna, Greek yogurt, Dijon mustard, diced celery, and red onion. 2. Season with salt and pepper. 3. Toast bread if desired. 4. Place lettuce and tomato on one slice of bread. 5. Top with tuna salad and the other slice of bread.',
        350,
        30,
        30,
        10,
        'Protein-rich',
        'Lunch'
    );
""".trimIndent())

        meals.add("""
    INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
    $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
    $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
    VALUES (
        'Lentil Soup',
        'lunch_lentil_soup',
        'Hearty lentil soup with vegetables and herbs',
        'Lentils - 1 cup (dry), Onion - 1, Carrots - 2, Celery - 2 stalks, Garlic - 3 cloves, Vegetable broth - 6 cups, Tomato paste - 2 tbsp, Olive oil - 1 tbsp, Bay leaf - 1, Thyme - 1 tsp, Cumin - 1 tsp, Salt - to taste, Pepper - to taste',
        '1. Heat olive oil in a pot and sauté diced onion, carrots, and celery until soft. 2. Add minced garlic and cook for 30 seconds. 3. Add lentils, tomato paste, vegetable broth, bay leaf, thyme, and cumin. 4. Bring to a boil, then reduce heat and simmer for 25-30 minutes until lentils are tender. 5. Season with salt and pepper.',
        320,
        18,
        50,
        6,
        'Vegetarian',
        'Lunch'
    );
""".trimIndent())

        meals.add("""
    INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
    $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
    $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
    VALUES (
        'Caesar Salad with Grilled Chicken',
        'lunch_caesar_salad',
        'Classic Caesar salad with romaine lettuce, croutons, and grilled chicken',
        'Romaine lettuce - 1 head, Chicken breast - 4 oz, Parmesan cheese - 2 tbsp (grated), Croutons - 1/4 cup, Caesar dressing - 2 tbsp, Lemon juice - 1 tsp, Olive oil - 1 tsp, Garlic powder - 1/4 tsp, Salt - to taste, Pepper - to taste',
        '1. Season chicken with garlic powder, salt, and pepper. 2. Grill chicken until cooked through. 3. Chop romaine lettuce and place in a bowl. 4. Slice grilled chicken and place on top of lettuce. 5. Sprinkle with Parmesan cheese and croutons. 6. Drizzle with Caesar dressing and lemon juice.',
        380,
        32,
        15,
        22,
        'Protein-rich',
        'Lunch'
    );
""".trimIndent())

        meals.add("""
    INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
    $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
    $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
    VALUES (
        'Vegetable and Hummus Pita',
        'lunch_hummus_pita',
        'Whole wheat pita filled with fresh vegetables and hummus',
        'Whole wheat pita - 1, Hummus - 1/4 cup, Cucumber - 1/2, Bell pepper - 1/2, Carrots - 1/4 cup (grated), Spinach - 1 cup, Feta cheese - 2 tbsp, Olive oil - 1 tsp, Lemon juice - 1 tsp, Salt - to taste, Pepper - to taste',
        '1. Cut pita in half and open the pockets. 2. Spread hummus inside each pita half. 3. Fill with sliced cucumber, bell pepper, grated carrots, and spinach. 4. Sprinkle with feta cheese. 5. Drizzle with a mixture of olive oil, lemon juice, salt, and pepper.',
        320,
        12,
        45,
        12,
        'Vegetarian',
        'Lunch'
    );
""".trimIndent())

        meals.add("""
    INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
    $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
    $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
    VALUES (
        'Chicken and Brown Rice Bowl',
        'lunch_chicken_rice_bowl',
        'Balanced bowl with grilled chicken, brown rice, and steamed vegetables',
        'Chicken breast - 4 oz, Brown rice - 1/2 cup (cooked), Broccoli - 1 cup, Carrots - 1/2 cup, Soy sauce - 1 tbsp, Honey - 1 tsp, Garlic - 1 clove (minced), Ginger - 1/2 tsp (minced), Sesame seeds - 1 tsp, Green onions - for garnish',
        '1. Season and grill chicken until cooked through, then slice. 2. Steam broccoli and carrots until tender-crisp. 3. In a small bowl, mix soy sauce, honey, garlic, and ginger to make the sauce. 4. Arrange brown rice, vegetables, and chicken in a bowl. 5. Drizzle with sauce. 6. Sprinkle with sesame seeds and green onions.',
        420,
        35,
        40,
        10,
        'Protein-rich',
        'Lunch'
    );
""".trimIndent())

        // Add more lunch meals
        meals.add("""
    INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
    $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
    $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
    VALUES (
        'Black Bean and Sweet Potato Burrito',
        'lunch_bean_burrito',
        'Hearty vegetarian burrito with black beans and roasted sweet potatoes',
        'Whole grain tortilla - 1 large, Black beans - 1/2 cup, Sweet potato - 1 small (roasted), Avocado - 1/4, Salsa - 2 tbsp, Greek yogurt - 1 tbsp, Cilantro - 2 tbsp (chopped), Lime juice - 1 tsp, Cumin - 1/4 tsp, Chili powder - 1/4 tsp, Salt - to taste',
        '1. Preheat oven to 400°F. 2. Dice sweet potato, toss with olive oil, cumin, chili powder, salt, and roast for 20-25 minutes. 3. Warm black beans in a pan. 4. Warm tortilla. 5. Layer beans, roasted sweet potato, sliced avocado, salsa, and Greek yogurt on the tortilla. 6. Sprinkle with cilantro and lime juice. 7. Fold and roll into a burrito.',
        450,
        14,
        65,
        15,
        'Vegetarian',
        'Lunch'
    );
""".trimIndent())

        meals.add("""
    INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
    $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
    $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
    VALUES (
        'Spinach and Feta Stuffed Chicken',
        'lunch_stuffed_chicken',
        'Chicken breast stuffed with spinach and feta, served with a side salad',
        'Chicken breast - 6 oz, Spinach - 2 cups, Feta cheese - 2 tbsp, Garlic - 1 clove (minced), Olive oil - 1 tbsp, Lemon juice - 1 tbsp, Mixed greens - 2 cups, Cherry tomatoes - 1/2 cup, Cucumber - 1/2, Balsamic vinaigrette - 1 tbsp, Salt - to taste, Pepper - to taste',
        '1. Preheat oven to 375°F. 2. In a pan, sauté spinach and garlic until wilted. 3. Cut a pocket in the chicken breast. 4. Stuff with sautéed spinach and feta cheese. 5. Season with salt and pepper. 6. Cook in oven for 25-30 minutes. 7. Toss mixed greens, tomatoes, and cucumber with balsamic vinaigrette. 8. Serve chicken with the side salad.',
        420,
        40,
        10,
        25,
        'Protein-rich',
        'Lunch'
    );
""".trimIndent())

        meals.add("""
    INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
    $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
    $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
    VALUES (
        'Salmon Poke Bowl',
        'lunch_poke_bowl',
        'Fresh and colorful Hawaiian-inspired poke bowl with raw salmon',
        'Sushi-grade salmon - 4 oz (diced), Brown rice - 1/2 cup (cooked), Cucumber - 1/2 (diced), Avocado - 1/4 (sliced), Carrots - 1/4 cup (shredded), Edamame - 1/4 cup, Seaweed - 1 sheet (torn), Soy sauce - 1 tbsp, Sesame oil - 1 tsp, Rice vinegar - 1 tsp, Sesame seeds - 1 tsp',
        '1. Cook brown rice according to package instructions and cool. 2. In a small bowl, mix soy sauce, sesame oil, and rice vinegar. 3. Marinate diced salmon in the sauce for 5 minutes. 4. Arrange rice in a bowl. 5. Top with marinated salmon, cucumber, avocado, carrots, edamame, and seaweed. 6. Sprinkle with sesame seeds.',
        430,
        28,
        35,
        20,
        'Protein-rich',
        'Lunch'
    );
""".trimIndent())

        meals.add("""
    INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
    $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
    $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
    VALUES (
        'Italian Pasta Salad',
        'lunch_pasta_salad',
        'Colorful pasta salad with vegetables, cheese, and Italian dressing',
        'Whole wheat pasta - 1 cup (cooked), Cherry tomatoes - 1/2 cup (halved), Cucumber - 1/2 (diced), Bell pepper - 1/2 (diced), Red onion - 1/4 (thinly sliced), Black olives - 1/4 cup (sliced), Mozzarella cheese - 1/4 cup (diced), Italian dressing - 2 tbsp, Fresh basil - 2 tbsp (chopped), Salt - to taste, Pepper - to taste',
        '1. Cook pasta according to package instructions, drain, and cool. 2. In a large bowl, combine pasta, tomatoes, cucumber, bell pepper, red onion, olives, and mozzarella. 3. Drizzle with Italian dressing. 4. Add chopped basil, salt, and pepper. 5. Toss to combine and refrigerate for at least 30 minutes before serving.',
        380,
        12,
        55,
        14,
        'Vegetarian',
        'Lunch'
    );
""".trimIndent())

        meals.add("""
    INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
    $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
    $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
    VALUES (
        'Turkey Club Sandwich',
        'lunch_turkey_club',
        'Classic triple-decker club sandwich with turkey, bacon, lettuce, and tomato',
        'Whole grain bread - 3 slices, Turkey breast - 4 oz, Bacon - 2 slices (cooked), Lettuce - 2 leaves, Tomato - 2 slices, Avocado - 1/4, Mayonnaise - 1 tbsp, Mustard - 1 tsp, Salt - to taste, Pepper - to taste',
        '1. Toast bread slices. 2. Spread mayonnaise and mustard on one side of each bread slice. 3. On the first slice, layer turkey, lettuce, and tomato. 4. Place the second slice of bread on top. 5. Layer more turkey, bacon, avocado, and remaining lettuce. 6. Top with the third slice of bread. 7. Cut into quarters and secure with toothpicks.',
        490,
        30,
        35,
        25,
        'Classic',
        'Lunch'
    );
""".trimIndent())

        meals.add("""
    INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
    $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
    $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
    VALUES (
        'Butternut Squash Soup',
        'lunch_squash_soup',
        'Creamy butternut squash soup with a hint of spice',
        'Butternut squash - 1 medium (peeled and diced), Onion - 1 (diced), Garlic - 2 cloves (minced), Vegetable broth - 4 cups, Coconut milk - 1/2 cup, Olive oil - 1 tbsp, Nutmeg - 1/4 tsp, Cinnamon - 1/4 tsp, Ginger - 1/2 tsp (ground), Salt - to taste, Pepper - to taste, Pumpkin seeds - 1 tbsp (for garnish)',
        '1. Heat olive oil in a pot and sauté onion until translucent. 2. Add garlic and cook for 30 seconds. 3. Add diced butternut squash, vegetable broth, nutmeg, cinnamon, and ginger. 4. Bring to a boil, then reduce heat and simmer for 20 minutes until squash is tender. 5. Blend the soup until smooth. 6. Stir in coconut milk and season with salt and pepper. 7. Garnish with pumpkin seeds before serving.',
        280,
        6,
        40,
        12,
        'Vegetarian',
        'Lunch'
    );
""".trimIndent())

        meals.add("""
    INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
    $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
    $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
    VALUES (
        'Mediterranean Chicken Pita',
        'lunch_med_chicken_pita',
        'Grilled chicken pita with tzatziki sauce and fresh vegetables',
        'Whole wheat pita - 1, Chicken breast - 4 oz, Tzatziki sauce - 2 tbsp, Lettuce - 1/2 cup, Tomato - 2 slices, Cucumber - 1/4 (sliced), Red onion - 2 tbsp (sliced), Feta cheese - 1 tbsp, Olive oil - 1 tsp, Lemon juice - 1 tsp, Oregano - 1/2 tsp, Salt - to taste, Pepper - to taste',
        '1. Season chicken with oregano, salt, and pepper, then grill until cooked through. 2. Slice the cooked chicken. 3. Cut pita in half and open the pockets. 4. Spread tzatziki sauce inside each pita half. 5. Fill with sliced chicken, lettuce, tomato, cucumber, and red onion. 6. Sprinkle with feta cheese. 7. Drizzle with olive oil and lemon juice.',
        420,
        35,
        30,
        18,
        'Mediterranean',
        'Lunch'
    );
""".trimIndent())

        meals.add("""
    INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
    $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
    $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
    VALUES (
        'Beef and Vegetable Stir-Fry',
        'lunch_beef_stirfry',
        'Lean beef strips stir-fried with colorful vegetables',
        'Lean beef - 4 oz (thinly sliced), Broccoli - 1 cup, Bell pepper - 1, Carrots - 1/2 cup (sliced), Snow peas - 1/2 cup, Garlic - 2 cloves (minced), Ginger - 1 tbsp (minced), Soy sauce - 2 tbsp, Honey - 1 tsp, Sesame oil - 1 tsp, Cornstarch - 1 tsp, Brown rice - 1/2 cup (cooked)',
        '1. In a small bowl, mix soy sauce, honey, sesame oil, and cornstarch to make the sauce. 2. Heat a wok or large pan over high heat. 3. Stir-fry beef until browned, then remove from pan. 4. In the same pan, stir-fry garlic and ginger for 30 seconds. 5. Add vegetables and stir-fry until tender-crisp. 6. Return beef to the pan and add sauce. 7. Cook until sauce thickens. 8. Serve over brown rice.',
        450,
        30,
        40,
        15,
        'Protein-rich',
        'Lunch'
    );
""".trimIndent())

        meals.add("""
    INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
    $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
    $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
    VALUES (
        'Caprese Panini',
        'lunch_caprese_panini',
        'Italian-inspired grilled sandwich with mozzarella, tomato, and basil',
        'Whole grain bread - 2 slices, Fresh mozzarella - 2 oz (sliced), Tomato - 1 medium (sliced), Fresh basil leaves - 8, Balsamic glaze - 1 tbsp, Olive oil - 1 tsp, Salt - to taste, Pepper - to taste',
        '1. Brush the outside of bread slices with olive oil. 2. Layer mozzarella, tomato, and basil leaves on one slice of bread. 3. Drizzle with balsamic glaze and season with salt and pepper. 4. Top with the other slice of bread. 5. Grill in a panini press or pan until bread is golden and cheese is melted.',
        380,
        18,
        35,
        20,
        'Vegetarian',
        'Lunch'
    );
""".trimIndent())

        meals.add("""
    INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
    $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
    $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
    VALUES (
        'Shrimp and Avocado Salad',
        'lunch_shrimp_salad',
        'Light and refreshing salad with grilled shrimp and creamy avocado',
        'Shrimp - 4 oz (peeled and deveined), Mixed greens - 3 cups, Avocado - 1/2, Cherry tomatoes - 1/2 cup, Cucumber - 1/2, Red onion - 2 tbsp (sliced), Olive oil - 1 tbsp, Lemon juice - 1 tbsp, Garlic - 1 clove (minced), Dijon mustard - 1 tsp, Salt - to taste, Pepper - to taste',
        '1. Season shrimp with salt and pepper, then grill until pink. 2. In a bowl, whisk together olive oil, lemon juice, garlic, and Dijon mustard to make the dressing. 3. In a large bowl, combine mixed greens, halved cherry tomatoes, diced cucumber, and sliced red onion. 4. Add sliced avocado and grilled shrimp. 5. Drizzle with dressing and toss gently.',
        350,
        25,
        15,
        22,
        'Protein-rich',
        'Lunch'
    );
""".trimIndent())

        meals.add("""
    INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
    $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
    $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
    VALUES (
        'Egg Salad Sandwich',
        'lunch_egg_salad',
        'Classic egg salad sandwich on whole grain bread',
        'Whole grain bread - 2 slices, Eggs - 2 (hard-boiled), Greek yogurt - 1 tbsp, Dijon mustard - 1 tsp, Celery - 1 stalk (diced), Red onion - 1 tbsp (diced), Dill - 1 tsp (chopped), Lettuce - 1 leaf, Salt - to taste, Pepper - to taste',
        '1. Chop the hard-boiled eggs. 2. In a bowl, mix eggs, Greek yogurt, Dijon mustard, diced celery, red onion, and dill. 3. Season with salt and pepper. 4. Toast bread if desired. 5. Place lettuce on one slice of bread. 6. Top with egg salad and the other slice of bread.',
        320,
        16,
        30,
        15,
        'Classic',
        'Lunch'
    );
""".trimIndent())

        meals.add("""
    INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
    $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
    $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
    VALUES (
        'Vegetable Frittata',
        'lunch_frittata',
        'Open-faced omelette packed with vegetables and cheese',
        'Eggs - 4, Bell pepper - 1/2 (diced), Spinach - 1 cup, Onion - 1/4 (diced), Cherry tomatoes - 1/2 cup (halved), Feta cheese - 1/4 cup, Olive oil - 1 tbsp, Fresh herbs - 1 tbsp (chopped), Salt - to taste, Pepper - to taste',
        '1. Preheat oven to 375°F. 2. In an oven-safe skillet, heat olive oil and sauté onion until translucent. 3. Add bell pepper and cook for 2 minutes. 4. Add spinach and cook until wilted. 5. Whisk eggs with salt and pepper, then pour over vegetables. 6. Top with cherry tomatoes and feta cheese. 7. Cook on stovetop for 2 minutes, then transfer to oven and bake for 10-12 minutes until set.',
        380,
        26,
        10,
        25,
        'Protein-rich',
        'Lunch'
    );
""".trimIndent())


        return meals
    }

    private fun getDinnerMeals(): List<String> {
        val meals = mutableListOf<String>()

        // Dinner 1: Grilled Salmon with Roasted Vegetables
        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Grilled Salmon with Roasted Vegetables',
            'dinner_salmon_vegetables',
            'Omega-3 rich salmon fillet with a side of roasted seasonal vegetables',
            'Salmon fillet - 6 oz, Asparagus - 8 spears, Cherry tomatoes - 1/2 cup, Zucchini - 1, Olive oil - 1 tbsp, Lemon - 1, Garlic - 2 cloves, Fresh herbs - 2 tbsp, Salt - to taste, Pepper - to taste',
            '1. Preheat oven to 400°F. 2. Place vegetables on a baking sheet, drizzle with olive oil, salt, and pepper. 3. Roast for 15-20 minutes. 4. Season salmon with salt, pepper, and herbs. 5. Grill or pan-sear salmon for 3-4 minutes per side. 6. Serve with roasted vegetables and lemon wedges.',
            380,
            34,
            12,
            22,
            'Protein-rich',
            'Dinner'
        );
    """.trimIndent())

        // Thêm 19 món bữa tối mới (cộng với 1 món đã có = 20 món bữa tối)
        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Baked Chicken with Sweet Potato',
            'dinner_chicken_sweet_potato',
            'Herb-roasted chicken breast with baked sweet potato and steamed broccoli',
            'Chicken breast - 6 oz, Sweet potato - 1 medium, Broccoli - 1 cup, Olive oil - 1 tbsp, Garlic powder - 1/2 tsp, Rosemary - 1 tsp (dried), Thyme - 1/2 tsp (dried), Salt - to taste, Pepper - to taste',
            '1. Preheat oven to 375°F. 2. Wash sweet potato and pierce several times with a fork. 3. Bake sweet potato for 45-60 minutes until soft. 4. Season chicken with olive oil, garlic powder, rosemary, thyme, salt, and pepper. 5. Bake chicken for 25-30 minutes. 6. Steam broccoli until tender. 7. Serve chicken with sweet potato and broccoli.',
            450,
            38,
            45,
            10,
            'Protein-rich',
            'Dinner'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Vegetarian Chili',
            'dinner_veg_chili',
            'Hearty vegetarian chili with beans, vegetables, and warm spices',
            'Black beans - 1 can (drained), Kidney beans - 1 can (drained), Diced tomatoes - 1 can, Onion - 1, Bell pepper - 1, Carrots - 2, Garlic - 3 cloves, Olive oil - 1 tbsp, Chili powder - 2 tsp, Cumin - 1 tsp, Paprika - 1 tsp, Vegetable broth - 1 cup, Salt - to taste, Pepper - to taste, Avocado - 1/2 (for garnish), Greek yogurt - 2 tbsp (for garnish)',
            '1. Heat olive oil in a large pot and sauté diced onion, bell pepper, and carrots until soft. 2. Add minced garlic and cook for 30 seconds. 3. Add chili powder, cumin, and paprika, and stir for 1 minute. 4. Add beans, diced tomatoes, and vegetable broth. 5. Bring to a boil, then reduce heat and simmer for 30 minutes. 6. Season with salt and pepper. 7. Serve topped with diced avocado and a dollop of Greek yogurt.',
            350,
            15,
            55,
            8,
            'Vegetarian',
            'Dinner'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Shrimp Stir-Fry with Brown Rice',
            'dinner_shrimp_stirfry',
            'Quick and healthy shrimp stir-fry with vegetables and brown rice',
            'Shrimp - 6 oz (peeled and deveined), Broccoli - 1 cup, Bell pepper - 1, Snap peas - 1 cup, Carrots - 1/2 cup (sliced), Garlic - 2 cloves (minced), Ginger - 1 tbsp (minced), Soy sauce - 2 tbsp, Sesame oil - 1 tsp, Brown rice - 1 cup (cooked), Green onions - for garnish, Sesame seeds - for garnish',
            '1. Heat a wok or large pan over high heat. 2. Add shrimp and cook until pink, then remove from pan. 3. In the same pan, stir-fry garlic and ginger for 30 seconds. 4. Add vegetables and stir-fry until tender-crisp. 5. Return shrimp to the pan and add soy sauce and sesame oil. 6. Stir to combine and heat through. 7. Serve over brown rice, garnished with green onions and sesame seeds.',
            380,
            30,
            40,
            8,
            'Protein-rich',
            'Dinner'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Turkey Meatballs with Zucchini Noodles',
            'dinner_turkey_meatballs',
            'Lean turkey meatballs with zucchini noodles in marinara sauce',
            'Ground turkey - 1 lb, Breadcrumbs - 1/4 cup, Egg - 1, Garlic - 2 cloves (minced), Onion - 1/4 (finely diced), Italian seasoning - 1 tsp, Marinara sauce - 2 cups, Zucchini - 3 (spiralized), Parmesan cheese - 2 tbsp (grated), Fresh basil - for garnish, Salt - to taste, Pepper - to taste',
            '1. Preheat oven to 375°F. 2. In a bowl, combine ground turkey, breadcrumbs, egg, garlic, onion, Italian seasoning, salt, and pepper. 3. Form into meatballs and place on a baking sheet. 4. Bake for 20-25 minutes until cooked through. 5. Heat marinara sauce in a pan. 6. Add cooked meatballs to the sauce. 7. In another pan, sauté spiralized zucchini for 2-3 minutes until tender. 8. Serve meatballs and sauce over zucchini noodles. 9. Top with grated Parmesan cheese and fresh basil.',
            420,
            35,
            20,
            22,
            'Protein-rich',
            'Dinner'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Beef and Broccoli',
            'dinner_beef_broccoli',
            'Classic beef and broccoli stir-fry with a savory sauce',
            'Lean beef strips - 6 oz, Broccoli - 3 cups, Garlic - 2 cloves (minced), Ginger - 1 tbsp (minced), Soy sauce - 3 tbsp, Beef broth - 1/4 cup, Brown sugar - 1 tsp, Sesame oil - 1 tsp, Cornstarch - 1 tbsp, Brown rice - 1 cup (cooked), Green onions - for garnish',
            '1. Mix soy sauce, beef broth, brown sugar, and cornstarch in a small bowl. 2. Heat oil in a wok or large pan over high heat. 3. Stir-fry beef until browned, then remove from pan. 4. In the same pan, stir-fry garlic and ginger for 30 seconds. 5. Add broccoli and stir-fry until tender-crisp. 6. Return beef to the pan and add sauce. 7. Cook until sauce thickens. 8. Drizzle with sesame oil. 9. Serve over brown rice, garnished with green onions.',
            450,
            35,
            40,
            15,
            'Protein-rich',
            'Dinner'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Eggplant Parmesan',
            'dinner_eggplant_parm',
            'Baked eggplant slices with marinara sauce and melted cheese',
            'Eggplant - 1 large, Eggs - 2, Breadcrumbs - 1 cup, Parmesan cheese - 1/4 cup (grated), Marinara sauce - 2 cups, Mozzarella cheese - 1 cup (shredded), Fresh basil - 1/4 cup (chopped), Olive oil - 2 tbsp, Salt - to taste, Pepper - to taste',
            '1. Preheat oven to 375°F. 2. Slice eggplant into 1/2-inch rounds. 3. Beat eggs in a bowl. 4. Mix breadcrumbs and Parmesan cheese in another bowl. 5. Dip eggplant slices in egg, then coat with breadcrumb mixture. 6. Arrange on a baking sheet and drizzle with olive oil. 7. Bake for 20 minutes, flipping halfway through. 8. In a baking dish, layer marinara sauce, eggplant slices, mozzarella cheese, and basil. 9. Bake for 15-20 minutes until cheese is melted and bubbly.',
            400,
            20,
            40,
            18,
            'Vegetarian',
            'Dinner'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Grilled Fish Tacos',
            'dinner_fish_tacos',
            'Grilled white fish tacos with cabbage slaw and avocado',
            'White fish (tilapia, cod) - 6 oz, Corn tortillas - 3, Cabbage - 1 cup (shredded), Carrot - 1/2 (grated), Lime - 1, Avocado - 1/2, Cilantro - 1/4 cup (chopped), Greek yogurt - 1/4 cup, Garlic powder - 1/2 tsp, Cumin - 1/2 tsp, Paprika - 1/2 tsp, Salt - to taste, Pepper - to taste, Olive oil - 1 tbsp',
            '1. Season fish with garlic powder, cumin, paprika, salt, and pepper. 2. Grill fish until cooked through and flaky. 3. In a bowl, mix shredded cabbage and grated carrot with lime juice, salt, and pepper to make slaw. 4. Warm tortillas. 5. Flake fish and divide among tortillas. 6. Top with cabbage slaw, sliced avocado, cilantro, and a dollop of Greek yogurt.',
            380,
            30,
            30,
            16,
            'Protein-rich',
            'Dinner'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Stuffed Bell Peppers',
            'dinner_stuffed_peppers',
            'Bell peppers stuffed with ground turkey, rice, and vegetables',
            'Bell peppers - 3, Ground turkey - 1/2 lb, Brown rice - 1 cup (cooked), Onion - 1/2 (diced), Garlic - 2 cloves (minced), Diced tomatoes - 1 can, Spinach - 2 cups, Mozzarella cheese - 1/2 cup (shredded), Italian seasoning - 1 tsp, Olive oil - 1 tbsp, Salt - to taste, Pepper - to taste',
            '1. Preheat oven to 375°F. 2. Cut bell peppers in half lengthwise and remove seeds. 3. Place pepper halves in a baking dish. 4. Heat olive oil in a pan and sauté onion until translucent. 5. Add garlic and cook for 30 seconds. 6. Add ground turkey and cook until browned. 7. Add diced tomatoes, spinach, and Italian seasoning. 8. Add cooked rice and mix well. 9. Season with salt and pepper. 10. Fill pepper halves with mixture and top with mozzarella cheese. 11. Cover with foil and bake for 25 minutes. 12. Remove foil and bake for another 10 minutes until cheese is golden.',
            420,
            30,
            40,
            15,
            'Protein-rich',
            'Dinner'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Chicken Fajitas',
            'dinner_chicken_fajitas',
            'Sizzling chicken fajitas with bell peppers and onions',
            'Chicken breast - 6 oz, Bell peppers - 2 (sliced), Onion - 1 (sliced), Garlic - 2 cloves (minced), Lime - 1, Cumin - 1 tsp, Chili powder - 1 tsp, Paprika - 1/2 tsp, Olive oil - 1 tbsp, Whole wheat tortillas - 3, Avocado - 1/2, Greek yogurt - 2 tbsp, Cilantro - for garnish, Salt - to taste, Pepper - to taste',
            '1. Slice chicken into strips and season with cumin, chili powder, paprika, salt, and pepper. 2. Heat olive oil in a large pan over high heat. 3. Cook chicken until browned and cooked through, then remove from pan. 4. In the same pan, cook bell peppers and onions until tender. 5. Add garlic and cook for 30 seconds. 6. Return chicken to the pan and squeeze lime juice over everything. 7. Warm tortillas. 8. Serve chicken and vegetables with tortillas, sliced avocado, Greek yogurt, and cilantro.',
            450,
            35,
            40,
            15,
            'Protein-rich',
            'Dinner'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Cauliflower Curry',
            'dinner_cauliflower_curry',
            'Flavorful vegetarian curry with cauliflower, chickpeas, and spices',
            'Cauliflower - 1 head (cut into florets), Chickpeas - 1 can (drained), Onion - 1 (diced), Garlic - 3 cloves (minced), Ginger - 1 tbsp (minced), Diced tomatoes - 1 can, Coconut milk - 1 cup, Curry powder - 2 tbsp, Turmeric - 1 tsp, Cumin - 1 tsp, Brown rice - 1 cup (cooked), Cilantro - for garnish, Olive oil - 1 tbsp, Salt - to taste, Pepper - to taste',
            '1. Heat olive oil in a large pot and sauté onion until translucent. 2. Add garlic and ginger and cook for 30 seconds. 3. Add curry powder, turmeric, and cumin, and stir for 1 minute. 4. Add cauliflower florets and stir to coat with spices. 5. Add diced tomatoes, chickpeas, and coconut milk. 6. Bring to a simmer and cook for 15-20 minutes until cauliflower is tender. 7. Season with salt and pepper. 8. Serve over brown rice, garnished with cilantro.',
            380,
            15,
            60,
            12,
            'Vegetarian',
            'Dinner'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Pesto Chicken with Roasted Vegetables',
            'dinner_pesto_chicken',
            'Pesto-marinated chicken with a medley of roasted vegetables',
            'Chicken breast - 6 oz, Pesto - 2 tbsp, Zucchini - 1, Yellow squash - 1, Bell pepper - 1, Red onion - 1/2, Cherry tomatoes - 1 cup, Olive oil - 1 tbsp, Balsamic vinegar - 1 tbsp, Garlic - 2 cloves (minced), Parmesan cheese - 2 tbsp (grated), Fresh basil - for garnish, Salt - to taste, Pepper - to taste',
            '1. Preheat oven to 400°F. 2. Marinate chicken in pesto for at least 15 minutes. 3. Cut vegetables into similar-sized pieces and place on a baking sheet. 4. Drizzle vegetables with olive oil, balsamic vinegar, salt, and pepper. 5. Roast vegetables for 20-25 minutes until tender. 6. Grill or pan-sear pesto-marinated chicken until cooked through. 7. Serve chicken with roasted vegetables, sprinkled with Parmesan cheese and fresh basil.',
            420,
            35,
            20,
            22,
            'Mediterranean',
            'Dinner'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Mushroom Risotto',
            'dinner_mushroom_risotto',
            'Creamy mushroom risotto with Parmesan cheese and fresh herbs',
            'Arborio rice - 1 cup, Mushrooms - 8 oz (sliced), Onion - 1/2 (diced), Garlic - 2 cloves (minced), Vegetable broth - 4 cups (warm), White wine - 1/4 cup, Parmesan cheese - 1/4 cup (grated), Butter - 1 tbsp, Olive oil - 1 tbsp, Fresh thyme - 1 tbsp (chopped), Fresh parsley - 2 tbsp (chopped), Salt - to taste, Pepper - to taste',
            '1. Heat olive oil and butter in a large pan over medium heat. 2. Sauté onion until translucent. 3. Add garlic and mushrooms, and cook until mushrooms are soft. 4. Add Arborio rice and stir for 1-2 minutes until translucent around the edges. 5. Add white wine and cook until absorbed. 6. Gradually add warm vegetable broth, 1/2 cup at a time, stirring frequently and adding more broth as it's absorbed. 7. Cook until rice is creamy and al dente, about 20-25 minutes. 8. Stir in Parmesan cheese, thyme, salt, and pepper. 9. Garnish with chopped parsley before serving.',
            420,
            10,
            60,
            15,
            'Vegetarian',
            'Dinner'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Baked Cod with Lemon-Herb Crust',
            'dinner_baked_cod',
            'Tender baked cod with a crispy lemon-herb crust',
            'Cod fillets - 6 oz, Breadcrumbs - 1/4 cup, Parmesan cheese - 2 tbsp (grated), Lemon zest - 1 tsp, Fresh parsley - 2 tbsp (chopped), Fresh dill - 1 tbsp (chopped), Olive oil - 1 tbsp, Lemon juice - 1 tbsp, Garlic - 1 clove (minced), Dijon mustard - 1 tsp, Asparagus - 1 bunch, Salt - to taste, Pepper - to taste',
            '1. Preheat oven to 375°F. 2. In a bowl, mix breadcrumbs, Parmesan cheese, lemon zest, parsley, and dill. 3. In another bowl, mix olive oil, lemon juice, garlic, and Dijon mustard. 4. Brush cod fillets with the oil mixture, then coat with the breadcrumb mixture. 5. Place on a baking sheet and bake for 12-15 minutes until fish flakes easily. 6. Toss asparagus with olive oil, salt, and pepper. 7. Roast asparagus in the oven for 10-12 minutes. 8. Serve cod with roasted asparagus and lemon wedges.',
            350,
            30,
            20,
            15,
            'Protein-rich',
            'Dinner'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Spaghetti Squash with Turkey Meatballs',
            'dinner_squash_meatballs',
            'Healthy alternative to pasta using spaghetti squash with turkey meatballs',
            'Spaghetti squash - 1 medium, Ground turkey - 1/2 lb, Breadcrumbs - 1/4 cup, Egg - 1, Garlic - 2 cloves (minced), Onion - 1/4 (finely diced), Italian seasoning - 1 tsp, Marinara sauce - 2 cups, Parmesan cheese - 2 tbsp (grated), Fresh basil - for garnish, Olive oil - 1 tbsp, Salt - to taste, Pepper - to taste',
            '1. Preheat oven to 375°F. 2. Cut spaghetti squash in half lengthwise and scoop out seeds. 3. Brush with olive oil and season with salt and pepper. 4. Place cut-side down on a baking sheet and bake for 45 minutes until tender. 5. In a bowl, combine ground turkey, breadcrumbs, egg, garlic, onion, Italian seasoning, salt, and pepper. 6. Form into meatballs and place on a baking sheet. 7. Bake meatballs for 20-25 minutes until cooked through. 8. Heat marinara sauce in a pan. 9. Add cooked meatballs to the sauce. 10. Use a fork to scrape out spaghetti squash strands. 11. Serve meatballs and sauce over spaghetti squash. 12. Top with grated Parmesan cheese and fresh basil.',
            400,
            32,
            30,
            18,
            'Protein-rich',
            'Dinner'
        );
    """.trimIndent())

        // Tiếp tục món Quinoa-Stuffed Acorn Squash
        meals.add("""
    INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
    $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
    $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
    VALUES (
        'Quinoa-Stuffed Acorn Squash',
        'dinner_stuffed_squash',
        'Roasted acorn squash halves filled with quinoa, cranberries, and pecans',
        'Acorn squash - 1, Quinoa - 1/2 cup (dry), Vegetable broth - 1 cup, Cranberries (dried) - 1/4 cup, Pecans - 1/4 cup (chopped), Onion - 1/4 (diced), Garlic - 1 clove (minced), Spinach - 2 cups, Olive oil - 1 tbsp, Cinnamon - 1/4 tsp, Maple syrup - 1 tbsp, Salt - to taste, Pepper - to taste',
        '1. Preheat oven to 375°F. 2. Cut acorn squash in half and scoop out seeds. 3. Brush with olive oil and season with salt, pepper, and cinnamon. 4. Place cut-side down on a baking sheet and bake for 30 minutes. 5. Meanwhile, rinse quinoa and cook in vegetable broth according to package instructions. 6. In a pan, sauté onion until translucent. 7. Add garlic and cook for 30 seconds. 8. Add spinach and cook until wilted. 9. In a bowl, combine cooked quinoa, sautéed vegetables, dried cranberries, and chopped pecans. 10. Season with salt and pepper. 11. Flip acorn squash halves and fill with quinoa mixture. 12. Drizzle with maple syrup. 13. Return to oven and bake for another 10 minutes.',
        380,
        10,
        60,
        14,
        'Vegetarian',
        'Dinner'
    );
""".trimIndent())

        meals.add("""
    INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
    $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
    $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
    VALUES (
        'Lemon Herb Roasted Chicken',
        'dinner_lemon_chicken',
        'Juicy roasted chicken with bright lemon and herb flavors',
        'Chicken thighs - 1 lb (bone-in, skin-on), Lemon - 1, Garlic - 4 cloves (minced), Fresh rosemary - 2 sprigs, Fresh thyme - 4 sprigs, Olive oil - 2 tbsp, Dijon mustard - 1 tbsp, Honey - 1 tsp, Baby potatoes - 1 lb, Carrots - 2 (sliced), Salt - to taste, Pepper - to taste',
        '1. Preheat oven to 425°F. 2. In a bowl, mix olive oil, lemon juice, lemon zest, garlic, Dijon mustard, and honey. 3. Season chicken thighs with salt and pepper. 4. Place chicken in a baking dish along with potatoes and carrots. 5. Pour the lemon-herb mixture over everything. 6. Add lemon slices, rosemary, and thyme sprigs to the dish. 7. Roast for 35-40 minutes until chicken is cooked through and vegetables are tender.',
        520,
        35,
        40,
        25,
        'Classic',
        'Dinner'
    );
""".trimIndent())

        meals.add("""
    INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
    $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
    $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
    VALUES (
        'Black Bean and Sweet Potato Enchiladas',
        'dinner_bean_enchiladas',
        'Vegetarian enchiladas filled with black beans and sweet potatoes',
        'Corn tortillas - 6, Black beans - 1 can (drained), Sweet potato - 1 large (diced), Onion - 1 (diced), Bell pepper - 1 (diced), Garlic - 2 cloves (minced), Enchilada sauce - 2 cups, Cheese - 1 cup (shredded), Cumin - 1 tsp, Chili powder - 1 tsp, Olive oil - 1 tbsp, Cilantro - for garnish, Greek yogurt - for serving, Salt - to taste, Pepper - to taste',
        '1. Preheat oven to 375°F. 2. Roast diced sweet potato with olive oil, salt, and pepper for 20 minutes. 3. In a pan, sauté onion and bell pepper until soft. 4. Add garlic and cook for 30 seconds. 5. Add black beans, cumin, chili powder, salt, pepper, and roasted sweet potato. 6. Warm tortillas to make them pliable. 7. Fill each tortilla with the bean mixture, roll up, and place in a baking dish. 8. Pour enchilada sauce over the rolled tortillas. 9. Sprinkle with cheese. 10. Bake for 20-25 minutes until cheese is melted and bubbly. 11. Serve with cilantro and Greek yogurt.',
        450,
        18,
        65,
        14,
        'Vegetarian',
        'Dinner'
    );
""".trimIndent())

        meals.add("""
    INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
    $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
    $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
    VALUES (
        'Sesame Ginger Salmon Bowl',
        'dinner_salmon_bowl',
        'Asian-inspired salmon bowl with vegetables and brown rice',
        'Salmon fillet - 6 oz, Brown rice - 1 cup (cooked), Broccoli - 1 cup, Carrots - 1/2 cup (sliced), Edamame - 1/2 cup, Avocado - 1/2, Soy sauce - 2 tbsp, Sesame oil - 1 tsp, Honey - 1 tbsp, Ginger - 1 tbsp (minced), Garlic - 1 clove (minced), Sesame seeds - 1 tbsp, Green onions - for garnish, Salt - to taste, Pepper - to taste',
        '1. Preheat oven to 400°F. 2. In a small bowl, mix soy sauce, sesame oil, honey, ginger, and garlic. 3. Place salmon on a baking sheet and brush with half of the sauce. 4. Bake for 12-15 minutes until salmon flakes easily. 5. Steam broccoli and carrots until tender-crisp. 6. Arrange brown rice, vegetables, edamame, and sliced avocado in a bowl. 7. Top with baked salmon. 8. Drizzle with remaining sauce. 9. Sprinkle with sesame seeds and green onions.',
        520,
        35,
        45,
        22,
        'Protein-rich',
        'Dinner'
    );
""".trimIndent())

        meals.add("""
    INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
    $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
    $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
    VALUES (
        'Mediterranean Baked Halloumi',
        'dinner_baked_halloumi',
        'Baked halloumi cheese with Mediterranean vegetables and herbs',
        'Halloumi cheese - 8 oz, Cherry tomatoes - 1 cup, Zucchini - 1, Bell pepper - 1, Red onion - 1/2, Garlic - 3 cloves, Olive oil - 2 tbsp, Balsamic vinegar - 1 tbsp, Oregano - 1 tsp (dried), Thyme - 1 tsp (dried), Whole wheat couscous - 1 cup (cooked), Fresh parsley - for garnish, Salt - to taste, Pepper - to taste',
        '1. Preheat oven to 400°F. 2. Cut vegetables into similar-sized pieces and place on a baking sheet. 3. Slice halloumi cheese and place among the vegetables. 4. Drizzle with olive oil and balsamic vinegar. 5. Sprinkle with dried herbs, salt, and pepper. 6. Roast for 20-25 minutes until vegetables are tender and cheese is golden. 7. Serve over cooked couscous, garnished with fresh parsley.',
        450,
        25,
        35,
        25,
        'Mediterranean',
        'Dinner'
    );
""".trimIndent())
        return meals
    }

    private fun getSnackMeals(): List<String> {
        val meals = mutableListOf<String>()

        // Snack 1: Greek Yogurt with Honey and Nuts
        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Greek Yogurt with Honey and Nuts',
            'snack_yogurt_nuts',
            'Protein-packed Greek yogurt sweetened with honey and topped with mixed nuts',
            'Greek yogurt - 1 cup, Honey - 1 tbsp, Mixed nuts - 2 tbsp',
            '1. Pour yogurt into a bowl. 2. Drizzle with honey. 3. Top with mixed nuts.',
            220,
            18,
            20,
            10,
            'Protein-rich',
            'Snack'
        );
    """.trimIndent())

        // Thêm 19 món ăn nhẹ mới (cộng với 1 món đã có = 20 món ăn nhẹ)
        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Apple with Almond Butter',
            'snack_apple_almond_butter',
            'Crisp apple slices with creamy almond butter',
            'Apple - 1 medium, Almond butter - 1 tbsp, Cinnamon - a pinch',
            '1. Slice the apple. 2. Spread almond butter on apple slices. 3. Sprinkle with cinnamon.',
            180,
            4,
            25,
            8,
            'Healthy',
            'Snack'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Hummus with Vegetable Sticks',
            'snack_hummus_veggies',
            'Creamy hummus served with fresh vegetable sticks for dipping',
            'Hummus - 1/4 cup, Carrot sticks - 1/2 cup, Cucumber sticks - 1/2 cup, Bell pepper strips - 1/2 cup',
            '1. Arrange vegetable sticks on a plate. 2. Serve with hummus for dipping.',
            150,
            6,
            20,
            6,
            'Vegetarian',
            'Snack'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Trail Mix',
            'snack_trail_mix',
            'Energizing mix of nuts, seeds, and dried fruits',
            'Almonds - 1 tbsp, Walnuts - 1 tbsp, Pumpkin seeds - 1 tbsp, Dried cranberries - 1 tbsp, Dark chocolate chips - 1 tsp',
            '1. Mix all ingredients in a small container. 2. Enjoy!',
            190,
            6,
            12,
            14,
            'Energy-boosting',
            'Snack'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Cottage Cheese with Pineapple',
            'snack_cottage_pineapple',
            'Protein-rich cottage cheese paired with sweet pineapple chunks',
            'Cottage cheese - 1/2 cup, Pineapple chunks - 1/2 cup',
            '1. Place cottage cheese in a bowl. 2. Top with pineapple chunks.',
            150,
            14,
            15,
            3,
            'Protein-rich',
            'Snack'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Avocado Toast Bites',
            'snack_avocado_toast_bites',
            'Mini avocado toast bites on whole grain crackers',
            'Whole grain crackers - 4, Avocado - 1/4, Cherry tomatoes - 4 (halved), Lemon juice - 1 tsp, Red pepper flakes - a pinch, Salt - to taste, Pepper - to taste',
            '1. Mash avocado with lemon juice, salt, and pepper. 2. Spread on crackers. 3. Top each with half a cherry tomato. 4. Sprinkle with red pepper flakes.',
            160,
            4,
            15,
            10,
            'Healthy',
            'Snack'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Protein Smoothie',
            'snack_protein_smoothie',
            'Quick protein shake with fruit for post-workout recovery',
            'Protein powder - 1 scoop, Banana - 1/2, Almond milk - 1 cup, Ice cubes - 3, Peanut butter - 1 tsp',
            '1. Add all ingredients to a blender. 2. Blend until smooth and creamy.',
            220,
            20,
            20,
            6,
            'Protein-rich',
            'Snack'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Edamame',
            'snack_edamame',
            'Steamed edamame pods with sea salt',
            'Edamame pods - 1 cup, Sea salt - 1/4 tsp',
            '1. Steam edamame pods for 5 minutes. 2. Sprinkle with sea salt.',
            120,
            10,
            10,
            5,
            'Protein-rich',
            'Snack'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Turkey and Cheese Roll-Ups',
            'snack_turkey_rollups',
            'Simple protein-packed turkey and cheese roll-ups',
            'Turkey slices - 3, Cheese slices - 2, Lettuce leaves - 3',
            '1. Place a slice of cheese on each turkey slice. 2. Top with a lettuce leaf. 3. Roll up and secure with a toothpick if needed.',
            180,
            20,
            2,
            10,
            'Protein-rich',
            'Snack'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Hard-Boiled Eggs',
            'snack_boiled_eggs',
            'Simple hard-boiled eggs for a portable protein snack',
            'Eggs - 2, Salt - to taste, Pepper - to taste',
            '1. Place eggs in a pot and cover with cold water. 2. Bring to a boil, then reduce heat and simmer for 9 minutes. 3. Transfer to an ice bath to cool. 4. Peel and season with salt and pepper.',
            140,
            12,
            1,
            10,
            'Protein-rich',
            'Snack'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Berry Yogurt Parfait',
            'snack_berry_parfait',
            'Layered yogurt parfait with berries and granola',
            'Greek yogurt - 1/2 cup, Mixed berries - 1/2 cup, Granola - 2 tbsp, Honey - 1 tsp',
            '1. In a glass, layer Greek yogurt, berries, and granola. 2. Repeat layers. 3. Drizzle with honey.',
            200,
            14,
            25,
            5,
            'Healthy',
            'Snack'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Banana with Peanut Butter',
            'snack_banana_peanut_butter',
            'Simple banana with a spread of peanut butter',
            'Banana - 1, Peanut butter - 1 tbsp',
            '1. Slice banana lengthwise. 2. Spread peanut butter on banana slices.',
            190,
            5,
            30,
            8,
            'Energy-boosting',
            'Snack'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Roasted Chickpeas',
            'snack_roasted_chickpeas',
            'Crunchy roasted chickpeas seasoned with spices',
            'Chickpeas - 1/2 cup (drained and rinsed), Olive oil - 1 tsp, Paprika - 1/4 tsp, Cumin - 1/4 tsp, Garlic powder - 1/4 tsp, Salt - to taste',
            '1. Preheat oven to 400°F. 2. Pat chickpeas dry with a paper towel. 3. Toss with olive oil and spices. 4. Spread on a baking sheet and roast for 20-25 minutes until crispy.',
            120,
            6,
            15,
            4,
            'Crunchy',
            'Snack'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Rice Cakes with Avocado',
            'snack_rice_cakes_avocado',
            'Light rice cakes topped with mashed avocado',
            'Rice cakes - 2, Avocado - 1/4, Lemon juice - 1 tsp, Red pepper flakes - a pinch, Salt - to taste',
            '1. Mash avocado with lemon juice, salt, and red pepper flakes. 2. Spread on rice cakes.',
            150,
            3,
            20,
            8,
            'Light',
            'Snack'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Cucumber Cream Cheese Bites',
            'snack_cucumber_cream_cheese',
            'Fresh cucumber slices topped with herbed cream cheese',
            'Cucumber - 1, Cream cheese - 2 tbsp, Fresh dill - 1 tsp (chopped), Lemon juice - 1/2 tsp, Salt - to taste, Pepper - to taste',
            '1. Slice cucumber into rounds. 2. Mix cream cheese with dill, lemon juice, salt, and pepper. 3. Top each cucumber slice with a dollop of the cream cheese mixture.',
            120,
            4,
            6,
            8,
            'Light',
            'Snack'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Chocolate Protein Balls',
            'snack_protein_balls',
            'No-bake protein balls with chocolate and nuts',
            'Oats - 1/4 cup, Protein powder - 1 scoop, Nut butter - 2 tbsp, Honey - 1 tbsp, Dark chocolate chips - 1 tbsp, Chia seeds - 1 tsp',
            '1. Mix all ingredients in a bowl. 2. Form into small balls. 3. Refrigerate for at least 30 minutes before serving.',
            220,
            12,
            20,
            10,
            'Protein-rich',
            'Snack'
        );
    """.trimIndent())

        meals.add("""
        INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
        $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
        $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
        VALUES (
            'Mixed Fruit Cup',
            'snack_fruit_cup',
            'Fresh mixed fruits for a refreshing and sweet snack',
            'Strawberries - 1/2 cup (sliced), Blueberries - 1/4 cup, Kiwi - 1 (sliced), Grapes - 1/4 cup, Mint leaves - for garnish',
            '1. Wash and slice fruits as needed. 2. Combine in a bowl. 3. Garnish with mint leaves.',
            100,
            1,
            25,
            0,
            'Refreshing',
            'Snack'
        );
    """.trimIndent())
        meals.add("""
    INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
    $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
    $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
    VALUES (
        'Cheese and Grape Skewers',
        'snack_cheese_grape_skewers',
        'Simple skewers with cheese cubes and grapes',
        'Cheese (cheddar or gouda) - 1 oz (cubed), Grapes - 10, Toothpicks',
        '1. Thread a cheese cube and a grape onto each toothpick. 2. Arrange on a plate and serve.',
        150,
        7,
        10,
        9,
        'Quick',
        'Snack'
    );
""".trimIndent())

        meals.add("""
    INSERT INTO $TABLE_MEALS ($COLUMN_MEAL_NAME, $COLUMN_MEAL_IMAGE, $COLUMN_MEAL_DESCRIPTION, 
    $COLUMN_MEAL_INGREDIENTS, $COLUMN_MEAL_INSTRUCTIONS, $COLUMN_MEAL_CALORIES, 
    $COLUMN_MEAL_PROTEINS, $COLUMN_MEAL_CARBS, $COLUMN_MEAL_FATS, $COLUMN_MEAL_CATEGORY, $COLUMN_MEAL_TIME)
    VALUES (
        'Celery with Peanut Butter',
        'snack_celery_pb',
        'Classic celery sticks filled with peanut butter',
        'Celery stalks - 3, Peanut butter - 2 tbsp, Raisins - 1 tbsp (optional for "ants on a log")',
        '1. Clean and cut celery into sticks. 2. Fill the center of each celery stick with peanut butter. 3. Top with raisins if desired.',
        200,
        8,
        12,
        16,
        'Classic',
        'Snack'
    );
""".trimIndent())


        return meals
    }

    private fun insertInitialNutritionPlans(db: SQLiteDatabase) {
        // Insert nutrition plans for different age groups and genders
        // Male plans
        val malePlans = listOf(
            """
            INSERT INTO $TABLE_NUTRITION_PLANS (
                $COLUMN_PLAN_NAME, $COLUMN_PLAN_DESCRIPTION, $COLUMN_PLAN_TARGET_GENDER,
                $COLUMN_PLAN_MIN_AGE, $COLUMN_PLAN_MAX_AGE, $COLUMN_PLAN_MEALS
            )
            VALUES (
                'Young Adult Male Plan',
                'Nutrition plan designed for young adult males focusing on muscle development and energy',
                'Male',
                18,
                30,
                '1,3,5,8,10,12,15'
            );
            """.trimIndent(),

            """
            INSERT INTO $TABLE_NUTRITION_PLANS (
                $COLUMN_PLAN_NAME, $COLUMN_PLAN_DESCRIPTION, $COLUMN_PLAN_TARGET_GENDER,
                $COLUMN_PLAN_MIN_AGE, $COLUMN_PLAN_MAX_AGE, $COLUMN_PLAN_MEALS
            )
            VALUES (
                'Middle-aged Male Plan',
                'Balanced nutrition plan for middle-aged males focusing on heart health and maintaining muscle mass',
                'Male',
                31,
                50,
                '2,4,6,9,11,13,16'
            );
            """.trimIndent(),

            """
            INSERT INTO $TABLE_NUTRITION_PLANS (
                $COLUMN_PLAN_NAME, $COLUMN_PLAN_DESCRIPTION, $COLUMN_PLAN_TARGET_GENDER,
                $COLUMN_PLAN_MIN_AGE, $COLUMN_PLAN_MAX_AGE, $COLUMN_PLAN_MEALS
            )
            VALUES (
                'Senior Male Plan',
                'Nutritious plan for senior males focusing on bone health, protein intake, and overall wellness',
                'Male',
                51,
                90,
                '1,4,7,9,11,14,17'
            );
            """.trimIndent()
        )

        // Female plans
        val femalePlans = listOf(
            """
            INSERT INTO $TABLE_NUTRITION_PLANS (
                $COLUMN_PLAN_NAME, $COLUMN_PLAN_DESCRIPTION, $COLUMN_PLAN_TARGET_GENDER,
                $COLUMN_PLAN_MIN_AGE, $COLUMN_PLAN_MAX_AGE, $COLUMN_PLAN_MEALS
            )
            VALUES (
                'Young Adult Female Plan',
                'Balanced nutrition plan for young adult females focusing on energy, iron intake, and overall health',
                'Female',
                18,
                30,
                '2,3,6,8,10,13,15'
            );
            """.trimIndent(),

            """
            INSERT INTO $TABLE_NUTRITION_PLANS (
                $COLUMN_PLAN_NAME, $COLUMN_PLAN_DESCRIPTION, $COLUMN_PLAN_TARGET_GENDER,
                $COLUMN_PLAN_MIN_AGE, $COLUMN_PLAN_MAX_AGE, $COLUMN_PLAN_MEALS
            )
            VALUES (
                'Middle-aged Female Plan',
                'Nutrition plan for middle-aged females focusing on calcium intake, metabolism, and heart health',
                'Female',
                31,
                50,
                '1,4,6,9,11,14,16'
            );
            """.trimIndent(),

            """
            INSERT INTO $TABLE_NUTRITION_PLANS (
                $COLUMN_PLAN_NAME, $COLUMN_PLAN_DESCRIPTION, $COLUMN_PLAN_TARGET_GENDER,
                $COLUMN_PLAN_MIN_AGE, $COLUMN_PLAN_MAX_AGE, $COLUMN_PLAN_MEALS
            )
            VALUES (
                'Senior Female Plan',
                'Nutritious plan for senior females focusing on bone health, protein intake, and overall wellness',
                'Female',
                51,
                90,
                '2,5,7,9,12,14,17'
            );
            """.trimIndent()
        )

        for (plan in malePlans) {
            db.execSQL(plan)
        }

        for (plan in femalePlans) {
            db.execSQL(plan)
        }
    }

    // Methods for exporting and importing database (for transferring between devices)
    fun exportDatabase(destinationFile: File): Boolean {
        try {
            if (!databaseDir.exists()) {
                databaseDir.mkdirs()
            }

            val currentDB = File(databasePath)

            if (currentDB.exists()) {
                val src = FileInputStream(currentDB).channel
                val dst = FileOutputStream(destinationFile).channel
                dst.transferFrom(src, 0, src.size())
                src.close()
                dst.close()
                return true
            }
            return false
        } catch (e: IOException) {
            Log.e(TAG, "Error exporting database: ${e.message}")
            return false
        }
    }

    fun importDatabase(sourceFile: File): Boolean {
        try {
            close()

            val src = FileInputStream(sourceFile).channel
            val dst = FileOutputStream(File(databasePath)).channel
            dst.transferFrom(src, 0, src.size())
            src.close()
            dst.close()

            // Reopen the database connection
            SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READWRITE)
            return true
        } catch (e: IOException) {
            Log.e(TAG, "Error importing database: ${e.message}")
            return false
        }
    }
}