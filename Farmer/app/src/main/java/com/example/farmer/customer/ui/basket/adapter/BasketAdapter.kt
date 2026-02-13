package com.example.farmer.customer.ui.basket.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.farmer.R
import com.example.farmer.customer.data.local.entity.BasketItem
import com.example.farmer.databinding.ItemBasketProductBinding

private const val BASE_IMAGE_URL = "http://10.0.2.2:8080/images/"

class BasketAdapter(
    private val onPlus: (BasketItem) -> Unit,
    private val onMinus: (BasketItem) -> Unit,
    private val onDelete: (BasketItem) -> Unit,
) : ListAdapter<BasketItem, BasketAdapter.BasketViewHolder>(BasketDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BasketViewHolder {
        val binding =
            ItemBasketProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BasketViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: BasketViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class BasketViewHolder(private val binding: ItemBasketProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BasketItem) {
            with(binding) {
                tvProductName.text = item.name
                tvFarmName.text = "Ферма: ${item.farmName}"
                tvPrice.text = item.priceText
                tvQuantity.text = item.quantity.toString()

                val fillImageUrl = BASE_IMAGE_URL + item.imageUrl
                Glide.with(root.context)
                    .load(fillImageUrl)
                    .placeholder(R.drawable.ic_error)
                    .error(R.drawable.ic_error)
                    .into(productImage)

                btnPlus.setOnClickListener { onPlus(item) }
                btnMinus.setOnClickListener { onMinus(item) }
                btnRemove.setOnClickListener { onDelete(item) }
            }
        }
    }

    class BasketDiffCallback : DiffUtil.ItemCallback<BasketItem>() {
        override fun areItemsTheSame(
            oldItem: BasketItem,
            newItem: BasketItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: BasketItem,
            newItem: BasketItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}