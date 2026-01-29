package com.example.farmer.farmer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.farmer.R
import com.example.farmer.databinding.FragmentLoginBinding
import com.example.farmer.databinding.FragmentMyProductBinding
import com.example.farmer.farmer.adapter.ProductAdapter
import kotlinx.coroutines.launch
import kotlin.getValue

class MyProductFragment : Fragment() {

    private val viewModel: FarmerViewModel by viewModels() {
        FarmerViewModelFactory(
            application = activity?.application
                ?: throw Exception("Нету apllication для вьюмодели(фермер)")
        )
    }
    private var _binding: FragmentMyProductBinding? = null
    private val binding get() = _binding!!

    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyProductBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModels()

        viewModel.loadMyProducts()
    }

    private fun setupRecyclerView(){
        productAdapter = ProductAdapter(emptyList())
        binding.rvListProduct.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
        }
    }

    private fun observeViewModels(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Наблюдаем за списком продуктов
                launch {
                    viewModel.products.collect { products ->
                        productAdapter.updateData(products)
                    }
                }
                // Наблюдаем за состоянием загрузки
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        // Здесь можно показать/скрыть ProgressBar
                        // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                    }
                }
                // Наблюдаем за ошибками
                launch {
                    viewModel.error.collect { errorMessage ->
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyProductFragment().apply {}
    }
}