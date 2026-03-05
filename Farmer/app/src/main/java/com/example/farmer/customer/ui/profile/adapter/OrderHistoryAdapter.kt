package com.example.farmer.customer.ui.profile.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.farmer.customer.data.network.model.Order
import com.example.farmer.customer.data.network.model.OrderHistoryResponse
import com.example.farmer.databinding.ItemProfileOrderBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.graphics.toColorInt
import com.example.farmer.R
import com.example.farmer.customer.data.network.model.OrderStatus

class OrderHistoryAdapter(
    private val onClick: (OrderHistoryResponse) -> Unit
) : ListAdapter<OrderHistoryResponse, OrderHistoryAdapter.OrderViewHolder>(OrderDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemProfileOrderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderViewHolder(private val binding: ItemProfileOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderHistoryResponse) {
            binding.apply {
                // 1. Форматирование даты
                val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("ru"))
                tvOrderDate.text = sdf.format(Date(order.createdAt))

                // 2. ID заказа
                tvOrderId.text = "Заказ #FA-${order.id}"

                // 3. Статус (текст и цвет из твоего Enum)

                when(order.status){
                    OrderStatus.COMPLETED -> {
                        tvStatusBadge.text = order.status.displayName
                        tvStatusBadge.setTextColor(order.status.colorHex.toColorInt())
                        tvStatusBadge.setBackgroundResource(R.drawable.bg_status_completed)
                    }
                    else -> {
                        tvStatusBadge.text = order.status.displayName
                        tvStatusBadge.setTextColor(order.status.colorHex.toColorInt())
                        tvStatusBadge.setBackgroundResource(R.drawable.bg_status_pending)
                    }
                }

                // 4. Список товаров (названия через запятую)
                tvProductSummary.text = order.products.joinToString(", ") { it.name }

                // 5. Итоговая сумма
                tvTotalPrice.text = "${order.totalAmount} ₽"

                // 6. Обработка клика
                root.setOnClickListener { onClick(order) }
            }
        }
    }

    // Объект для эффективного обновления списка
    object OrderDiffCallback : DiffUtil.ItemCallback<OrderHistoryResponse>() {
        override fun areItemsTheSame(oldItem: OrderHistoryResponse, newItem: OrderHistoryResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: OrderHistoryResponse,
            newItem: OrderHistoryResponse
        ): Boolean {
            return oldItem==newItem
        }
    }
}