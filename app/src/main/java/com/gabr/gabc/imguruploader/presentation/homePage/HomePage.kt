package com.gabr.gabc.imguruploader.presentation.homePage

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.gabr.gabc.imguruploader.databinding.HomePageLayoutBinding
import com.gabr.gabc.imguruploader.domain.imageManager.models.Account
import com.gabr.gabc.imguruploader.presentation.homePage.components.ImageDetails
import com.gabr.gabc.imguruploader.presentation.homePage.components.ImgurImageGalleryAdapter
import com.gabr.gabc.imguruploader.presentation.homePage.viewModel.HomeViewModel
import com.gabr.gabc.imguruploader.presentation.loginPage.LoginPage
import com.gabr.gabc.imguruploader.presentation.shared.PermissionsRequester
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomePage: AppCompatActivity() {
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var photoMedia: ActivityResultLauncher<Uri>
    private lateinit var requestMultiplePermissions: ActivityResultLauncher<Array<String>>

    private var addImageButtonsVisible = false

    private lateinit var binding: HomePageLayoutBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = run { ViewModelProvider(this)[HomeViewModel::class.java] }
        val photoUri = PermissionsRequester.getPhotoUri(this)

        val account = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(LoginPage.ACCOUNT, Account::class.java)
        } else {
            intent.getParcelableExtra(LoginPage.ACCOUNT)
        }
        account?.let { viewModel.updateUserData(it) }

        pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    createUploadImageForm(uri)
                }
            }
        photoMedia = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                createUploadImageForm(photoUri)
            }
        }

        requestMultiplePermissions =
            PermissionsRequester.requestMultiplePermissionsCaller(
                this,
                pickMedia,
                photoMedia,
                photoUri
            )

        viewModel.loadImages { errorMessage ->
            // TODO: Show snackbar
        }

        binding = HomePageLayoutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.imgurImages.layoutManager = GridLayoutManager(this, 4)
        initLiveDataObservables()
        initFloatingActionButtons()
    }

    private fun createUploadImageForm(uri: Uri) {
        val bundle = Bundle()
        bundle.putParcelable(ImageDetails.PHOTO, uri)
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add<ImageDetails>(binding.uploadImageForm.id, args = bundle)
        }
    }

    private fun initLiveDataObservables() {
        viewModel.isLoading.observe(this) {
            binding.loadingLayout.loading.visibility = if (it) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
        viewModel.images.observe(this) {
            val adapter = ImgurImageGalleryAdapter(it)
            binding.imgurImages.adapter = adapter
        }
    }

    private fun initFloatingActionButtons() {
        binding.addFromGallery.setOnClickListener {
            requestMultiplePermissions.launch(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                } else {
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }
            )
        }
        binding.addFromPhoto.setOnClickListener {
            requestMultiplePermissions.launch(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.CAMERA
                    )
                } else {
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    )
                }
            )
        }

        binding.addImage.shrink()
        binding.addImage.setOnClickListener {
            if (!addImageButtonsVisible) {
                binding.addFromGallery.show()
                binding.addFromPhoto.show()
                binding.addImage.extend()
            } else {
                binding.addFromGallery.hide()
                binding.addFromPhoto.hide()
                binding.addImage.shrink()
            }
            addImageButtonsVisible = !addImageButtonsVisible
        }
    }
}