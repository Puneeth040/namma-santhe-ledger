package com.example.nammasantheledger.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nammasantheledger.R
import com.example.nammasantheledger.databinding.FragmentSettingsAboutBinding
import com.example.nammasantheledger.ui.ThemePreferenceManager
import com.google.android.material.snackbar.Snackbar

class SettingsAboutFragment : Fragment() {
    private var _binding: FragmentSettingsAboutBinding? = null
    private val binding get() = _binding!!
    private lateinit var themePrefs: ThemePreferenceManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        themePrefs = ThemePreferenceManager(requireContext())
        val versionName = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0).versionName
        binding.tvVersion.text = getString(R.string.app_version_label) + ": " + versionName
        when (themePrefs.currentThemeMode()) {
            AppCompatDelegate.MODE_NIGHT_NO -> binding.tgTheme.check(binding.btnThemeLight.id)
            AppCompatDelegate.MODE_NIGHT_YES -> binding.tgTheme.check(binding.btnThemeDark.id)
            else -> binding.tgTheme.check(binding.btnThemeSystem.id)
        }
        binding.tgTheme.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            val mode = when (checkedId) {
                binding.btnThemeLight.id -> AppCompatDelegate.MODE_NIGHT_NO
                binding.btnThemeDark.id -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
            themePrefs.saveTheme(mode)
            Snackbar.make(binding.root, "Theme updated", Snackbar.LENGTH_SHORT).show()
        }
        binding.btnOpenCreditScreen.setOnClickListener {
            findNavController().navigate(R.id.quickCreditFragment)
        }
        binding.btnOpenPaymentScreen.setOnClickListener {
            findNavController().navigate(R.id.paymentFragment)
        }
        binding.btnOpenCustomerScreen.setOnClickListener {
            findNavController().navigate(R.id.customerListFragment)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
