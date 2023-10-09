package com.gabr.gabc.imguruploader.presentation.homePage.components

import android.app.Activity
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.load
import coil.size.Scale
import com.gabr.gabc.imguruploader.R
import com.gabr.gabc.imguruploader.databinding.FragmentUploadFormBinding
import com.gabr.gabc.imguruploader.presentation.homePage.viewModel.HomeViewModel
import com.google.android.material.snackbar.Snackbar

class UploadForm: Fragment() {
    companion object {
        const val PHOTO = "PHOTO"
    }

    private lateinit var binding: FragmentUploadFormBinding
    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentUploadFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val photo = arguments?.let { bundle ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(PHOTO, Uri::class.java)
            } else {
                bundle.getParcelable(PHOTO)
            }
        }

        binding.imageToUpload.load(photo) {
            error(R.drawable.broken_image)
            scale(Scale.FILL)
        }

        initForm(savedInstanceState)

        binding.upload.setOnClickListener {
            onSubmit()
        }
    }

    private fun initForm(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            val form = viewModel.formState.value
            binding.imgurTitle.setText(form.title)
            binding.imgurDescription.setText(form.description)
        }
        binding.imgurTitle.doOnTextChanged { text, _, _, _ ->
            viewModel.setForm(viewModel.formState.value.copy(title = text.toString()))
        }
        binding.imgurDescription.doOnTextChanged { text, _, _, _ ->
            viewModel.setForm(viewModel.formState.value.copy(description = text.toString()))
        }
        binding.imgurTitle.setOnEditorActionListener { _, _, _ ->
            binding.imgurTitle.clearFocus()
            binding.imgurDescription.requestFocus()
            true
        }
        binding.imgurDescription.setOnEditorActionListener { _, _, _ ->
            binding.imgurDescription.clearFocus()
            val inputMethodManager = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
            true
        }
    }

    private fun onSubmit() {
        viewModel.uploadImage(
            onSuccess = {
                with(requireActivity().supportFragmentManager.beginTransaction()) {
                    remove(this@UploadForm)
                    commit()
                }
            },
            onError = {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
            }
        )
    }
}