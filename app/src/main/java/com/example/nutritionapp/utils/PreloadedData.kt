package com.example.nutritionapp.utils

import com.example.nutritionapp.database.entities.Meal
import com.example.nutritionapp.database.entities.NutritionPlan
import java.util.*

/**
 * Lớp chứa dữ liệu mẫu để điền vào cơ sở dữ liệu khi khởi tạo
 */
object PreloadedData {

    /**
     * Trả về danh sách tất cả các món ăn
     */
    fun getAllMeals(): List<Meal> {
        return getBreakfastMeals() + getLunchMeals() + getDinnerMeals() + getSnackMeals()
    }

    /**
     * Trả về danh sách tất cả kế hoạch dinh dưỡng
     */
    fun getAllNutritionPlans(): List<NutritionPlan> {
        return getMalePlans() + getFemalePlans()
    }

    /**
     * Trả về danh sách các món ăn sáng
     */
    private fun getBreakfastMeals(): List<Meal> {
        val meals = mutableListOf<Meal>()

        // Classic Oatmeal with Fruits
        meals.add(
            Meal(
                name = "Classic Oatmeal with Fruits",
                image = "breakfast_oatmeal",
                description = "A nutritious and filling breakfast bowl of oatmeal topped with fresh fruits and honey",
                ingredients = "Rolled oats - 1/2 cup, Milk - 1 cup, Banana - 1, Berries - 1/2 cup, Honey - 1 tbsp, Cinnamon - 1/4 tsp",
                instructions = "1. Bring milk to a simmer in a small pot. 2. Add oats and cook for 5 minutes, stirring occasionally. 3. Transfer to a bowl and top with sliced banana, berries, honey, and cinnamon.",
                calories = 310.0,
                proteins = 10.0,
                carbs = 56.0,
                fats = 6.0,
                category = "Healthy",
                mealTime = "Breakfast"
            )
        )

        // Avocado Toast with Eggs
        meals.add(
            Meal(
                name = "Avocado Toast with Eggs",
                image = "breakfast_avocado_toast",
                description = "Whole grain toast topped with mashed avocado and poached eggs",
                ingredients = "Whole grain bread - 2 slices, Avocado - 1, Eggs - 2, Salt - to taste, Pepper - to taste, Red pepper flakes - a pinch",
                instructions = "1. Toast the bread slices. 2. Mash the avocado and spread on toast. 3. Poach or fry the eggs. 4. Place eggs on top of avocado toast. 5. Season with salt, pepper, and red pepper flakes.",
                calories = 420.0,
                proteins = 18.0,
                carbs = 30.0,
                fats = 26.0,
                category = "Healthy",
                mealTime = "Breakfast"
            )
        )

        // Greek Yogurt Parfait
        meals.add(
            Meal(
                name = "Greek Yogurt Parfait",
                image = "breakfast_yogurt_parfait",
                description = "Creamy Greek yogurt layered with granola and fresh berries",
                ingredients = "Greek yogurt - 1 cup, Granola - 1/4 cup, Mixed berries - 1/2 cup, Honey - 1 tbsp",
                instructions = "1. In a glass or bowl, add a layer of Greek yogurt. 2. Add a layer of granola. 3. Add a layer of mixed berries. 4. Repeat the layers. 5. Drizzle with honey.",
                calories = 280.0,
                proteins = 18.0,
                carbs = 38.0,
                fats = 8.0,
                category = "Healthy",
                mealTime = "Breakfast"
            )
        )

        // Vegetable Omelette
        meals.add(
            Meal(
                name = "Vegetable Omelette",
                image = "breakfast_omelette",
                description = "Fluffy omelette filled with sautéed vegetables and cheese",
                ingredients = "Eggs - 3, Bell pepper - 1/4, Onion - 1/4, Spinach - 1/2 cup, Cheddar cheese - 1/4 cup, Salt - to taste, Pepper - to taste, Olive oil - 1 tsp",
                instructions = "1. Beat eggs in a bowl with salt and pepper. 2. Heat oil in a pan and sauté vegetables until soft. 3. Pour beaten eggs over vegetables. 4. Once the bottom is set, sprinkle cheese on top. 5. Fold omelette in half and cook until eggs are fully set.",
                calories = 350.0,
                proteins = 24.0,
                carbs = 8.0,
                fats = 24.0,
                category = "Protein-rich",
                mealTime = "Breakfast"
            )
        )

        // Protein Smoothie Bowl
        meals.add(
            Meal(
                name = "Protein Smoothie Bowl",
                image = "breakfast_smoothie_bowl",
                description = "Thick smoothie topped with nutritious toppings for a satisfying breakfast",
                ingredients = "Banana - 1, Frozen berries - 1 cup, Protein powder - 1 scoop, Almond milk - 1/2 cup, Chia seeds - 1 tbsp, Sliced fruits and nuts for topping",
                instructions = "1. Blend banana, frozen berries, protein powder, and almond milk until smooth. 2. Pour into a bowl. 3. Top with chia seeds, sliced fruits, and nuts.",
                calories = 380.0,
                proteins = 25.0,
                carbs = 45.0,
                fats = 10.0,
                category = "Protein-rich",
                mealTime = "Breakfast"
            )
        )

        return meals
    }

