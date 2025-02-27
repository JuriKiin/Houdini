package com.jurikiin.houdini.ui.screens

import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.jurikiin.houdini.MainViewModel
import com.jurikiin.houdini.MainViewModelState
import com.jurikiin.houdini.R
import com.jurikiin.houdini.model.CutType
import com.jurikiin.houdini.model.Printer
import com.jurikiin.houdini.model.PrinterConfiguration
import com.jurikiin.houdini.ui.components.Header
import com.jurikiin.houdini.ui.components.HoudiniButton
import com.jurikiin.houdini.ui.components.HoudiniDoubleButton
import com.jurikiin.houdini.ui.model.ButtonState

@Composable
fun PrinterDetails(
    viewModelState: MainViewModelState,
    printer: Printer,
    resources: Resources,
    viewModel: MainViewModel,
    onDisconnect: (Printer) -> Unit = {}
) {
    var input by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(10f, fill = false),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier.size(192.dp).padding(16.dp),
                painter = painterResource(R.drawable.printer_ill),
                contentDescription = "Printer Illustration"
            )

            Text(printer.name)
            Text(printer.address)
            Text("Connection: ${printer.connectionType}")

            if (viewModelState is MainViewModelState.PrinterResult) {
                Text("Printer Status: ${viewModelState.result.state} : ${viewModelState.result.message}")
            } else {
                Text("Printer Status: -- : --")
            }

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                value = input,
                onValueChange = { input = it },
                label = { Text("Write something here") })

            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Header(title = "Select your paper size")
                HoudiniDoubleButton(
                    button1 = ButtonState("57mm") { viewModel.setPaperSize(printer, PrinterConfiguration.MM_57) },
                    button2 = ButtonState("80mm") { viewModel.setPaperSize(printer, PrinterConfiguration.MM_80) }
                )

                HoudiniButton(onClick = { viewModel.printText(printer, input) }, title = "Write Text")
                HoudiniButton(onClick = { viewModel.feed(printer, 1) }, title = "Line Feed")
                HoudiniButton(onClick = { viewModel.feed(printer, 10) }, title = "Full Feed")
                HoudiniDoubleButton(
                    button1 = ButtonState("Partial Cut") { viewModel.cut(printer, CutType.PARTIAL) },
                    button2 = ButtonState("Full Cut") { viewModel.cut(printer, CutType.FULL) }
                )
                HoudiniButton(
                    onClick = {
                        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.printer_ill)
                        viewModel.printImage(printer, bitmap)
                    },
                    title = "Print Image"
                )
                HoudiniButton(
                    onClick = { viewModel.getStatus(printer) },
                    title = "Printer Status"
                )
            }
        }


        HoudiniButton(
            modifier = Modifier.weight(1f, fill = false),
            onClick = { onDisconnect(printer) },
            title = "Disconnect"
        )
    }
}