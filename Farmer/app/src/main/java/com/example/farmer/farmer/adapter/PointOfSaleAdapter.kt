package com.example.farmer.farmer.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.farmer.databinding.ItemPointSaleBinding
import com.example.farmer.farmer.network.PointOfSale

class PointOfSaleAdapter(
    private var points: MutableList<PointOfSale>,
    private var listener: OnPointClickListener
) : RecyclerView.Adapter<PointOfSaleAdapter.PointViewHolder>() {

    inner class PointViewHolder(val binding: ItemPointSaleBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointViewHolder {
        val binding = ItemPointSaleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PointViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PointViewHolder, position: Int) {
        val point = points[position]
        with(holder.binding) {
            tvPointName.text = point.name
            tvPointAddress.text = point.address
            tvDeletePoint.setOnClickListener { listener.onDeleteClick(point, position) }
        }
    }

    override fun getItemCount() = points.size

    fun removeItem(position: Int) {
        points.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeRemoved(position, points.size)
    }

    fun updateData(newPoints: List<PointOfSale>) {
        this.points = newPoints.toMutableList()
        notifyDataSetChanged()
    }
}

interface OnPointClickListener {
    fun onDeleteClick(point: PointOfSale, position: Int)
}