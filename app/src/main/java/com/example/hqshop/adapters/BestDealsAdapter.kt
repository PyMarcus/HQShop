package com.example.hqshop.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.hqshop.databinding.BestDealsRvItemBinding
import com.example.hqshop.models.ProductResult

class BestDealsAdapter: RecyclerView.Adapter<BestDealsAdapter.BestDealsViewHolder> (){
    private val diffCallback = object : DiffUtil.ItemCallback<ProductResult>(){
        override fun areItemsTheSame(oldItem: ProductResult, newItem: ProductResult): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductResult, newItem: ProductResult): Boolean {
            return oldItem.id == newItem.id
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealsViewHolder {
        return BestDealsViewHolder(
            BestDealsRvItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BestDealsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
        holder.itemView.setOnClickListener{
            onClick?.invoke(product)
        }
    }

    var onClick: ((ProductResult) -> Unit)? = null

    inner class BestDealsViewHolder(private val binding: BestDealsRvItemBinding):
        ViewHolder(binding.root){
        fun bind(product: ProductResult){
            binding.apply {
                tvDealProductName.text = product.name
                tvOldPrice.text = "R$ ${product.price.toString().replace(".", ",")}"
                product.offerPercentage?.let { price ->
                    tvNewPrice.text = "R$ ${
                        (product.price - (product.price * price)).toString().replace(".", ",")
                    }"
                }
                Glide.with(itemView).load(product.images).into(imgBestDeal)
            }
        }
    }
}