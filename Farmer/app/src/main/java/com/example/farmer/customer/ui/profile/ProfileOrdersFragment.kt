package com.example.farmer.customer.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.farmer.R
import com.example.farmer.common.util.TokenManager
import com.example.farmer.customer.data.network.RetrofitClientCustomer
import com.example.farmer.customer.data.repository.ProfileCustomerRepository
import com.example.farmer.customer.ui.ViewModelFactory
import com.example.farmer.customer.ui.profile.adapter.OrderHistoryAdapter
import com.example.farmer.customer.ui.profile.adapter.OrdersAdapter
import com.example.farmer.databinding.FragmentProfileOrdersBinding
import kotlinx.coroutines.launch

class ProfileOrdersFragment : Fragment() {


    private val viewModel: ProfileViewModel by viewModels {
        ViewModelFactory(
            mapOf(
            ProfileViewModel::class.java to {
                ProfileViewModel(
                    application = requireActivity().application,
                    profileCustomerRepository = ProfileCustomerRepository(apiService = RetrofitClientCustomer.instance),
                    tokenManager = TokenManager(requireActivity().application)
                )
            }
        ))
    }
    private var _binding: FragmentProfileOrdersBinding? = null
    private val binding get() = _binding!!

    private lateinit var orderHistoryAdapter: OrderHistoryAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        setupRecyclerView()
        observeViewModel()
        viewModel.getMyOrders()


    }

    private fun setupRecyclerView() {
        // Инициализируем адаптер с обработкой клика
        orderHistoryAdapter = OrderHistoryAdapter { order ->
            val bundle = bundleOf("SELECTED_ORDER" to order)
            findNavController().navigate(R.id.action_profileOrdersFragment_to_detailOrderCustomerFragment, bundle)
        }

        binding.rvOrders.apply {
            adapter = orderHistoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    viewModel.orderArr.collect { orders ->
                        orderHistoryAdapter.submitList(orders)
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileOrdersFragment().apply {
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}