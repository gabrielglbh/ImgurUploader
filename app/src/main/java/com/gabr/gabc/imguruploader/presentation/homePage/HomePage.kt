package com.gabr.gabc.imguruploader.presentation.homePage

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gabr.gabc.imguruploader.R
import com.gabr.gabc.imguruploader.domain.imageManager.models.Account
import com.gabr.gabc.imguruploader.presentation.homePage.components.ImageDetails
import com.gabr.gabc.imguruploader.presentation.homePage.viewModel.HomeViewModel
import com.gabr.gabc.imguruploader.presentation.loginPage.LoginPage
import com.gabr.gabc.imguruploader.presentation.shared.PermissionsRequester
import com.gabr.gabc.imguruploader.presentation.shared.components.ComposeImage
import com.gabr.gabc.imguruploader.presentation.shared.components.LoadingScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomePage: ComponentActivity() {
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var photoMedia: ActivityResultLauncher<Uri>
    private lateinit var requestMultiplePermissions: ActivityResultLauncher<Array<String>>

    private val fadeInAnimTime = 500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: HomeViewModel by viewModels()
        val photoUri = PermissionsRequester.getPhotoUri(this)

        val account = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(LoginPage.ACCOUNT, Account::class.java)
        } else {
            intent.getParcelableExtra(LoginPage.ACCOUNT)
        }
        account?.let { viewModel.userData.value = it }

        pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    viewModel.updateShouldShowFormDialog(true, uri)
                }
            }
        photoMedia = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                viewModel.updateShouldShowFormDialog(true, photoUri)
            }
        }

        requestMultiplePermissions =
            PermissionsRequester.requestMultiplePermissionsCaller(
                this,
                pickMedia,
                photoMedia,
                photoUri
            )

        setContent {
            BackHandler {
                if (viewModel.shouldShowDetails.value) {
                    viewModel.updateShouldShowFormDialog(false)
                }
            }

            HomeView()
        }
    }

    @Composable
    fun HomeView() {
        val config = LocalConfiguration.current
        val viewModel: HomeViewModel by viewModels()
        val user = viewModel.userData.value
        val images = viewModel.images

        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(key1 = Unit, block = {
            viewModel.loadImages { errorMessage ->
                scope.launch {
                    snackbarHostState.showSnackbar(errorMessage)
                }
            }
        })

        Scaffold (
            topBar = {
                ActionBar()
            },
            floatingActionButton = {
                CustomFloatingActionButton()
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            }
        ) {
            Box(
                modifier = Modifier.padding(it)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface (
                        shape = CircleShape,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(bottom = 6.dp)
                    ) {
                        ComposeImage(uri = user.avatar)
                    }
                    Text(user.username, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.size(8.dp))
                    // TODO: Change UI when on landscape mode
                    LazyVerticalGrid(columns = GridCells.Fixed(4)) {
                        items(images) {
                            Column(horizontalAlignment = Alignment.Start, modifier = Modifier.padding(horizontal = 2.dp)) {
                                ComposeImage(
                                    uri = it.link,
                                    modifier = Modifier.clip(RoundedCornerShape(4.dp)).height((config.screenWidthDp / 4).dp)
                                )
                                Spacer(modifier = Modifier.size(4.dp))
                                it.title?.let { it1 -> Text(it1, style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )) }
                            }
                        }
                    }
                }
                if (viewModel.shouldShowDetails.value) ImageDetails(
                    viewModel = viewModel,
                    onSubmit = {
                        viewModel.uploadImage { errorMessage ->
                            scope.launch {
                                snackbarHostState.showSnackbar(errorMessage)
                            }
                        }
                    }
                )
                if (viewModel.isLoading.value) LoadingScreen()
            }
        }
    }

    @Composable
    fun ActionBar() {
        val viewModel: HomeViewModel by viewModels()

        Box(
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(
                    if (viewModel.shouldShowDetails.value) { R.string.dialog_upload_image } else { R.string.app_name }),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
        }
    }

    @Composable
    fun CustomFloatingActionButton() {
        var expanded by remember { mutableStateOf(false) }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
        ) {
            CustomFAB(expanded, Icons.Outlined.Image) {
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
                expanded = false
            }
            Spacer(modifier = Modifier.size(16.dp))
            CustomFAB(expanded, Icons.Outlined.CameraAlt) {
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
                expanded = false
            }
            Spacer(modifier = Modifier.size(16.dp))
            FloatingActionButton(
                modifier = Modifier.padding(4.dp),
                onClick = {
                    expanded = !expanded
                }
            ) {
                Icon(if (expanded) { Icons.Outlined.Clear } else { Icons.Outlined.Add }, "")
            }
        }
    }

    @Composable
    fun CustomFAB(visible: Boolean, icon: ImageVector, onClick: () -> Unit) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(fadeInAnimTime)),
            exit = fadeOut(tween(fadeInAnimTime))
        ) {
            FloatingActionButton(
                modifier = Modifier.padding(4.dp),
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 2.dp
                ),
                containerColor = if (icon == Icons.Outlined.CameraAlt) {
                    MaterialTheme.colorScheme.tertiaryContainer
                } else {
                    MaterialTheme.colorScheme.secondaryContainer
                },
                onClick = {
                    onClick()
                }
            ) {
                Icon(icon, "")
            }
        }
    }
}