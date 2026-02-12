package com.example.farmer.customer.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.farmer.R
import com.example.farmer.databinding.ActivityMainCustomerBinding
import com.example.farmer.databinding.ActivityMainFarmerBinding
import com.yandex.mapkit.MapKitFactory

class MainCustomerActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainCustomerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("e793a345-d9df-4603-8bc9-1a28bfc5d024")
        MapKitFactory.initialize(this)
        binding = ActivityMainCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nhfCustomer) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }
}