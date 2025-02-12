package com.jurikiin.houdini.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.jurikiin.houdini.ui.model.ButtonState

@Composable
fun HoudiniDoubleButton(button1: ButtonState, button2: ButtonState) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
    ) {
        Box(modifier = Modifier.weight(1f)) {
            button1.Compose()
        }
        Box(modifier = Modifier.weight(1f)) {
            button2.Compose()
        }
    }
}