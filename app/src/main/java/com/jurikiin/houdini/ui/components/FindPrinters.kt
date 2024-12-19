package com.jurikiin.houdini.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FindPrinters(isLoading: Boolean = false, onClick: () -> Unit = {}) {
    HoudiniButton(onClick = onClick) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFFF9FBB2))
        } else {
            Text("Search for Printers", color = Color(0xFFF9FBB2))
        }
    }
}

@Composable
fun HoudiniButton(onClick: () -> Unit = {}, content: @Composable () -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .heightIn(max = 48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF30A8FF)),
        onClick = onClick
    ) {
        content()
    }
}