    /**
     * Trả về danh sách các món ăn trưa
     */
    private fun getLunchMeals(): List<Meal> {
        val meals = mutableListOf<Meal>()

        // Quinoa Salad with Grilled Chicken
        meals.add(
            Meal(
                name = "Quinoa Salad with Grilled Chicken",
                image = "lunch_quinoa_chicken",
                description = "Protein-rich quinoa salad with grilled chicken breast and vegetables",
                ingredients = "Quinoa - 1 cup (cooked), Chicken breast - 4 oz, Cherry tomatoes - 1/2 cup, Cucumber - 1/2, Red onion - 1/4, Feta cheese - 2 tbsp, Olive oil - 1 tbsp, Lemon juice - 1 tbsp, Salt - to taste, Pepper - to taste",
                instructions = "1. Season chicken with salt and pepper, then grill until cooked through. 2. In a bowl, combine cooked quinoa, diced tomatoes, cucumber, and red onion. 3. Add crumbled feta cheese. 4. Slice the grilled chicken and place on top. 5. Drizzle with olive oil and lemon juice, then toss to combine.",
                calories = 420.0,
                proteins = 30.0,
                carbs = 38.0,
                fats = 16.0,
                category = "Protein-rich",
                mealTime = "Lunch"
            )
        )

        // Vegetable Stir-Fry with Tofu
        meals.add(
            Meal(
                name = "Vegetable Stir-Fry with Tofu",
                image = "lunch_stirfry_tofu",
                description = "Colorful vegetable stir-fry with crispy tofu cubes served over brown rice",
                ingredients = "Firm tofu - 8 oz, Bell peppers - 1, Broccoli - 1 cup, Carrots - 1, Snap peas - 1/2 cup, Garlic - 2 cloves, Ginger - 1 tbsp (minced), Soy sauce - 2 tbsp, Sesame oil - 1 tbsp, Brown rice - 1 cup (cooked)",
                instructions = "1. Press and drain tofu, then cut into cubes. 2. Heat oil in a wok or large pan and cook tofu until golden. 3. Remove tofu and add vegetables, garlic, and ginger. 4. Stir-fry until vegetables are tender-crisp. 5. Add tofu back to the pan with soy sauce. 6. Serve over brown rice.",
                calories = 380.0,
                proteins = 18.0,
                carbs = 45.0,
                fats = 14.0,
                category = "Vegetarian",
                mealTime = "Lunch"
            )
        )

        // Mediterranean Chickpea Salad
        meals.add(
            Meal(
                name = "Mediterranean Chickpea Salad",
                image = "lunch_chickpea_salad",
                description = "Refreshing chickpea salad with Mediterranean flavors and olive oil dressing",
                ingredients = "Chickpeas - 1 can (drained), Cucumber - 1, Cherry tomatoes - 1 cup, Red onion - 1/4, Feta cheese - 1/4 cup, Kalamata olives - 10, Parsley - 1/4 cup (chopped), Olive oil - 2 tbsp, Lemon juice - 1 tbsp, Salt - to taste, Pepper - to taste",
                instructions = "1. Combine chickpeas, diced cucumber, halved cherry tomatoes, diced red onion, crumbled feta, and olives in a bowl. 2. In a small bowl, whisk together olive oil, lemon juice, salt, and pepper. 3. Pour dressing over salad and toss. 4. Garnish with chopped parsley before serving.",
                calories = 380.0,
                proteins = 15.0,
                carbs = 42.0,
                fats = 18.0,
                category = "Vegetarian",
                mealTime = "Lunch"
            )
        )

        return meals
    }

