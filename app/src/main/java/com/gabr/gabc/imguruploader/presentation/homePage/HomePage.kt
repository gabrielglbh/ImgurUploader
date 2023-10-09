package com.gabr.gabc.imguruploader.presentation.homePage

import android.Manifest
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import coil.load
import com.gabr.gabc.imguruploader.R
import com.gabr.gabc.imguruploader.databinding.HomePageLayoutBinding
import com.gabr.gabc.imguruploader.domain.imageManager.models.Account
import com.gabr.gabc.imguruploader.domain.imageManager.models.ImgurImage
import com.gabr.gabc.imguruploader.presentation.homePage.components.UploadForm
import com.gabr.gabc.imguruploader.presentation.homePage.components.ImgurImageGalleryAdapter
import com.gabr.gabc.imguruploader.presentation.homePage.viewModel.HomeViewModel
import com.gabr.gabc.imguruploader.presentation.loginPage.LoginPage
import com.gabr.gabc.imguruploader.presentation.shared.PermissionsRequester
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomePage: AppCompatActivity() {
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var photoMedia: ActivityResultLauncher<Uri>
    private lateinit var requestMultiplePermissions: ActivityResultLauncher<Array<String>>

    private var addImageButtonsVisible = false

    private lateinit var binding: HomePageLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: HomeViewModel by viewModels()
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        onBackPressedDispatcher.addCallback(this, true) {
            if (!isLandscape && viewModel.hasImage.value == true) {
                binding.toolbarWidget.title = getString(R.string.app_name)
                viewModel.updateHasImage(null)
                with(supportFragmentManager.beginTransaction()) {
                    supportFragmentManager.findFragmentById(binding.uploadImageForm.id)
                        ?.let { remove(it) }
                    commit()
                }
            }
        }

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
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
        }

        binding = HomePageLayoutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (!isLandscape && viewModel.hasImage.value == true) {
            binding.toolbarWidget.title = getString(R.string.upload_image_title)
        } else {
            binding.toolbarWidget.title = getString(R.string.app_name)
        }

        val spanCount = if (isLandscape) { 3 } else { 4 }
        binding.imgurImages.layoutManager = StaggeredGridLayoutManager(spanCount, 1)
        setAdapterForRecyclerView(viewModel.images.value ?: listOf())

        initLiveDataObservables(viewModel)
        initFloatingActionButtons()
    }

    private fun createUploadImageForm(uri: Uri) {
        val viewModel: HomeViewModel by viewModels()
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        viewModel.updateHasImage(uri)
        if (!isLandscape) {
            binding.toolbarWidget.title = getString(R.string.upload_image_title)
        }

        val bundle = Bundle()
        bundle.putParcelable(UploadForm.PHOTO, uri)
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add<UploadForm>(binding.uploadImageForm.id, args = bundle)
        }

    }

    private fun initLiveDataObservables(viewModel: HomeViewModel) {
        viewModel.isLoading.observe(this) {
            binding.loadingLayout.loading.visibility = if (it) { View.VISIBLE } else { View.GONE }
        }
        viewModel.images.observe(this) {
            setAdapterForRecyclerView(it)
        }
        viewModel.hasImage.observe(this) {
            val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            binding.noPhotoSelected?.visibility = if (it) { View.GONE } else { View.VISIBLE }
            if (!isLandscape && !it) {
                binding.toolbarWidget.title = getString(R.string.app_name)
            }
        }
        viewModel.userData.observe(this) {
            binding.userAvatar.load(it.avatar) {
                error(R.drawable.broken_image)
            }
        }
    }

    private fun setAdapterForRecyclerView(images: List<ImgurImage>) {
        val viewModel: HomeViewModel by viewModels()
        val adapter = ImgurImageGalleryAdapter(images) {
            viewModel.updateHasImage(it.link)
            // TODO: Another fragment for view details (Only display data and option to remove)
        }
        binding.imgurImages.adapter = adapter
    }

    private fun initFloatingActionButtons() {
        binding.addFromGallery.setOnClickListener {
            hideFloatingActionButtons()
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
            hideFloatingActionButtons()
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
                addImageButtonsVisible = true
            } else {
                hideFloatingActionButtons()
            }
        }
    }

    private fun hideFloatingActionButtons() {
        binding.addFromGallery.hide()
        binding.addFromPhoto.hide()
        binding.addImage.shrink()
        addImageButtonsVisible = false
    }
}