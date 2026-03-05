package com.example.farmer.farmer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.farmer.R
import com.example.farmer.customer.data.network.model.OrderHistoryResponse
import com.example.farmer.customer.data.network.model.OrderStatus
import com.example.farmer.databinding.ItemOrderPendingFarmerBinding
import com.example.farmer.databinding.ItemProfileOrderBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderAdapter(
    private val onClick: ((OrderHistoryResponse) -> Unit)? = null,
    private val onAcceptClick: (OrderHistoryResponse) -> Unit,
    private val onCancelClick: ((OrderHistoryResponse) -> Unit)? = null,
    private val onReorderClick: ((OrderHistoryResponse) -> Unit)? = null,
    private val onTrackClick: ((OrderHistoryResponse) -> Unit)? = null,
    private val onReviewClick: ((OrderHistoryResponse) -> Unit)? = null
) : ListAdapter<OrderHistoryResponse, RecyclerView.ViewHolder>(OrderDiffCallback) {

    companion object {
        private const val TYPE_PENDING = 0
        private const val TYPE_ACCEPTED = 1
        private const val TYPE_COMPLETED = 2
        private const val TYPE_CANCELLED = 3
        private const val TYPE_REJECTED = 4
    }

    private val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("ru"))

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).status) {
            OrderStatus.PENDING -> TYPE_PENDING
            OrderStatus.ACCEPTED -> TYPE_ACCEPTED
            OrderStatus.COMPLETED -> TYPE_COMPLETED
            OrderStatus.CANCELLED -> TYPE_CANCELLED
            OrderStatus.REJECTED -> TYPE_REJECTED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_PENDING -> PendingOrderViewHolder(
                ItemOrderPendingFarmerBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            TYPE_REJECTED -> RejectedOrderViewHolder(
                ItemProfileOrderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            else -> RejectedOrderViewHolder(
                ItemProfileOrderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val order = getItem(position)
        when (holder) {
            is PendingOrderViewHolder -> holder.bind(order)
            is RejectedOrderViewHolder -> holder.bind(order)
        }
    }


    inner class PendingOrderViewHolder(private val binding: ItemOrderPendingFarmerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderHistoryResponse) {
            binding.apply {
                // Основная информация
                tvOrderDate.text = dateFormat.format(Date(order.createdAt))
                tvOrderId.text = "Заказ #FA-${order.id}"
                tvProductSummary.text = order.products.joinToString(", ") { it.name }
                tvTotalPrice.text = "${order.totalAmount} ₽"

                // Статус
                tvStatusBadge.text = order.status.displayName
                tvStatusBadge.setTextColor(order.status.colorHex.toColorInt())
                tvStatusBadge.setBackgroundResource(R.drawable.bg_status_pending)

                // Кнопка отмены (только для ожидающих заказов)
                btnCancelOrder.setOnClickListener {
                    onCancelClick?.invoke(order)
                }
                btnAcceptOrder.setOnClickListener {
                    onAcceptClick.invoke(order)
                }

                // Обработка клика по всему элементу
                root.setOnClickListener { onClick?.invoke(order) }
            }
        }
    }


    inner class RejectedOrderViewHolder(private val binding: ItemProfileOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderHistoryResponse) {
            binding.apply {
                tvOrderDate.text = dateFormat.format(Date(order.createdAt))
                tvOrderId.text = "Заказ #FA-${order.id}"
                tvProductSummary.text = order.products.joinToString(", ") { it.name }
                tvTotalPrice.text = "${order.totalAmount} ₽"

                tvStatusBadge.text = order.status.displayName
                tvStatusBadge.setTextColor(order.status.colorHex.toColorInt())
                tvStatusBadge.setBackgroundResource(R.drawable.bg_status_rejected)
                root.setOnClickListener { onClick?.invoke(order) }
            }
        }
    }


    object OrderDiffCallback : DiffUtil.ItemCallback<OrderHistoryResponse>() {
        override fun areItemsTheSame(oldItem: OrderHistoryResponse, newItem: OrderHistoryResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: OrderHistoryResponse,
            newItem: OrderHistoryResponse
        ): Boolean {
            return oldItem == newItem
        }
    }
}