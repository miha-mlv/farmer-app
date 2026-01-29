package com.example.farmer.farmer

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.farmer.R
import com.example.farmer.common.auth.MainActivity
import com.example.farmer.common.util.TokenManager
import com.example.farmer.customer.MainCustomerActivity
import com.example.farmer.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch
import kotlin.getValue


class ProfileFarmerFragment : Fragment() {

    private val tokenManager: TokenManager by lazy{
        TokenManager(activity?.application ?: throw Exception("Ошибка токен менеджера"))
    }
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FarmerViewModel by viewModels() {
        FarmerViewModelFactory(
            application = activity?.application
                ?: throw Exception("Нету apllication для вьюмодели(фермер)")
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOption()
        binding.btnLogOut.setOnClickListener { setOnBtnLogOut() }
        observerOnViewModel()
        viewModel.getProfile()


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun observerOnViewModel(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    viewModel.profileData.collect { profile ->
                        binding.tvFarmName.text = profile.farmName
//                        binding.tvLocation = profile.location
                    }
                }
            }
        }
    }
    private fun setOnBtnLogOut(){
        tokenManager.logout()
        startActivity(
            Intent(
                activity,
                MainActivity::class.java
            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        requireActivity().finish()
    }

    private fun setOption(){
        with(binding){
            itemPersonalInfo.optionTitle.text = "Личные данные"
            itemPersonalInfo.optionIcon.setImageResource(R.drawable.icon_profile)


            itemSettings.optionTitle.text = "Настройки"
            itemSettings.optionIcon.setImageResource(R.drawable.ic_settings)

            itemOrders.optionTitle.text = "Заказы"
            itemOrders.optionIcon.setImageResource(R.drawable.ic_order)

            itemPoints.optionTitle.text = "Точки продаж"
            itemPoints.optionIcon.setImageResource(R.drawable.icon_my_product)
            binding.itemPoints.root.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_pointSaleFragment)
            }


            itemNotifications.optionTitle.text = "Уведомления"
            itemNotifications.optionIcon.setImageResource(R.drawable.ic_notification)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFarmerFragment().apply {
            }
    }
}