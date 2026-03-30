package com.example.farmer.farmer

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.farmer.R
import com.example.farmer.databinding.ActivityMainFarmerBinding
import com.example.farmer.databinding.FragmentProfileBinding
import com.yandex.mapkit.MapKitFactory

class MainFarmerActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainFarmerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
//        MapKitFactory.setApiKey("e793a345-d9df-4603-8bc9-1a28bfc5d024")
//        MapKitFactory.initialize(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainFarmerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nhfFarmer) as NavHostFragment
        val navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, destination, arguments ->
            when (destination.id) {
                R.id.chatFragment, R.id.orderFarmerFragment, R.id.pointSaleFragment, R.id.detailOrderFarmerFragment -> {
                    binding.bottomNavigationView.visibility = View.GONE
                }

                else -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }
            }
        }

        binding.bottomNavigationView.setupWithNavController(navController)

    }
}