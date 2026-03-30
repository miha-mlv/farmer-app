package com.example.farmer.customer.ui.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.farmer.R
import com.example.farmer.customer.data.network.model.OrderItemDto
import com.example.farmer.databinding.DetailOrderItemBinding

private const val BASE_IMAGE_URL = "http://10.0.2.2:8080/images/"

class OrderDetailAdapter : ListAdapter<OrderItemDto, OrderDetailAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = DetailOrderItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ProductViewHolder(private val binding: DetailOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: OrderItemDto) {
            with(binding) {
                tvProductName.text = product.name
                tvProductQuantity.text = "Количество: ${product.quantity}"
                tvProductPrice.text = "${product.price} ₽"

                // Загрузка изображения
                val fullImageUrl = BASE_IMAGE_URL + product.imageUrl
                Glide.with(ivProductImage.context)
                    .load(fullImageUrl)
                    .placeholder(R.drawable.ic_error) // Добавь заглушку
                    .error(R.drawable.ic_error)
                    .into(ivProductImage)
            }
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<OrderItemDto>() {
        override fun areItemsTheSame(oldItem: OrderItemDto, newItem: OrderItemDto): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: OrderItemDto, newItem: OrderItemDto): Boolean {
            return oldItem == newItem
        }
    }
}