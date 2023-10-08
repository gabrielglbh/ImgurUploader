package com.gabr.gabc.imguruploader.presentation.homePage.components

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.gabr.gabc.imguruploader.databinding.FragmentUploadFormBinding
import com.gabr.gabc.imguruploader.presentation.homePage.viewModel.HomeViewModel

class ImageDetails: Fragment() {
    companion object {
        const val PHOTO = "PHOTO"
    }

    private lateinit var binding: FragmentUploadFormBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = requireActivity().run { ViewModelProvider(this)[HomeViewModel::class.java] }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentUploadFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val photo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable(PHOTO, Uri::class.java)
        } else {
            requireArguments().getParcelable(PHOTO)
        }

        binding.imageToUpload.load(photo)

        if (savedInstanceState != null) {
            val form = viewModel.formState.value
            binding.imgurTitle.setText(form.title)
            binding.imgurDescription.setText(form.description)
        }
        binding.imgurTitle.doOnTextChanged { text, _, _, _ ->
            viewModel.updateForm(viewModel.formState.value.copy(title = text.toString()))
        }
        binding.imgurDescription.doOnTextChanged { text, _, _, _ ->
            viewModel.updateForm(viewModel.formState.value.copy(description = text.toString()))
        }
        binding.imgurTitle.setOnEditorActionListener { _, _, _ ->
            binding.imgurTitle.clearFocus()
            binding.imgurDescription.requestFocus()
            true
        }
        binding.imgurDescription.setOnEditorActionListener { _, _, _ ->
            binding.imgurDescription.clearFocus()
            onSubmit()
            true
        }

        binding.upload.setOnClickListener {
            onSubmit()
        }
    }

    private fun onSubmit() {
        viewModel.uploadImage(
            onSuccess = {
                with(requireActivity().supportFragmentManager.beginTransaction()) {
                    remove(this@ImageDetails)
                    commit()
                }
            },
            onError = {
                // TODO: Show snackbar on error
            }
        )
    }
}