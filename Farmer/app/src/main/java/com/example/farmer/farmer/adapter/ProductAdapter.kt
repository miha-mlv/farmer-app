package com.example.farmer.farmer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.farmer.R
import com.example.farmer.databinding.ItemProductBinding
import com.example.farmer.farmer.network.Product

private const val BASE_IMAGE_URL = "http://10.0.2.2:8080/images/"

class ProductAdapter(private var products: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ProductViewHolder, position: Int
    ) {
        val product = products[position]
        with(holder.binding){
            tvProductName.text = product.name
            tvProductPrice.text = "${product.price} ₽"
            val imageUrl = product.images.firstOrNull()

            if (!imageUrl.isNullOrEmpty()) {
                val fullImageUrl = BASE_IMAGE_URL + imageUrl
                Glide.with(root.context) // root — это корневой макет карточки
                    .load(fullImageUrl)
                    .placeholder(R.drawable.ic_error)
                    .error(R.drawable.ic_error)
                    .into(ivProductImage)
            } else {
                ivProductImage.setImageResource(R.drawable.ic_error)
            }
        }
    }

    fun updateData(newProducts: List<Product>) {
        this.products = newProducts
        notifyDataSetChanged() // Уведомляем адаптер об изменении данных
    }

    override fun getItemCount(): Int {
        return products.size
    }

}
