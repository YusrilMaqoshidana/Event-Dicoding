package id.usereal.eventdicoding.ui.bottomnavigasi

import SettingsViewModel
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import id.usereal.eventdicoding.R
import id.usereal.eventdicoding.databinding.ActivityMainBinding
import id.usereal.eventdicoding.ui.settings.SettingPreferences
import id.usereal.eventdicoding.ui.settings.dataStore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Menggunakan ViewBinding untuk inflasi layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mendapatkan instance SettingPreferences dari DataStore
        val pref = SettingPreferences.getInstance(application.dataStore)

        // Inisialisasi SettingsViewModel menggunakan ViewModelFactory
        settingsViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(pref)
        )[SettingsViewModel::class.java]

        // Observasi perubahan tema dari SettingsViewModel
        settingsViewModel.getThemeSettings().observe(this) { isDarkModeActive ->
            // Mengatur mode malam berdasarkan pengaturan dari DataStore
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        // Setup BottomNavigationView dan NavController untuk navigasi antar fragment
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_upcoming, R.id.navigation_finished, R.id.navigation_favorite, R.id.navigation_settings
            )
        )

        // Menghubungkan BottomNavigationView dengan NavController
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    // Menghandle navigasi kembali ke fragment sebelumnya
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
