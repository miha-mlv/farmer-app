package com.example.farmer.customer.ui.productsboard

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.farmer.customer.data.local.AppDatabase
import com.example.farmer.customer.data.network.RetrofitClientCustomer
import com.example.farmer.customer.data.repository.ProductBasketRepository
import com.example.farmer.customer.data.repository.ProductRepository
import com.example.farmer.customer.ui.ViewModelFactory
import com.example.farmer.customer.ui.productsboard.adapter.ProductPagingAdapter
import com.example.farmer.databinding.FilterPopupBinding
import com.example.farmer.databinding.FragmentProductsBinding
import com.yandex.mapkit.logo.Padding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64


class ProductsFragment : Fragment() {

    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductsViewModel by viewModels {
        ViewModelFactory(
            mapOf(
                ProductsViewModel::class.java to {
                    ProductsViewModel(
                        requireActivity().application,
                        ProductRepository(RetrofitClientCustomer.instance),
                        ProductBasketRepository(
                            RetrofitClientCustomer.instance,
                            AppDatabase.getDatabase(requireActivity().application).basketDao()
                        )
                    )
                }
            ))
    }

    private val adapter = ProductPagingAdapter(
        onProductClick = { product ->
            val action = ProductsFragmentDirections.actionProductsFragmentToProductDetailFragment(product)
            findNavController().navigate(action)
        },
        onAddBasketClick = { product ->
            viewModel.addToBasket(product)
            Toast.makeText(
                requireContext(),
                "Товар ${product.name} добавлен в корзину",
                Toast.LENGTH_SHORT
            ).show()
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.productsRecyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.products.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        binding.searchEditText.addTextChangedListener{
            viewModel.searchQuery.value = it?.toString() ?: ""
        }

        binding.btnFilter.setOnClickListener {
            showFilterPopup()
        }




    }
    private fun showFilterPopup() {
        val inflater = LayoutInflater.from(requireContext())
        val popupBinding = FilterPopupBinding.inflate(inflater)


        val popup = PopupWindow(
            popupBinding.root,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true // Позволяет закрыть по нажатию вне окна
        )
        popup.elevation = 10f

        // Настройка Спиннера категорий (пример)
        val categories = listOf("Овощи", "Фрукты", "Молочное", "Мясо", "Зерновые", "Другое")
        popupBinding.spinnerCategory.adapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_dropdown_item,
            categories
        )

        // Кнопка применить
        popupBinding.btnApply.setOnClickListener {
            val min = popupBinding.etMinPrice.text.toString().toDoubleOrNull()
            val max = popupBinding.etMaxPrice.text.toString().toDoubleOrNull()
            val category = popupBinding.spinnerCategory.selectedItem.toString()

            viewModel.setFilters(min, max, category)
            popup.dismiss()
        }

        // Кнопка очистить
        popupBinding.btnClear.setOnClickListener {
            viewModel.setFilters(null, null, null)
            popup.dismiss()
        }

        // Показываем под кнопкой фильтра с небольшим смещением
        popup.showAsDropDown(binding.btnFilter, -150, 10)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProductsFragment().apply {}
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}