package com.example.farmer.farmer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.farmer.R
import com.example.farmer.customer.ui.profile.adapter.OrderHistoryAdapter
import com.example.farmer.databinding.FragmentOrderFarmerBinding
import com.example.farmer.farmer.adapter.OrderAdapter
import com.example.farmer.farmer.network.RetrofitClientFarmer
import com.example.farmer.farmer.repository.OrderRepository
import kotlinx.coroutines.launch


class OrderFarmerFragment : Fragment() {

    private val viewModel: OrderViewModel by viewModels {
        FarmerViewModelFactory(
            mapOf(
                OrderViewModel::class.java to {
                    OrderViewModel(
                        requireActivity().application, OrderRepository(
                            RetrofitClientFarmer.instance
                        )
                    )
                }
            )
        )
    }
    private var _binding: FragmentOrderFarmerBinding? = null
    private val binding get() = _binding!!

    private var selectedCategory: Button? = null

    private lateinit var orderAdapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrderFarmerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        val categories =
            listOf("Все", "В ожидании", "Активные", "Отклоненные", "Завершенные", "Отмененные")
        categories.forEach { category ->
            val button = layoutInflater.inflate(R.layout.container_item, null) as Button
            button.text = category

            button.setOnClickListener {
                selectCategory(button)
                applyFilter(button.text.toString())
            }

            binding.container.addView(button)

            // Выбираем первую категорию по умолчанию
            if (category == "All") {
                selectCategory(button)
            }
        }

        setupRecyclerView()
        observeViewModel()
        viewModel.getMyOrders()
    }

    private fun setupRecyclerView() {

        orderAdapter = OrderAdapter(onAcceptClick = {
            Toast.makeText(requireContext(), "acceptn", Toast.LENGTH_SHORT).show()
        }) { order ->

            val bundle = bundleOf("orderId" to order.id)
            //findNavController().navigate(R.id.action_history_to_details, bundle)
        }

        binding.rvOrders.apply {
            adapter = orderAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    viewModel.orderArr.collect { orders ->
                        orderAdapter.submitList(orders)
                    }
                }
            }
        }
    }

    private fun applyFilter(filter: String) {

        when (filter) {
            "Все" -> {
                filterAllOrders()
                showMessage("Показаны все заказы")
            }

            "В ожидании" -> {
                filterCurrentOrders()
                showMessage("Показаны текущие заказы")
            }

            "Активные" -> {
                filterCompletedOrders()
                showMessage("Показаны завершенные заказы")
            }

            "Отклоненные" -> {
                filterCancelledOrders()
                showMessage("Показаны отмененные заказы")
            }

            "Завершенные" -> {
                filterByLastMonth()
                showMessage("Заказы за последний месяц")
            }

            "Отмененные" -> {
                filterByLastSixMonths()
                showMessage("Заказы за последние полгода")
            }
        }
    }

    // Методы фильтрации
    private fun filterAllOrders() {
        // Логика для показа всех заказов
        // Например, обновление RecyclerView адаптера
    }

    private fun filterCurrentOrders() {
        // Логика для текущих заказов
    }

    private fun filterCompletedOrders() {
        // Логика для завершенных заказов
    }

    private fun filterCancelledOrders() {
        // Логика для отмененных заказов
    }

    private fun filterByLastMonth() {
        // Логика для фильтрации за последний месяц

        // Пример фильтрации по дате
    }

    private fun filterByLastSixMonths() {
        // Логика для фильтрации за последние 6 месяцев
    }

    private fun showMessage(message: String) {
        Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()
    }


    private fun selectCategory(button: Button) {
        selectedCategory?.isSelected = false
        button.isSelected = true
        selectedCategory = button
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrderFarmerFragment().apply {}
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}