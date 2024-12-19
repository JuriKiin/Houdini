package com.jurikiin.houdini.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Header(title: String) {
    Text(
        text = title,
        fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
        fontSize = 24.sp,
        color = Color(0xFFF9FBB2),
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 16.dp, vertical = 32.dp),
        style = MaterialTheme.typography.headlineLarge,
        textAlign = TextAlign.Left
    )
}

@Composable
@Preview
fun Header_Preview() {
    Header("Welcome to Houdini.")
}