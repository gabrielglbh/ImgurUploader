package com.gabr.gabc.imguruploader.presentation.homePage.components

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import com.gabr.gabc.imguruploader.R
import com.gabr.gabc.imguruploader.databinding.ComponentImgurImageBinding
import com.gabr.gabc.imguruploader.domain.imageManager.models.ImgurImage

class ImgurImageGalleryAdapter(private val images: List<ImgurImage>, private val onClick: (ImgurImage) -> Unit) :
    RecyclerView.Adapter<ImgurImageGalleryAdapter.ImgurImageViewHolder>() {

    inner class ImgurImageViewHolder(val binding: ComponentImgurImageBinding)
        :RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ImgurImageViewHolder {
        val binding = ComponentImgurImageBinding
            .inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ImgurImageViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ImgurImageViewHolder, position: Int) {
        with(viewHolder){
            with(images[position]) {
                binding.imgurImage.load(link) {
                    error(R.drawable.broken_image)
                    scale(Scale.FILL)
                }
                binding.imgurCard.setOnClickListener {
                    onClick(images[position])
                }
            }
        }
    }

    override fun getItemCount() = images.size

}
