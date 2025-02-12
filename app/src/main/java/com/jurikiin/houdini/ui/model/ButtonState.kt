package com.jurikiin.houdini.ui.model

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.jurikiin.houdini.ui.components.HoudiniButton

data class ButtonState(
    val text: String,
    val onClick: () -> Unit
) {
    @Composable
    fun Compose() {
        HoudiniButton(onClick = { onClick() }) {
            Text(text)
        }
    }
}
