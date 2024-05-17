package com.example.hqshop.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hqshop.databinding.CartRvItemBinding
import com.example.hqshop.models.CartModel
import com.example.hqshop.models.ProductResult

class CartAdapter: RecyclerView.Adapter<CartAdapter.CartProductsViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<CartModel>(){
        override fun areItemsTheSame(oldItem: CartModel, newItem: CartModel): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartModel, newItem: CartModel): Boolean {
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
            onClick?.invoke(product.product)
        }

        holder.binding.plus.setOnClickListener{
            onPlusClick?.invoke(product.product)
        }
        holder.binding.minus.setOnClickListener{
            onMinusClick?.invoke(product.product)
        }
    }

    var onClick: ((ProductResult) -> Unit)? = null
    var onPlusClick: ((ProductResult) -> Unit)? = null
    var onMinusClick: ((ProductResult) -> Unit)? = null

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class CartProductsViewHolder(val binding: CartRvItemBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(cart: CartModel){
            binding.apply {
                Glide.with(itemView).load(cart.product.images).into(imgAd)
                tvAdName.text = cart.product.name
                quantity.text = cart.quantity.toString()
                cart.product.offerPercentage?.let { price ->
                    tvAdPrice.text = "R$ ${
                        (cart.product.price - (cart.product.price * price)).toString().replace(".", ",")
                    }"
                }?:run{
                    tvAdPrice.text = "R$ ${cart.product.price.toString().replace(".", ",")}"
                }
            }
        }
    }
}