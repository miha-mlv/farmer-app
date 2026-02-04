package com.example.farmer.farmer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.farmer.R
import com.example.farmer.databinding.ItemProductSelectProfileBinding
import com.example.farmer.farmer.network.PointOfSale
import com.example.farmer.farmer.network.Product

private const val BASE_IMAGE_URL = "http://10.0.2.2:8080/images/"

class SelectedProductAdapter(private var products: List<Product>) :
    RecyclerView.Adapter<SelectedProductAdapter.SelectViewHolder>() {

    private val selectedProductsId = mutableSetOf<Long>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectViewHolder {
        val binding = ItemProductSelectProfileBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SelectViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SelectViewHolder,
        position: Int
    ) {
        val product = products[position]
        val isSelected = selectedProductsId.contains(product.id)
        with(holder.binding) {
            productName.text = product.name
            overlay.visibility = if(isSelected) View.VISIBLE else View.GONE
            checkIcon.visibility = if(isSelected) View.VISIBLE else View.GONE
            val imageUrl = product.images.firstOrNull()
            if (!imageUrl.isNullOrEmpty()) {
                val fullImageUrl = BASE_IMAGE_URL + imageUrl
                Glide.with(root.context)
                    .load(fullImageUrl)
                    .placeholder(R.drawable.ic_error)
                    .error(R.drawable.ic_error)
                    .into(productImage)
            } else {
                productImage.setImageResource(R.drawable.ic_error)
            }
        }
        holder.itemView.setOnClickListener {
            if(isSelected){
                selectedProductsId.remove(product.id)
            }else{
                selectedProductsId.add(product.id)
            }
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun getSelectedId(): List<Long>{
        return selectedProductsId.toList()
    }

    fun updateData(p0: List<Product>){
        products = p0
        notifyDataSetChanged()
    }

    inner class SelectViewHolder(val binding: ItemProductSelectProfileBinding) :
        RecyclerView.ViewHolder(binding.root)
}
