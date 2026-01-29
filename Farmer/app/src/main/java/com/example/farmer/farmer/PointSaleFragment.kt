package com.example.farmer.farmer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.farmer.R
import com.example.farmer.databinding.FragmentPoinSaleBinding
import kotlin.getValue


class PointSaleFragment : Fragment() {

    private val viewModel: FarmerViewModel by viewModels() {
        FarmerViewModelFactory(
            application = activity?.application
                ?: throw Exception("Нету apllication для вьюмодели(фермер)")
        )
    }
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
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PointSaleFragment().apply {

            }
    }
}