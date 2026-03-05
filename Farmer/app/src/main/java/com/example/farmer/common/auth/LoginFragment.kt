package com.example.farmer.common.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.farmer.R
import com.example.farmer.common.util.TokenManager
import com.example.farmer.customer.ui.MainCustomerActivity
import com.example.farmer.databinding.FragmentLoginBinding
import com.example.farmer.farmer.MainFarmerActivity
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels() {
        AuthViewModelFactory(
            application = activity?.application ?: throw Exception("Нету application для вьюМодели")
        )
    }
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val email = arguments?.getString("userEmail").toString()
        binding.etEmail.setText(if (email == "null") "" else email)
        binding.tvCreateAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.btnLogin.setOnClickListener { setOnClickLogin() }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is AuthViewModel.LoginState.Loading -> {
                            //Показать прогресс бар
                        }

                        is AuthViewModel.LoginState.Success -> {
                            navigateByRole(state.role)
                        }

                        is AuthViewModel.LoginState.Error -> {
                            Toast.makeText(activity, state.message, Toast.LENGTH_LONG).show()
                        }

                        else -> Unit
                    }
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(email: String, password: String): LoginFragment {
            return LoginFragment()
        }
    }

    fun navigateByRole(role: String) {
        when (role) {
            "Buyer" -> {
                startActivity(
                    Intent(
                        activity,
                        MainCustomerActivity::class.java
                    ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                requireActivity().finish()
            }

            "Farmer" -> {
                startActivity(
                    Intent(
                        activity,
                        MainFarmerActivity::class.java
                    ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                requireActivity().finish()
            }
        }
    }

    private fun setOnClickLogin() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        viewModel.login(email = email, password = password)
    }
}