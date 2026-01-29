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
import com.example.farmer.databinding.FragmentVerifEmailBinding


class VerifEmailFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels() {
        AuthViewModelFactory(
            application = activity?.application ?: throw Exception("нету application")
        )
    }
    private var _binding: FragmentVerifEmailBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVerifEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val email = arguments?.getString("userEmail").toString()
        binding.tvSendEmail.text = getString(R.string.confirmation_text, email)
        binding.btnLogin.setOnClickListener {
            if (binding.etVerificationCode.text.toString().isNotEmpty()) {
                viewModel.verification(
                    code = binding.etVerificationCode.text.toString(),
                    email = email
                )
            }
        }

        viewModel.verificationResult.observe(viewLifecycleOwner) { result ->
            if (result) {
                val bundle = Bundle().apply {
                    putString("userEmail", email)
                }
                Toast.makeText(
                    activity,
                    "Успешная верификация",
                    Toast.LENGTH_LONG
                ).show()
                findNavController().navigate(
                    R.id.action_verifEmailFragment_to_loginFragment2,
                    bundle
                )
            } else {
                Toast.makeText(activity, "Что то пошло не так!", Toast.LENGTH_LONG).show()
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
            VerifEmailFragment().apply {}
    }
}