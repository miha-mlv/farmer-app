package com.example.farmer.customer.ui.productsboard.adapter

import com.example.farmer.R
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.farmer.customer.data.network.model.Product
import com.example.farmer.customer.ui.productsboard.ProductsFragmentDirections
import com.example.farmer.databinding.ItemProductCustomerBinding

private const val BASE_IMAGE_URL = "http://10.0.2.2:8080/images/"

class ProductPagingAdapter(
    private val onProductClick: (Product) -> Unit,
    private val onAddBasketClick: (Product) -> Unit
) :
    PagingDataAdapter<Product, ProductPagingAdapter.ProductViewHolder>(diffCallback = DiffCallback) {

    class ProductViewHolder(private val binding: ItemProductCustomerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            product: Product,
            onProductClick: (Product) -> Unit,
            onAddBasketClick: (Product) -> Unit
        ) {
            binding.productName.text = product.name
            binding.productPrice.text = product.priceText
            binding.farmName.text = product.farmName
            binding.root.setOnClickListener { onProductClick(product) }
            binding.btnAddToBasket.setOnClickListener { onAddBasketClick(product) }

            val fullImageURL = BASE_IMAGE_URL + product.images
            if (!fullImageURL.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(fullImageURL)
                    .placeholder(R.drawable.ic_error)
                    .error(R.drawable.ic_error)
                    .into(binding.productImage)
            } else {
                binding.productImage.setImageResource(R.drawable.ic_error)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
        val binding = ItemProductCustomerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item, onProductClick, onAddBasketClick)
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Product, newItem: Product) = oldItem == newItem
    }
}

