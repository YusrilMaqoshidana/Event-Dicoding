package id.usereal.eventdicoding.ui.settings

import SettingsViewModel
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider

import id.usereal.eventdicoding.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val switchTheme = binding.darkModeSwitch
        val pref = SettingPreferences.getInstance(requireContext().dataStore)

        settingsViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(pref)
        )[SettingsViewModel::class.java]
        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.saveThemeSetting(isChecked)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("SettingsFragment", "onDestroyView: Fragment view destroyed")
        _binding = null
    }
}
