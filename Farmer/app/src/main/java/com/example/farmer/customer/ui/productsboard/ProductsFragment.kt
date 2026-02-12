package com.example.farmer.customer.ui.productsboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.farmer.customer.data.local.AppDatabase
import com.example.farmer.customer.data.network.RetrofitClientCustomer
import com.example.farmer.customer.data.repository.ProductRepository
import com.example.farmer.customer.ui.productsboard.adapter.ProductPagingAdapter
import com.example.farmer.databinding.FragmentProductsBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ProductsFragment : Fragment() {

    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductsViewModel by viewModels {
        ViewModelFactory(
            mapOf(
            ProductsViewModel::class.java to {
                ProductsViewModel(
                    requireActivity().application,
                    ProductRepository(RetrofitClientCustomer.instance)
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

            // -------------- метод вьюмодели добавления в локальную бд

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