    /**
     * Trả về danh sách các món ăn tối
     */
    private fun getDinnerMeals(): List<Meal> {
        val meals = mutableListOf<Meal>()

        // Grilled Salmon with Roasted Vegetables
        meals.add(
            Meal(
                name = "Grilled Salmon with Roasted Vegetables",
                image = "dinner_salmon_vegetables",
                description = "Omega-3 rich salmon fillet with a side of roasted seasonal vegetables",
                ingredients = "Salmon fillet - 6 oz, Asparagus - 8 spears, Cherry tomatoes - 1/2 cup, Zucchini - 1, Olive oil - 1 tbsp, Lemon - 1, Garlic - 2 cloves, Fresh herbs - 2 tbsp, Salt - to taste, Pepper - to taste",
                instructions = "1. Preheat oven to 400°F. 2. Place vegetables on a baking sheet, drizzle with olive oil, salt, and pepper. 3. Roast for 15-20 minutes. 4. Season salmon with salt, pepper, and herbs. 5. Grill or pan-sear salmon for 3-4 minutes per side. 6. Serve with roasted vegetables and lemon wedges.",
                calories = 380.0,
                proteins = 34.0,
                carbs = 12.0,
                fats = 22.0,
                category = "Protein-rich",
                mealTime = "Dinner"
            )
        )

        // Baked Chicken with Sweet Potato
        meals.add(
            Meal(
                name = "Baked Chicken with Sweet Potato",
                image = "dinner_chicken_sweet_potato",
                description = "Herb-roasted chicken breast with baked sweet potato and steamed broccoli",
                ingredients = "Chicken breast - 6 oz, Sweet potato - 1 medium, Broccoli - 1 cup, Olive oil - 1 tbsp, Garlic powder - 1/2 tsp, Rosemary - 1 tsp (dried), Thyme - 1/2 tsp (dried), Salt - to taste, Pepper - to taste",
                instructions = "1. Preheat oven to 375°F. 2. Wash sweet potato and pierce several times with a fork. 3. Bake sweet potato for 45-60 minutes until soft. 4. Season chicken with olive oil, garlic powder, rosemary, thyme, salt, and pepper. 5. Bake chicken for 25-30 minutes. 6. Steam broccoli until tender. 7. Serve chicken with sweet potato and broccoli.",
                calories = 450.0,
                proteins = 38.0,
                carbs = 45.0,
                fats = 10.0,
                category = "Protein-rich",
                mealTime = "Dinner"
            )
        )

        // Vegetarian Chili
        meals.add(
            Meal(
                name = "Vegetarian Chili",
                image = "dinner_veg_chili",
                description = "Hearty vegetarian chili with beans, vegetables, and warm spices",
                ingredients = "Black beans - 1 can (drained), Kidney beans - 1 can (drained), Diced tomatoes - 1 can, Onion - 1, Bell pepper - 1, Carrots - 2, Garlic - 3 cloves, Olive oil - 1 tbsp, Chili powder - 2 tsp, Cumin - 1 tsp, Paprika - 1 tsp, Vegetable broth - 1 cup, Salt - to taste, Pepper - to taste, Avocado - 1/2 (for garnish), Greek yogurt - 2 tbsp (for garnish)",
                instructions = "1. Heat olive oil in a large pot and sauté diced onion, bell pepper, and carrots until soft. 2. Add minced garlic and cook for 30 seconds. 3. Add chili powder, cumin, and paprika, and stir for 1 minute. 4. Add beans, diced tomatoes, and vegetable broth. 5. Bring to a boil, then reduce heat and simmer for 30 minutes. 6. Season with salt and pepper. 7. Serve topped with diced avocado and a dollop of Greek yogurt.",
                calories = 350.0,
                proteins = 15.0,
                carbs = 55.0,
                fats = 8.0,
                category = "Vegetarian",
                mealTime = "Dinner"
            )
        )

        return meals
    }

