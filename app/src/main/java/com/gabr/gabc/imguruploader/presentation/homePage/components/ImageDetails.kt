package com.gabr.gabc.imguruploader.presentation.homePage.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gabr.gabc.imguruploader.R
import com.gabr.gabc.imguruploader.presentation.homePage.viewModel.HomeViewModel
import com.gabr.gabc.imguruploader.presentation.shared.Validators
import com.gabr.gabc.imguruploader.presentation.shared.components.TextForm

@Composable
fun ImageDetails(
    viewModel: HomeViewModel,
    onSubmit: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val configuration = LocalConfiguration.current
    val form = viewModel.formState.collectAsState().value

    val isPortrait = when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> { false }
        else -> { true }
    }

    var errorTitle by remember { mutableStateOf(false) }
    var errorDescription by remember { mutableStateOf(false) }

    val errorForm = stringResource(R.string.error_empty_form)

    fun onFormCompleted() {
        focusManager.clearFocus()

        if (errorTitle || errorDescription) {
            viewModel.updateForm(form.copy(error = errorForm))
        } else {
            onSubmit()
        }
    }

    val image: @Composable () -> Unit = {
        AsyncImage(model = form.link, contentDescription = "", modifier = Modifier.height(256.dp))
    }
    val title: @Composable (modifier: Modifier) -> Unit = { m ->
        TextForm(
            labelId = R.string.img_titulo,
            onValueChange = {
                viewModel.updateForm(form.copy(title = it, error = ""))
                errorTitle = Validators.isTitleInvalid(form.title)
            },
            value = form.title,
            imeAction = ImeAction.Next,
            isError = errorTitle,
            modifier = m
        )
    }
    val description: @Composable (modifier: Modifier) -> Unit = { m ->
        TextForm(
            labelId = R.string.img_description,
            onValueChange = {
                viewModel.updateForm(form.copy(description = it, error = ""))
                errorDescription = Validators.isDescriptionInvalid(form.description)
            },
            value = form.description,
            isError = errorDescription,
            onSubmitWithImeAction = { onFormCompleted() },
            modifier = m
        )
    }
    val errorText: @Composable () -> Unit = {
        Text(
            form.error,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        )
    }
    val button: @Composable () -> Unit = {
        Button(
            modifier = Modifier
                .padding(horizontal = 24.dp).run {
                    if (isPortrait) fillMaxWidth()
                    else this
                },
            onClick = { onFormCompleted() }
        ) {
            Text(
                stringResource(R.string.dialog_upload)
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isPortrait) Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            image()
            Spacer(modifier = Modifier.size(8.dp))
            title(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp))
            description(Modifier.fillMaxWidth())
            if (form.error.isNotEmpty()) errorText()
            Spacer(modifier = Modifier.size(12.dp))
            button()
        } else Row {
            image()
            Spacer(modifier = Modifier.size(8.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                title(Modifier.padding(bottom = 8.dp))
                description(Modifier)
                if (form.error.isNotEmpty()) errorText()
                Spacer(modifier = Modifier.size(12.dp))
                button()
            }
        }

    }
}