package com.example.nutritionapp.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nutritionapp.App
import com.example.nutritionapp.R
import com.example.nutritionapp.database.entities.User
import com.example.nutritionapp.databinding.ActivityRegisterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val userDao by lazy { App.getInstance().database.userDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        // Back button click
        binding.imageViewBack.setOnClickListener {
            onBackPressed()
        }

        // Login text click
        binding.textViewLogin.setOnClickListener {
            onBackPressed()
        }

        // Register button click
        binding.buttonRegister.setOnClickListener {
            if (validateInputs()) {
                register()
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Validate username
        val username = binding.editTextUsername.text.toString().trim()
        if (username.isEmpty()) {
            binding.textInputLayoutUsername.error = "Username is required"
            isValid = false
        } else {
            binding.textInputLayoutUsername.error = null
        }

        // Validate email
        val email = binding.editTextEmail.text.toString().trim()
        if (email.isEmpty()) {
            binding.textInputLayoutEmail.error = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.textInputLayoutEmail.error = "Invalid email format"
            isValid = false
        } else {
            binding.textInputLayoutEmail.error = null
        }

        // Validate password
        val password = binding.editTextPassword.text.toString()
        if (password.isEmpty()) {
            binding.textInputLayoutPassword.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            binding.textInputLayoutPassword.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            binding.textInputLayoutPassword.error = null
        }

        // Validate confirm password
        val confirmPassword = binding.editTextConfirmPassword.text.toString()
        if (confirmPassword.isEmpty()) {
            binding.textInputLayoutConfirmPassword.error = "Confirm Password is required"
            isValid = false
        } else if (confirmPassword != password) {
            binding.textInputLayoutConfirmPassword.error = "Passwords do not match"
            isValid = false
        } else {
            binding.textInputLayoutConfirmPassword.error = null
        }

        // Validate age
        val ageText = binding.editTextAge.text.toString().trim()
        if (ageText.isNotEmpty()) {
            val age = ageText.toIntOrNull()
            if (age == null || age <= 0 || age > 120) {
                binding.textInputLayoutAge.error = "Please enter a valid age"
                isValid = false
            } else {
                binding.textInputLayoutAge.error = null
            }
        }

        return isValid
    }

    private fun register() {
        val username = binding.editTextUsername.text.toString().trim()
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString()

        val gender = when {
            binding.radioButtonMale.isChecked -> "Male"
            binding.radioButtonFemale.isChecked -> "Female"
            else -> null
        }

        val age = binding.editTextAge.text.toString().trim().toIntOrNull()

        // Show progress
        binding.progressBar.visibility = View.VISIBLE
        binding.buttonRegister.isEnabled = false

        CoroutineScope(Dispatchers.IO).launch {
            // Check if email already exists
            val existingUser = userDao.getUserByEmail(email)

            if (existingUser != null) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.buttonRegister.isEnabled = true
                    binding.textInputLayoutEmail.error = "Email already exists"
                }
                return@launch
            }

            // Create new user
            val user = User(
                username = username,
                email = email,
                password = password,
                gender = gender,
                age = age
            )

            // Insert user to database
            val userId = userDao.insert(user)

            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE
                binding.buttonRegister.isEnabled = true

                if (userId > 0) {
                    // Registration successful
                    Toast.makeText(
                        this@RegisterActivity,
                        "Registration successful! Please login.",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish() // Go back to login screen
                } else {
                    // Registration failed
                    Toast.makeText(
                        this@RegisterActivity,
                        "Registration failed. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}