    /**
     * Trả về danh sách các món ăn nhẹ
     */
    private fun getSnackMeals(): List<Meal> {
        val meals = mutableListOf<Meal>()

        // Greek Yogurt with Honey and Nuts
        meals.add(
            Meal(
                name = "Greek Yogurt with Honey and Nuts",
                image = "snack_yogurt_nuts",
                description = "Protein-packed Greek yogurt sweetened with honey and topped with mixed nuts",
                ingredients = "Greek yogurt - 1 cup, Honey - 1 tbsp, Mixed nuts - 2 tbsp",
                instructions = "1. Pour yogurt into a bowl. 2. Drizzle with honey. 3. Top with mixed nuts.",
                calories = 220.0,
                proteins = 18.0,
                carbs = 20.0,
                fats = 10.0,
                category = "Protein-rich",
                mealTime = "Snack"
            )
        )

        // Apple with Almond Butter
        meals.add(
            Meal(
                name = "Apple with Almond Butter",
                image = "snack_apple_almond_butter",
                description = "Crisp apple slices with creamy almond butter",
                ingredients = "Apple - 1 medium, Almond butter - 1 tbsp, Cinnamon - a pinch",
                instructions = "1. Slice the apple. 2. Spread almond butter on apple slices. 3. Sprinkle with cinnamon.",
                calories = 180.0,
                proteins = 4.0,
                carbs = 25.0,
                fats = 8.0,
                category = "Healthy",
                mealTime = "Snack"
            )
        )

        // Trail Mix
        meals.add(
            Meal(
                name = "Trail Mix",
                image = "snack_trail_mix",
                description = "Energizing mix of nuts, seeds, and dried fruits",
                ingredients = "Almonds - 1 tbsp, Walnuts - 1 tbsp, Pumpkin seeds - 1 tbsp, Dried cranberries - 1 tbsp, Dark chocolate chips - 1 tsp",
                instructions = "1. Mix all ingredients in a small container. 2. Enjoy!",
                calories = 190.0,
                proteins = 6.0,
                carbs = 12.0,
                fats = 14.0,
                category = "Energy-boosting",
                mealTime = "Snack"
            )
        )

        return meals
    }

    /**
     * Trả về danh sách các kế hoạch dinh dưỡng cho nam giới
     */
    private fun getMalePlans(): List<NutritionPlan> {
        val plans = mutableListOf<NutritionPlan>()

        // Young Adult Male Plan
        plans.add(
            NutritionPlan(
                name = "Young Adult Male Plan",
                description = "Nutrition plan designed for young adult males focusing on muscle development and energy",
                targetGender = "Male",
                minAge = 18,
                maxAge = 30,
                meals = "1,3,5,8,10,12",
                caloriesGoal = 2500.0,
                proteinsGoal = 180.0,
                carbsGoal = 300.0,
                fatsGoal = 70.0
            )
        )

        // Middle-aged Male Plan
        plans.add(
            NutritionPlan(
                name = "Middle-aged Male Plan",
                description = "Balanced nutrition plan for middle-aged males focusing on heart health and maintaining muscle mass",
                targetGender = "Male",
                minAge = 31,
                maxAge = 50,
                meals = "2,4,6,9,11,13",
                caloriesGoal = 2200.0,
                proteinsGoal = 150.0,
                carbsGoal = 250.0,
                fatsGoal = 60.0
            )
        )

        // Senior Male Plan
        plans.add(
            NutritionPlan(
                name = "Senior Male Plan",
                description = "Nutritious plan for senior males focusing on bone health, protein intake, and overall wellness",
                targetGender = "Male",
                minAge = 51,
                maxAge = 90,
                meals = "1,4,7,9,11,14",
                caloriesGoal = 1800.0,
                proteinsGoal = 120.0,
                carbsGoal = 200.0,
                fatsGoal = 60.0
            )
        )

        return plans
    }

    /**
     * Trả về danh sách các kế hoạch dinh dưỡng cho nữ giới
     */
    private fun getFemalePlans(): List<NutritionPlan> {
        val plans = mutableListOf<NutritionPlan>()

        // Young Adult Female Plan
        plans.add(
            NutritionPlan(
                name = "Young Adult Female Plan",
                description = "Balanced nutrition plan for young adult females focusing on energy, iron intake, and overall health",
                targetGender = "Female",
                minAge = 18,
                maxAge = 30,
                meals = "2,3,6,8,10,13",
                caloriesGoal = 2000.0,
                proteinsGoal = 120.0,
                carbsGoal = 250.0,
                fatsGoal = 60.0
            )
        )

        // Middle-aged Female Plan
        plans.add(
            NutritionPlan(
                name = "Middle-aged Female Plan",
                description = "Nutrition plan for middle-aged females focusing on calcium intake, metabolism, and heart health",
                targetGender = "Female",
                minAge = 31,
                maxAge = 50,
                meals = "1,4,6,9,11,14",
                caloriesGoal = 1800.0,
                proteinsGoal = 100.0,
                carbsGoal = 200.0,
                fatsGoal = 55.0
            )
        )

        // Senior Female Plan
        plans.add(
            NutritionPlan(
                name = "Senior Female Plan",
                description = "Nutritious plan for senior females focusing on bone health, protein intake, and overall wellness",
                targetGender = "Female",
                minAge = 51,
                maxAge = 90,
                meals = "2,5,7,9,12,14",
                caloriesGoal = 1600.0,
                proteinsGoal = 90.0,
                carbsGoal = 180.0,
                fatsGoal = 50.0
            )
        )

        return plans
    }
}