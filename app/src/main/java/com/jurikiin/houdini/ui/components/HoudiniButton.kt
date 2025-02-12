package com.jurikiin.houdini.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jurikiin.houdini.ui.theme.PrimaryBackground
import com.jurikiin.houdini.ui.theme.PrimaryText

@Composable
fun HoudiniButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) = Button(
        modifier = modifier
            .padding(8.dp)
            .heightIn(max = 48.dp)
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryText),
        onClick = onClick
) {
    content()
}

@Composable
fun HoudiniButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    title: String
) = HoudiniButton(modifier, onClick = { onClick() }) { Text(title, color = PrimaryBackground) }