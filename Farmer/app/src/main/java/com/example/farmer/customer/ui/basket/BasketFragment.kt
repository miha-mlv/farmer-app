package com.example.farmer.customer.ui.basket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.farmer.customer.data.local.AppDatabase
import com.example.farmer.customer.data.network.RetrofitClientCustomer
import com.example.farmer.customer.data.repository.ProductBasketRepository
import com.example.farmer.customer.ui.basket.adapter.BasketAdapter
import com.example.farmer.customer.ui.productsboard.ViewModelFactory
import com.example.farmer.databinding.FragmentBasketBinding


class BasketFragment : Fragment() {

    private val viewModel: BasketViewModel by viewModels {
        ViewModelFactory(
            mapOf(
                BasketViewModel::class.java to {
                    BasketViewModel(
                        application = requireActivity().application,
                        basketRepository = ProductBasketRepository(
                            apiService = RetrofitClientCustomer.instance,
                            basketDao = AppDatabase.getDatabase(requireActivity().application)
                                .basketDao()
                        )
                    )
                }
            ))
    }

    private var _binding: FragmentBasketBinding? = null
    private val binding get() = _binding!!

    private val adapter = BasketAdapter(
        onPlus = { item -> viewModel.plusQuantity(item) },
        onMinus = { item -> viewModel.minusQuantity(item) },
        onDelete = { item -> viewModel.removeItem(item) },
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBasketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvCart.adapter = adapter

        observeOnViewModel()
    }

    private fun observeOnViewModel() {
        viewModel.basketItems.observe(viewLifecycleOwner) { items ->
            if (items.isNullOrEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvCart.visibility = View.GONE
                binding.checkoutCard.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.rvCart.visibility = View.VISIBLE
                binding.checkoutCard.visibility = View.VISIBLE
                adapter.submitList(items) // DiffUtil сам анимирует изменения
            }
        }

        viewModel.totalPrice.observe(viewLifecycleOwner) { total ->
            binding.tvTotal.text = "$total ₽" // Сумма с доставкой
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BasketFragment().apply {}
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}