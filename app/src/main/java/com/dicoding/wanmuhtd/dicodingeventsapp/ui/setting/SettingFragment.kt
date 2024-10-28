package com.dicoding.wanmuhtd.dicodingeventsapp.ui.setting

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.dicoding.wanmuhtd.dicodingeventsapp.databinding.FragmentSettingBinding
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.ViewModelFactory
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.past.PastEventViewModel

@Suppress("viewModel","unused")
class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var workManager: WorkManager
    private lateinit var periodicWorkRequest: PeriodicWorkRequest

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(
                    requireContext(),
                    "Notifications permission allowed",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Notifications permission rejected",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        val pref = SettingPreferences.getInstance(requireActivity().dataStore)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireContext(), pref)
        @Suppress("UNUSED_VARIABLE")
        val viewModel: PastEventViewModel by viewModels<PastEventViewModel> { factory }

        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val switchTheme = binding.switchTheme
        val switchDailyReminder = binding.switchDailyReminder
        val pref = SettingPreferences.getInstance(requireContext().dataStore)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity(), pref)
        val viewModel: SettingViewModel by viewModels<SettingViewModel> {
            factory
        }

        workManager = WorkManager.getInstance(requireContext())

        viewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                switchTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                switchTheme.isChecked = false
            }
        }
        switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            viewModel.saveThemeSetting(isChecked)
        }

        viewModel.getDailyReminderSetting()
            .observe(viewLifecycleOwner) { isDailyReminderActive: Boolean ->
                if (isDailyReminderActive) {
                    viewModel.scheduleDailyReminder(this.requireContext())
                    switchDailyReminder.isChecked = true
                } else {
                    viewModel.cancelDailyReminder(this.requireContext())
                    switchDailyReminder.isChecked = false
                }
            }

        switchDailyReminder.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            viewModel.saveDailyReminderSetting(isChecked)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
