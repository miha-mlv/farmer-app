package com.example.farmer.customer.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.farmer.R
import com.example.farmer.customer.data.ThemeManager
import com.example.farmer.customer.data.local.AppDatabase
import com.example.farmer.customer.data.network.RetrofitClientCustomer
import com.example.farmer.customer.data.repository.ProductBasketRepository
import com.example.farmer.customer.data.repository.ProductRepository
import com.example.farmer.customer.ui.productsboard.ProductsViewModel
import com.example.farmer.customer.ui.ViewModelFactory
import com.example.farmer.databinding.ActivityMainCustomerBinding
import com.example.farmer.databinding.ActivityMainFarmerBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yandex.mapkit.MapKitFactory
import kotlinx.coroutines.launch
import kotlin.getValue

class MainCustomerActivity : AppCompatActivity() {
    private lateinit var themeManager: ThemeManager
    private val viewModel: MainCustomerViewModel by viewModels {
        ViewModelFactory(
            mapOf(
                MainCustomerViewModel::class.java to {
                    MainCustomerViewModel(
                        application,
                        ProductBasketRepository(
                            RetrofitClientCustomer.instance,
                            AppDatabase.getDatabase(application).basketDao()
                        )
                    )
                }
            ))
    }
    lateinit var binding: ActivityMainCustomerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        themeManager = ThemeManager(this)
        lifecycleScope.launch {
            themeManager.isDarkMode.collect { isDark ->
                isDark?.let {
                    val mode = if (it) AppCompatDelegate.MODE_NIGHT_YES
                    else AppCompatDelegate.MODE_NIGHT_NO

                    // Обновляем тему, если она отличается от текущей
                    if (AppCompatDelegate.getDefaultNightMode() != mode) {
                        AppCompatDelegate.setDefaultNightMode(mode)
                    }
                }
            }
        }
        super.onCreate(savedInstanceState)
        binding = ActivityMainCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nhfCustomer) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
        setupCartBadge()
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.profileCustomerFragment,
                R.id.basketFragment,
                R.id.productsFragment -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }

                else -> {
                    binding.bottomNavigationView.visibility = View.GONE
                }
            }
        }
    }

    private fun setupCartBadge() {
        val navView: BottomNavigationView = binding.bottomNavigationView // твой BottomBar

        // Получаем бейдж для пункта меню "Корзина"
        val badge = navView.getOrCreateBadge(R.id.basketFragment) // ID из твоего menu.xml

        viewModel.basketCount.observe(this) { count ->
            if (count > 0) {
                badge.isVisible = true
                badge.number = count
                // Можно настроить цвета
                badge.backgroundColor = ContextCompat.getColor(this, R.color.stock_low)
                badge.badgeTextColor = ContextCompat.getColor(this, R.color.white)
            } else {
                badge.isVisible = false
            }
        }
    }
}