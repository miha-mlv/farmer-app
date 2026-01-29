package com.example.farmer.common.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.farmer.R
import com.example.farmer.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels() {
        AuthViewModelFactory(
            application = activity?.application ?: throw Exception("нету application")
        )
    }
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.registrationResult.observe(viewLifecycleOwner) { result ->
            if (result) {
                val bundle = Bundle().apply {
                    putString("userEmail", binding.etEmail.text.toString())
                }
                Toast.makeText(
                    activity,
                    "Успешная регистрация\nПодтвердите почту",
                    Toast.LENGTH_LONG
                ).show()
                findNavController().navigate(
                    R.id.action_registerFragment_to_verifEmailFragment2,
                    bundle
                )
            } else {
                Toast.makeText(activity, "Повторите снова", Toast.LENGTH_LONG).show()
            }

        }

        binding.btnRegister.setOnClickListener { setOnClickBtnRegister() }
        binding.toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    binding.btnBuyer.id -> {
                        // Прячем поле "Farm Name"
                        binding.tvFarmName.visibility = View.GONE
                        binding.etFarmName.visibility = View.GONE
                    }

                    binding.btnFarmer.id -> {
                        // Показываем поле "Farm Name"
                        binding.tvFarmName.visibility = View.VISIBLE
                        binding.etFarmName.visibility = View.VISIBLE
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
        fun newInstance(param1: String, param2: String) =
            RegisterFragment().apply {}
    }

    private fun setOnClickBtnRegister() {
        val roleId = binding.toggleGroup.checkedButtonId
        if (roleId == binding.btnBuyer.id) {
            val email = binding.etEmail.text.toString()
            val username = binding.etName.text.toString()
            val password = binding.etPassword.text.toString()
            val role = "Buyer"
            viewModel.register(
                email = email,
                username = username,
                password = password,
                role = role
            )
        } else {
            val email = binding.etEmail.text.toString()
            val username = binding.etName.text.toString()
            val password = binding.etPassword.text.toString()
            val farmName = binding.etFarmName.text.toString()
            val role = "Farmer"
            viewModel.register(
                email = email,
                username = username,
                password = password,
                role = role,
                farmName = farmName
            )
        }
    }
}