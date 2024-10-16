package id.usereal.eventdicoding.ui.settings

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import id.usereal.eventdicoding.databinding.FragmentSettingsBinding
import id.usereal.eventdicoding.viewmodel.SettingsViewModel
import id.usereal.eventdicoding.viewmodel.ViewModelFactory

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val settingViewModel: SettingsViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        // Observe dark mode settings
        settingViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
            binding.darkModeSwitch.isChecked = isDarkModeActive
        }

        // Observe reminder settings
        settingViewModel.getReminderState().observe(viewLifecycleOwner) { isReminderActive ->
            binding.reminderSwitch.isChecked = isReminderActive
        }
    }

    private fun setupListeners() {
        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingViewModel.saveThemeSetting(isChecked)
        }

        binding.reminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingViewModel.setReminder(isChecked)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("SettingsFragment", "onDestroyView: Fragment view destroyed")
        _binding = null
    }
}
