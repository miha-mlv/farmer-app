package com.example.farmer.customer.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.farmer.R
import com.example.farmer.common.auth.MainActivity
import com.example.farmer.common.util.TokenManager
import com.example.farmer.databinding.FragmentProductsBinding
import com.example.farmer.databinding.FragmentProfileBinding
import com.example.farmer.databinding.FragmentProfileCustomerBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileCustomerFragment : Fragment() {

    private val tokenManager: TokenManager by lazy {
        TokenManager(activity?.application ?: throw Exception("Ошибка токен менеджера"))
    }
    private var _binding: FragmentProfileCustomerBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileCustomerBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnExit.setOnClickListener {
            tokenManager.logout()
            startActivity(
                Intent(
                    activity,
                    MainActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            requireActivity().finish()
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileCustomerFragment().apply {
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}