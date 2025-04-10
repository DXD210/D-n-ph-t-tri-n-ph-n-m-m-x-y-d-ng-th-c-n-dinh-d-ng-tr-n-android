//package com.example.nutritionapp.activities
//
//import android.content.Intent
//import android.os.Bundle
//import android.view.Menu
//import android.view.MenuItem
//import androidx.appcompat.app.AlertDialog
//import androidx.appcompat.app.AppCompatActivity
//import androidx.navigation.NavController
//import androidx.navigation.findNavController
//import androidx.navigation.ui.AppBarConfiguration
//import androidx.navigation.ui.navigateUp
//import androidx.navigation.ui.setupActionBarWithNavController
//import androidx.navigation.ui.setupWithNavController
//import com.example.nutritionapp.App
//import com.example.nutritionapp.R
//import kotlinx.android.synthetic.main.activity_main.*
//import java.io.File
//
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var navController: NavController
//    private lateinit var appBarConfiguration: AppBarConfiguration
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        // Setup toolbar
//        setSupportActionBar(toolbar)
//
//        // Setup navigation
//        setupNavigation()
//    }
//
//    private fun setupNavigation() {
//        navController = findNavController(R.id.nav_host_fragment)
//
//        // Setup the bottom navigation view with navController
//        bottomNavigationView.setupWithNavController(navController)
//
//        // Setup the ActionBar with navController and appBarConfiguration
//        appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.dailyMealFragment,
//                R.id.nutritionPlanFragment,
//                R.id.favoritesFragment,
//                R.id.notesFragment
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
//
//        // Update toolbar title based on destination
//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            toolbar.title = destination.label
//        }
//    }
//
//    override fun onSupportNavigateUp(): Boolean {
//        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.main_menu, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.action_export_db -> {
//                exportDatabase()
//                true
//            }
//            R.id.action_import_db -> {
//                importDatabase()
//                true
//            }
//            R.id.action_logout -> {
//                logout()
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
//
//    private fun exportDatabase() {
//        // Create a file in Downloads directory
//        val exportFile = File(getExternalFilesDir(null), "nutrition_app_backup.db")
//
//        val success = App.getDatabase(this).exportDatabase(this, exportFile)
//
//        if (success) {
//            AlertDialog.Builder(this)
//                .setTitle("Database Exported")
//                .setMessage("Database has been exported to:\n${exportFile.absolutePath}")
//                .setPositiveButton("OK", null)
//                .show()
//        } else {
//            AlertDialog.Builder(this)
//                .setTitle("Export Failed")
//                .setMessage("Failed to export database.")
//                .setPositiveButton("OK", null)
//                .show()
//        }
//    }
//
//    private fun importDatabase() {
//        // Show confirmation dialog
//        AlertDialog.Builder(this)
//            .setTitle("Import Database")
//            .setMessage("This will replace your current data with the imported data. Continue?")
//            .setPositiveButton("Import") { _, _ ->
//                // In a real app, you would use a file picker
//                // For simplicity, we'll look for the file in a known location
//                val importFile = File(getExternalFilesDir(null), "nutrition_app_backup.db")
//
//                if (importFile.exists()) {
//                    val success = App.getDatabase(this).importDatabase(this, importFile)
//
//                    if (success) {
//                        AlertDialog.Builder(this)
//                            .setTitle("Import Successful")
//                            .setMessage("Database has been imported successfully. The app will restart.")
//                            .setPositiveButton("OK") { _, _ ->
//                                // Restart the app to reload data
//                                val intent = Intent(this, MainActivity::class.java)
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                                startActivity(intent)
//                                finish()
//                            }
//                            .show()
//                    } else {
//                        AlertDialog.Builder(this)
//                            .setTitle("Import Failed")
//                            .setMessage("Failed to import database.")
//                            .setPositiveButton("OK", null)
//                            .show()
//                    }
//                } else {
//                    AlertDialog.Builder(this)
//                        .setTitle("File Not Found")
//                        .setMessage("Backup file not found at: ${importFile.absolutePath}")
//                        .setPositiveButton("OK", null)
//                        .show()
//                }
//            }
//            .setNegativeButton("Cancel", null)
//            .show()
//    }
//
//    private fun logout() {
//        // Show confirmation dialog
//        AlertDialog.Builder(this)
//            .setTitle("Logout")
//            .setMessage("Are you sure you want to logout?")
//            .setPositiveButton("Logout") { _, _ ->
//                // Clear user session
//                App.getInstance().preferenceManager.clearLoginSession()
//
//                // Navigate to login screen
//                val intent = Intent(this, LoginActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(intent)
//                finish()
//            }
//            .setNegativeButton("Cancel", null)
//            .show()
//    }
//}

package com.example.nutritionapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.nutritionapp.App
import com.example.nutritionapp.R
import com.example.nutritionapp.database.AppDatabase
import com.example.nutritionapp.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar
        setSupportActionBar(binding.toolbar)

        // Setup navigation
        setupNavigation()
    }

    private fun setupNavigation() {
        // Find NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        // Get NavController
        navController = navHostFragment.navController

        // Setup bottom navigation
        binding.bottomNavigationView.setupWithNavController(navController)

        // Setup ActionBar with NavController
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.dailyMealFragment,
                R.id.nutritionPlanFragment,
                R.id.favoritesFragment,
                R.id.notesFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Update toolbar title based on destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.toolbar.title = destination.label
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_export_db -> {
                exportDatabase()
                true
            }
            R.id.action_import_db -> {
                importDatabase()
                true
            }
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun exportDatabase() {
        val exportFile = File(getExternalFilesDir(null), "nutrition_app_backup.db")

        val success = AppDatabase.exportDatabase(this, exportFile)

        if (success) {
            AlertDialog.Builder(this)
                .setTitle("Database Exported")
                .setMessage("Database has been exported to:\n${exportFile.absolutePath}")
                .setPositiveButton("OK", null)
                .show()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Export Failed")
                .setMessage("Failed to export database.")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    private fun importDatabase() {
        AlertDialog.Builder(this)
            .setTitle("Import Database")
            .setMessage("This will replace your current data with the imported data. Continue?")
            .setPositiveButton("Import") { _, _ ->
                val importFile = File(getExternalFilesDir(null), "nutrition_app_backup.db")

                if (importFile.exists()) {
                    val success = AppDatabase.importDatabase(this, importFile)

                    if (success) {
                        AlertDialog.Builder(this)
                            .setTitle("Import Successful")
                            .setMessage("Database has been imported successfully. The app will restart.")
                            .setPositiveButton("OK") { _, _ ->
                                // Restart the app to reload data
                                val intent = Intent(this, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                                finish()
                            }
                            .show()
                    } else {
                        AlertDialog.Builder(this)
                            .setTitle("Import Failed")
                            .setMessage("Failed to import database.")
                            .setPositiveButton("OK", null)
                            .show()
                    }
                } else {
                    AlertDialog.Builder(this)
                        .setTitle("File Not Found")
                        .setMessage("Backup file not found at: ${importFile.absolutePath}")
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun logout() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                // Clear user session
                App.getInstance().preferenceManager.clearLoginSession()

                // Navigate to login screen
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}