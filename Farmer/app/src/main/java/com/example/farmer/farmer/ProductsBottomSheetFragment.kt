package com.example.farmer.farmer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.farmer.R
import com.example.farmer.databinding.FragmentProductsBottomSheetBinding
import com.example.farmer.databinding.FragmentProfileBinding
import com.example.farmer.farmer.adapter.ProductAdapter
import com.example.farmer.farmer.adapter.SelectedProductAdapter
import com.example.farmer.farmer.network.RetrofitClientFarmer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import kotlin.getValue


class ProductsBottomSheetFragment : BottomSheetDialogFragment() {

    private val viewModel: FarmerViewModel by viewModels() {
        FarmerViewModelFactory(
            mapOf(
                FarmerViewModel::class.java to {
                    FarmerViewModel(RetrofitClientFarmer.instance, requireActivity().application)
                }
            )
        )
    }

    private var _binding: FragmentProductsBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val args: ProductsBottomSheetFragmentArgs by navArgs()

    private lateinit var adapterSelectProduct: SelectedProductAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductsBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val posId = args.posId
        val address = args.posName

        binding.tvPosName.text = "Адресс точки продажи: $address"
        viewModel.loadMyProducts()
        observerOnViewModel()
        setupRecyclerView()
        binding.btnStartSale.setOnClickListener {
            Log.d("ID product: ", "ID`s: ${adapterSelectProduct.getSelectedId()}}")
            viewModel.activatePOS(posId, adapterSelectProduct.getSelectedId(), true)
        }
    }

    private fun setupRecyclerView() {
        adapterSelectProduct = SelectedProductAdapter(emptyList())
        binding.rvProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = adapterSelectProduct
        }
    }

    private fun observerOnViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.products.collect { products ->
                        adapterSelectProduct.updateData(products)
                    }
                }
                launch {
                    viewModel.isSuccess.collect {
                        Toast.makeText(context, "Точка успешно активирована", Toast.LENGTH_LONG)
                            .show()
                        findNavController().popBackStack()
                    }
                }
                launch {
                    viewModel.error.collect { errorMessage ->
                        Toast.makeText(
                            context,
                            "Ошибка активации точки: $errorMessage",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProductsBottomSheetFragment()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}