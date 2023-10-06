package com.gabr.gabc.imguruploader.presentation.shared.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.gabr.gabc.imguruploader.R

@Composable
fun CustomDialog(
    onDismissRequest: (Boolean) -> Unit,
    @StringRes title: Int,
    content: @Composable () -> Unit,
    onSubmit: () -> Unit,
    @StringRes buttonTitle: Int = R.string.dialog_upload,
    onSubmitSecondary: (() -> Unit)? = null,
    @StringRes buttonSecondaryTitle: Int? = null,
) {
    Dialog(onDismissRequest = { onDismissRequest(false) }) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = stringResource(title),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    content()
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            onSubmit()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    ) {
                        Text(
                            stringResource(buttonTitle),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (onSubmitSecondary != null && buttonSecondaryTitle != null) Button(
                        onClick = {
                            onSubmitSecondary()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Text(
                            stringResource(buttonSecondaryTitle),
                            color = MaterialTheme.colorScheme.onTertiary
                        )
                    }
                }
            }
        }
    }
}