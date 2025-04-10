//package com.example.nutritionapp.activities
//
//import android.content.Intent
//import android.os.Bundle
//import android.view.View
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.example.nutritionapp.App
//import com.example.nutritionapp.R
//import com.example.nutritionapp.database.entities.User
//import kotlinx.android.synthetic.main.activity_login.*
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//class LoginActivity : AppCompatActivity() {
//
//    private val userDao by lazy { App.getInstance().database.userDao() }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)
//
//        setupListeners()
//    }
//
//    private fun setupListeners() {
//        // Register button click
//        textViewRegister.setOnClickListener {
//            startActivity(Intent(this, RegisterActivity::class.java))
//        }
//
//        // Login button click
//        buttonLogin.setOnClickListener {
//            if (validateInputs()) {
//                login()
//            }
//        }
//
//        // Forgot password click (can be implemented if needed)
//        textViewForgotPassword.setOnClickListener {
//            Toast.makeText(this, "Reset password functionality coming soon!", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun validateInputs(): Boolean {
//        var isValid = true
//
//        // Validate email
//        val email = editTextEmail.text.toString().trim()
//        if (email.isEmpty()) {
//            textInputLayoutEmail.error = "Email is required"
//            isValid = false
//        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            textInputLayoutEmail.error = "Invalid email format"
//            isValid = false
//        } else {
//            textInputLayoutEmail.error = null
//        }
//
//        // Validate password
//        val password = editTextPassword.text.toString()
//        if (password.isEmpty()) {
//            textInputLayoutPassword.error = "Password is required"
//            isValid = false
//        } else {
//            textInputLayoutPassword.error = null
//        }
//
//        return isValid
//    }
//
//    private fun login() {
//        val email = editTextEmail.text.toString().trim()
//        val password = editTextPassword.text.toString()
//
//        // Show progress
//        progressBar.visibility = View.VISIBLE
//        buttonLogin.isEnabled = false
//
//        CoroutineScope(Dispatchers.IO).launch {
//            val user = userDao.login(email, password)
//
//            withContext(Dispatchers.Main) {
//                progressBar.visibility = View.GONE
//                buttonLogin.isEnabled = true
//
//                if (user != null) {
//                    // Login successful
//                    loginSuccess(user)
//                } else {
//                    // Login failed
//                    Toast.makeText(
//                        this@LoginActivity,
//                        "Invalid email or password",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//    }
//
//    private fun loginSuccess(user: User) {
//        // Save user session
//        App.getInstance().preferenceManager.saveLoginSession(
//            user.id,
//            user.email,
//            user.username
//        )
//
//        // Navigate to MainActivity
//        startActivity(Intent(this, MainActivity::class.java))
//        finishAffinity() // Close all activities in the stack
//    }
//}

package com.example.nutritionapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nutritionapp.App
import com.example.nutritionapp.R
import com.example.nutritionapp.database.entities.User
import com.example.nutritionapp.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val userDao by lazy { App.getInstance().database.userDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        // Register button click
        binding.textViewRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Login button click
        binding.buttonLogin.setOnClickListener {
            if (validateInputs()) {
                login()
            }
        }

        // Forgot password click (can be implemented if needed)
        binding.textViewForgotPassword.setOnClickListener {
            Toast.makeText(this, "Reset password functionality coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

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
        } else {
            binding.textInputLayoutPassword.error = null
        }

        return isValid
    }

    private fun login() {
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString()

        // Show progress
        binding.progressBar.visibility = View.VISIBLE
        binding.buttonLogin.isEnabled = false

        CoroutineScope(Dispatchers.IO).launch {
            val user = userDao.login(email, password)

            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE
                binding.buttonLogin.isEnabled = true

                if (user != null) {
                    // Login successful
                    loginSuccess(user)
                } else {
                    // Login failed
                    Toast.makeText(
                        this@LoginActivity,
                        "Invalid email or password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun loginSuccess(user: User) {
        // Save user session
        App.getInstance().preferenceManager.saveLoginSession(
            user.id,
            user.email,
            user.username
        )

        // Navigate to MainActivity
        val intent = Intent(this, MainActivity::class.java)
        // Add flags to create a new task and clear previous activities
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Just finish the current login activity
    }
}