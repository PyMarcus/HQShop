package com.example.hqshop.adapters

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.hqshop.databinding.ViewpageImageItemBinding

class ViewPager2ImagesAdapter: RecyclerView.Adapter<ViewPager2ImagesAdapter.ViewPagerViewHolder> (){

    private val diffCallback = object : DiffUtil.ItemCallback<Bitmap>(){
        override fun areItemsTheSame(oldItem: Bitmap, newItem: Bitmap): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Bitmap, newItem: Bitmap): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        return ViewPagerViewHolder(
            ViewpageImageItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        val image = differ.currentList[position]
        holder.bind(image)
    }

    inner class ViewPagerViewHolder(val binding: ViewpageImageItemBinding)
        :ViewHolder(binding.root){

            fun bind(imageB64: Bitmap){
                Glide.with(itemView).load(imageB64).into(binding.productImageDetail)
            }
    }
}