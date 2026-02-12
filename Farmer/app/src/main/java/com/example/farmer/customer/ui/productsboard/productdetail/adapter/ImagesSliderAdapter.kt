package com.example.farmer.customer.ui.productsboard.productdetail.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.farmer.R
import com.example.farmer.databinding.ItemImageSliderBinding

private const val BASE_IMAGE_URL = "http://10.0.2.2:8080/images/"
class ImagesSliderAdapter(private val images: List<String>) :
    RecyclerView.Adapter<ImagesSliderAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageSliderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageName = images[position]
        val fillImageUrl = BASE_IMAGE_URL+imageName
        Log.d("onBindViewHolder", "ImagesSliderAdapter")

        Glide.with(holder.binding.root.context)
            .load(fillImageUrl)
            .placeholder(R.drawable.ic_error)
            .error(R.drawable.ic_error)
            .into(holder.binding.itemImageView)
    }

    override fun getItemCount() = images.size
    class ImageViewHolder(val binding: ItemImageSliderBinding) :
        RecyclerView.ViewHolder(binding.root)
}