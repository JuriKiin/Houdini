package com.jurikiin.houdini.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jurikiin.houdini.R
import com.jurikiin.houdini.model.Printer

@Composable
fun PrinterList(printers: List<Printer>, onClick: (Printer) -> Unit) =
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
        printers.forEach {
            PrinterCard(it) { p -> onClick(p)}
        }
    }

@Composable
fun PrinterCard(printer: Printer, onClick: (Printer) -> Unit) =
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(printer) }
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(2f)) {
                PrinterItemText("Name: ${printer.name}")
                PrinterItemText("IP: ${printer.address}")
                PrinterItemText("Connection: ${printer.connectionType}")
            }
            Image(
                modifier = Modifier.weight(1f).size(64.dp),
                painter = painterResource(R.drawable.printer),
                contentDescription = "Printer",
                colorFilter = ColorFilter.tint(Color.White)
            )
        }

    }

@Composable
fun PrinterItemText(text: String) =
    Text(
        text = text,
        modifier = Modifier.padding(8.dp),
        textAlign = TextAlign.Start
    )