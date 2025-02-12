package com.jurikiin.houdini.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jurikiin.houdini.ui.theme.PrimaryBackground
import com.jurikiin.houdini.ui.theme.PrimaryText

@Composable
fun FindPrinters(modifier: Modifier = Modifier, isLoading: Boolean = false, onClick: () -> Unit = {}) {
    HoudiniButton(modifier, onClick = onClick) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = PrimaryBackground)
        } else {
            Text("Search for Printers")
        }
    }
}