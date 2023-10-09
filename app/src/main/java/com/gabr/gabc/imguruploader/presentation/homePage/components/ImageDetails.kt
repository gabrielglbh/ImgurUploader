package com.gabr.gabc.imguruploader.presentation.homePage.components

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.load
import com.gabr.gabc.imguruploader.R
import com.gabr.gabc.imguruploader.databinding.FragmentImageDetailsBinding
import com.gabr.gabc.imguruploader.domain.imageManager.models.ImgurImage
import com.gabr.gabc.imguruploader.presentation.homePage.viewModel.HomeViewModel
import com.google.android.material.snackbar.Snackbar

class ImageDetails : Fragment() {
    companion object {
        const val IMGUR_IMAGE = "IMGUR_IMAGE"
    }

    private lateinit var binding: FragmentImageDetailsBinding
    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentImageDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val image = arguments?.let { bundle ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(IMGUR_IMAGE, ImgurImage::class.java)
            } else {
                bundle.getParcelable(IMGUR_IMAGE)
            }
        }

        image?.let { img ->
            binding.detailsImage.load(img.link) {
                error(R.drawable.broken_image)
            }
            binding.detailsTitle.text = img.title
            binding.detailsDescription.text = img.description

            if (img.title.isNullOrEmpty()) binding.detailsTitle.visibility = View.GONE
            if (img.description.isNullOrEmpty()) binding.detailsDescription.visibility = View.GONE

            binding.delete.visibility = View.VISIBLE
            binding.delete.setOnClickListener {
                viewModel.deleteImage(
                    img.deleteHash,
                    onSuccess = {
                        with(requireActivity().supportFragmentManager.beginTransaction()) {
                            remove(this@ImageDetails)
                            commit()
                        }
                    },
                    onError = {
                        Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}