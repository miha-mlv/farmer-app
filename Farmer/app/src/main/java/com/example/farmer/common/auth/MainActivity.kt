package com.example.farmer.common.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.farmer.common.util.TokenManager
import com.example.farmer.customer.ui.MainCustomerActivity
import com.example.farmer.databinding.ActivityMainBinding
import com.example.farmer.farmer.MainFarmerActivity

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tokenManager = TokenManager(applicationContext)
        //tokenManager.logout()
        Log.d("token: ","${tokenManager.getToken()}")
        if (tokenManager.getToken() != null) {
            val role = tokenManager.getRole()
            when (role) {
                "Buyer" -> {
                    startActivity(
                        Intent(
                            this,
                            MainCustomerActivity::class.java
                        ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                    finish()
                    return
                }

                "Farmer" -> {
                    startActivity(
                        Intent(
                            this,
                            MainFarmerActivity::class.java
                        ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                    finish()
                    return
                }
            }

        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}