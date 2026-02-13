package com.example.farmer.customer.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.farmer.R
import com.example.farmer.common.auth.MainActivity
import com.example.farmer.common.util.TokenManager
import com.example.farmer.databinding.FragmentProductsBinding
import com.example.farmer.databinding.FragmentProfileBinding
import com.example.farmer.databinding.FragmentProfileCustomerBinding


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
        setOption()
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

    private fun setOption() {
        with(binding) {
//            binding.itemPoints.root.setOnClickListener {
//                findNavController().navigate(R.id.action_profileFragment_to_pointSaleFragment)
//            }

            itemSettings.optionTitle.text = "Настройки"
            itemSettings.optionIcon.setImageResource(R.drawable.ic_settings)

            itemOrders.optionTitle.text = "Заказы"
            itemOrders.optionIcon.setImageResource(R.drawable.ic_order)

            itemNotifications.optionTitle.text = "Уведомления"
            itemNotifications.optionIcon.setImageResource(R.drawable.ic_notification)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileCustomerFragment().apply {}
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}