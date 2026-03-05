package com.example.farmer.customer.ui.basket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.farmer.R
import com.example.farmer.common.util.TokenManager
import com.example.farmer.customer.data.local.AppDatabase
import com.example.farmer.customer.data.local.entity.BasketItem
import com.example.farmer.customer.data.network.RetrofitClientCustomer
import com.example.farmer.customer.data.network.model.OrderResponse
import com.example.farmer.customer.data.repository.ProductBasketRepository
import com.example.farmer.customer.ui.basket.adapter.BasketAdapter
import com.example.farmer.customer.ui.ViewModelFactory
import com.example.farmer.databinding.FragmentBasketBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


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
                        ),
                        tokenManager = TokenManager(requireActivity().application)
                    )
                }
            ))
    }

    private var _binding: FragmentBasketBinding? = null
    private val binding get() = _binding!!
    private var products = listOf<BasketItem>()

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

        binding.btnOrder.setOnClickListener {
            val items = adapter.currentList // Берем данные из твоего адаптера
            val total =
                items.sumOf { it.priceText.dropLast(3).toInt() * it.quantity } // Пример подсчета

            if (items.isNotEmpty()) {
                viewModel.sendOrder(items, total)
            }
        }

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
                products = items
                adapter.submitList(items) // DiffUtil сам анимирует изменения
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            // repeatOnLifecycle дождется, пока фрагмент станет активным
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.resultRequest.collect { result ->
                        result?.let {
                            if (it.isSuccess) {
                                // Успех! Очищаем корзину и переходим на другой экран
                                viewModel.clearBasket()
                                showSuccessDialog(it.getOrNull())
                            } else {
                                // Ошибка от сервера (например, 400 или 500)
                                showError(it.exceptionOrNull()?.message ?: "Ошибка при заказе")
                            }
                        }
                    }
                }

                launch {
                    viewModel.error.collect { error ->
                        showError(error)
                        Toast.makeText(
                            requireContext(),
                            "Результат запроса: $error",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

            }
        }

        viewModel.totalPrice.observe(viewLifecycleOwner) { total ->
            binding.tvTotal.text = "$total ₽" // Сумма с доставкой
        }

    }

    private fun showSuccessDialog(response: OrderResponse?) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Заказ оформлен")
            .setMessage("Ваш заказ разделен на ${response?.count} части(ей) по фермерам.")
            .setPositiveButton("ОК") { _, _ ->
                // Навигация, например, в профиль или историю заказов
                findNavController().navigateUp()
            }
            .show()
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
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