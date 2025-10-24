package com.smartdocumentscanner.sdk.demo

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class ImagePagerAdapter(private val images: MutableList<Bitmap>) : 
    RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder>() {
    
    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_scanned_image, parent, false)
        return ImageViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.imageView.setImageBitmap(images[position])
    }
    
    override fun getItemCount(): Int = images.size
    
    fun addImage(bitmap: Bitmap) {
        images.add(bitmap)
        notifyItemInserted(images.size - 1)
        // Also notify the entire adapter to ensure proper refresh
        notifyDataSetChanged()
    }
    
    fun getCurrentImage(position: Int): Bitmap? {
        return if (position < images.size) images[position] else null
    }
    
    fun getTotalCount(): Int {
        return images.size
    }
}
