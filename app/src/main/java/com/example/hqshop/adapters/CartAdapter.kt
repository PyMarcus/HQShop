package com.example.hqshop.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hqshop.databinding.CartRvItemBinding
import com.example.hqshop.models.ProductResult

class CartAdapter: RecyclerView.Adapter<CartAdapter.CartProductsViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<ProductResult>(){
        override fun areItemsTheSame(oldItem: ProductResult, newItem: ProductResult): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductResult, newItem: ProductResult): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            CartProductsViewHolder {
        return CartProductsViewHolder(
            CartRvItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun onBindViewHolder(holder: CartProductsViewHolder, position: Int) {
        val product =  differ.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener{
            onClick?.invoke(product)
        }
    }

    var onClick: ((ProductResult) -> Unit)? = null

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class CartProductsViewHolder(private val binding: CartRvItemBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(product: ProductResult){
            binding.apply {
                Glide.with(itemView).load(product.images).into(imgAd)
                tvAdName.text = product.name
                tvAdPrice.text = "R$ ${product.price.toString().replace(".", ",")}"
            }
        }
    }
}