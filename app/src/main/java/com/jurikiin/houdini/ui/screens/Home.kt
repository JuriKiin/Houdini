package com.jurikiin.houdini.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.jurikiin.houdini.MainViewModelState
import com.jurikiin.houdini.model.Printer
import com.jurikiin.houdini.ui.components.FindPrinters
import com.jurikiin.houdini.ui.components.Header
import com.jurikiin.houdini.ui.components.PrinterList
import com.jurikiin.houdini.ui.theme.GradientEnd
import com.jurikiin.houdini.ui.theme.GradientStart
import com.jurikiin.houdini.ui.theme.PrimaryText

@Composable
fun Home(
    loading: Boolean,
    homeState: MainViewModelState,
    onFindPrinters: () -> Unit = {},
    onSelectPrinter: (Printer) -> Unit = {}
) = Column(verticalArrangement = Arrangement.SpaceBetween) {
    Header(modifier = Modifier.heightIn(max = 96.dp).welcome(), title = "Welcome to Houdini.")

    when (homeState) {
        is MainViewModelState.PrintersFound -> {
            Column(modifier = Modifier
                .weight(5f)
                .padding(8.dp)
            ) {
                Text(
                    modifier = Modifier.padding(8.dp, 4.dp, 8.dp, 0.dp),
                    color = PrimaryText,
                    text = "Printers Found: ${homeState.printers.size}"
                )
                PrinterList(homeState.printers) {
                    onSelectPrinter(it)
                }
            }
        }

        else -> { Spacer(modifier = Modifier.weight(5f)) }
    }

    FindPrinters(modifier = Modifier.heightIn(max = 128.dp).fillMaxWidth().padding(8.dp), loading) { onFindPrinters() }
}

private fun Modifier.welcome() = Modifier.then(
    fillMaxSize()
        .background(Brush.horizontalGradient(listOf(GradientStart, GradientEnd)))
)