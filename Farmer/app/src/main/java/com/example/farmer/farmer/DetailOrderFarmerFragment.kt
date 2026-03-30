package com.example.farmer.farmer

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.farmer.R
import com.example.farmer.customer.data.network.model.OrderHistoryResponse
import com.example.farmer.customer.data.network.model.OrderStatus
import com.example.farmer.databinding.FragmentDetailOrderFarmerBinding
import com.example.farmer.farmer.adapter.OrderDetailAdapter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.graphics.toColorInt
import androidx.core.os.bundleOf

class DetailOrderFarmerFragment : Fragment() {

    private var _binding: FragmentDetailOrderFarmerBinding? = null
    private val binding get() = _binding!!

    private val adapter = OrderDetailAdapter()

    private val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("ru"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailOrderFarmerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.rvProducts.adapter = adapter

        val order = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("SELECTED_ORDER", OrderHistoryResponse::class.java)
        } else {
            arguments?.getParcelable("SELECTED_ORDER")
        }

        order?.let { data ->
            setupUI(data)
        }
        binding.btnChat.setOnClickListener {
            val bundle = bundleOf(
                "FARM_NAME" to ("Покупатель"),
                "SENDER_ID" to (order?.farmerId ?: 0L),
                "ORDER_ID" to (order?.id ?: 0L),
                "RECEIVER_ID" to (order?.customerId ?: 0L)
            ) // передать имя покупателя
            findNavController().navigate(
                R.id.action_detailOrderFarmerFragment_to_chatFragment,
                bundle
            )
            //Переход к чату
            Toast.makeText(requireActivity(), "Переход к чату", Toast.LENGTH_SHORT).show()
        }

    }

    private fun setupUI(order: OrderHistoryResponse) {
        with(binding) {
            toolbar.title = "ЗАКАЗ #${order.id}"

            when (order.status) {
                OrderStatus.REJECTED -> {
                    cardStatus.setCardBackgroundColor("#fdf3f2".toColorInt())
                    tvStatusDate.setTextColor("#F44336".toColorInt())
                    tvStatusDate.text = "Создан: ${dateFormat.format(Date(order.createdAt))}"
                    tvStatusTitle.setTextColor("#F44336".toColorInt())
                    tvCancelTime.text = dateFormat.format(Date(order.rejectedAt!!))
                    tvCancelReason.text = "Статус: ${order.rejectionReason ?: "Причина не указана"}"
                    tvCancelComment.text = order.rejectionComment
                    layoutCancellationDetails.visibility = View.VISIBLE
                }

                OrderStatus.ACCEPTED -> {
                    cardStatus.setCardBackgroundColor("#f0fdf4".toColorInt())
                    tvStatusDate.setTextColor("#4CAF50".toColorInt())
                    tvStatusTitle.setTextColor("#4CAF50".toColorInt())
                    tvStatusDate.text = "Создан: ${dateFormat.format(Date(order.createdAt))}"
                    tvStatusTitle.text = "Статус: ЗАКАЗ ПРИНЯТ"
                    layoutCancellationDetails.visibility = View.GONE
                }

                else -> {
                    cardStatus.setCardBackgroundColor("#F9E8CC".toColorInt())
                    tvStatusDate.setTextColor("#FFA500".toColorInt())
                    tvStatusTitle.setTextColor("#FFA500".toColorInt())
                    layoutCancellationDetails.visibility = View.GONE
                }
            }

            // Передаем продукты в адаптер
            adapter.submitList(order.products)

            // Суммы
            tvTotalAmount.text = "${order.totalAmount} ₽"
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DetailOrderFarmerFragment().apply {
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}