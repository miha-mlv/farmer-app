package com.example.farmer.customer.ui.profile

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
import com.example.farmer.customer.data.network.RetrofitClientCustomer
import com.example.farmer.customer.data.repository.ProfileCustomerRepository
import com.example.farmer.customer.ui.ViewModelFactory
import com.example.farmer.databinding.FragmentProductsBinding
import com.example.farmer.databinding.FragmentProfileBinding
import com.example.farmer.databinding.FragmentProfileCustomerBinding
import kotlinx.coroutines.launch


class ProfileCustomerFragment : Fragment() {

    private val tokenManager: TokenManager by lazy {
        TokenManager(activity?.application ?: throw Exception("Ошибка токен менеджера"))
    }

    private val viewModel: ProfileViewModel by viewModels {
        ViewModelFactory(
            mapOf(ProfileViewModel::class.java to {
                ProfileViewModel(
                    application = requireActivity().application,
                    profileCustomerRepository = ProfileCustomerRepository(apiService = RetrofitClientCustomer.instance),
                    tokenManager = TokenManager(requireActivity().application)
                )
            })
        )
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
        _binding = FragmentProfileCustomerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOption()
        viewModel.getProfile()
        binding.btnExit.setOnClickListener {
            viewModel.logOut()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.profile.collect { profileResponse ->
                        binding.tvCustomerName.text = profileResponse?.customerName ?: " "
                        binding.tvCustomerEmail.text = profileResponse?.customerEmail ?: " "
                    }
                }

                launch {
                    viewModel.logout.collect {
                        startActivity(
                            Intent(
                                activity,
                                MainActivity::class.java
                            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                        requireActivity().finish()
                    }
                }
            }
        }


    }

    private fun setOption() {
        with(binding) {
//            binding.itemPoints.root.setOnClickListener {
//                findNavController().navigate(R.id.action_profileFragment_to_pointSaleFragment)
//            }

            itemSettings.optionTitle.text = "Настройки"
            itemSettings.optionIcon.setImageResource(R.drawable.ic_settings)
            itemSettings.root.setOnClickListener {
                findNavController().navigate(R.id.action_profileCustomerFragment_to_settingsCustomerFragment)
            }

            itemOrders.optionTitle.text = "Заказы"
            itemOrders.optionIcon.setImageResource(R.drawable.ic_order)
            itemOrders.root.setOnClickListener {
                findNavController().navigate(R.id.action_profileCustomerFragment_to_profileOrdersFragment)
            }

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