package com.dicoding.wanmuhtd.dicodingeventsapp


import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.WorkManager
import com.dicoding.wanmuhtd.dicodingeventsapp.databinding.ActivityMainBinding
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.ViewModelFactory
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.setting.SettingPreferences
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.setting.SettingViewModel
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.setting.dataStore
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var workManager: WorkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        val pref = SettingPreferences.getInstance(this.dataStore)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this, pref)
        val viewModel: SettingViewModel by viewModels<SettingViewModel> {
            factory
        }

        viewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
}