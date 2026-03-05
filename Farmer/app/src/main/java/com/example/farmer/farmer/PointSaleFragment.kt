package com.example.farmer.farmer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.manager.Lifecycle
import com.example.farmer.R
import com.example.farmer.databinding.FragmentPoinSaleBinding
import com.example.farmer.farmer.adapter.OnPointClickListener
import com.example.farmer.farmer.adapter.PointOfSaleAdapter
import com.example.farmer.farmer.network.PointOfSale
import com.example.farmer.farmer.network.RetrofitClientFarmer
import kotlinx.coroutines.launch
import kotlin.getValue


class PointSaleFragment : Fragment(), OnPointClickListener {

    private val viewModel: FarmerViewModel by viewModels() {
        FarmerViewModelFactory(
            mapOf(
                FarmerViewModel::class.java to {
                    FarmerViewModel(RetrofitClientFarmer.instance, requireActivity().application)
                }
            )
        )
    }
    private lateinit var adapter: PointOfSaleAdapter
    private var _binding: FragmentPoinSaleBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPoinSaleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnAddPoint.setOnClickListener {
            findNavController().navigate(R.id.action_pointSaleFragment_to_addPointSaleFragment)
        }
//        val pointsList = viewModel.getPoints()
        setupAdapter()
        observeViewModels()
        viewModel.getPoints()
    }

    private fun observeViewModels(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED){
                launch {
                    viewModel.pointOfSale.collect { pointOfSales ->
                        adapter.updateData(pointOfSales)
                    }
                }
            }
        }
    }

    private fun setupAdapter(){
        adapter = PointOfSaleAdapter(mutableListOf(), this)
        binding.rvPoints.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPoints.adapter = adapter

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onDeleteClick(
        point: PointOfSale,
        position: Int
    ) {
        AlertDialog.Builder(requireContext())
            .setTitle("Удаление")
            .setMessage("Вы точно хотите удалить точку ${point.name}?")
            .setPositiveButton("Да") { _, _ ->
                viewModel.deletePoint(point.id)
                adapter.removeItem(position)
            }
            .setNegativeButton("Нет", null)
            .show()
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PointSaleFragment().apply {

            }
    }
}