package com.example.farmer.customer.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.farmer.customer.data.ThemeManager
import com.example.farmer.databinding.FragmentSettingsCustomerBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class SettingsCustomerFragment : Fragment() {

    private var _binding: FragmentSettingsCustomerBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsCustomerBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val themeManager = ThemeManager(this.context)

        lifecycleScope.launch {
            val isDark = themeManager.isDarkMode.first() ?: false
            binding.themeSwitch.isChecked = isDark

            updateTheme(isDark)
        }

        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                themeManager.setDarkMode(isChecked)
                updateTheme(isChecked)
            }
        }
    }

    private fun updateTheme(isDark: Boolean) {
        val mode = if (isDark) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsCustomerFragment().apply {}
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}