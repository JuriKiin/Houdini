package com.jurikiin.houdini

import android.bluetooth.BluetoothManager
import android.content.Context
import android.graphics.BitmapFactory
import android.hardware.usb.UsbManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.jurikiin.houdini.communication.HoudiniCommunicationHandler
import com.jurikiin.houdini.model.CutType
import com.jurikiin.houdini.navigation.NavigationState
import com.jurikiin.houdini.ui.components.FindPrinters
import com.jurikiin.houdini.ui.components.Header
import com.jurikiin.houdini.ui.components.HoudiniButton
import com.jurikiin.houdini.ui.components.HoudiniDoubleButton
import com.jurikiin.houdini.ui.components.PrinterCard
import com.jurikiin.houdini.ui.components.PrinterList
import com.jurikiin.houdini.ui.model.ButtonState
import com.jurikiin.houdini.ui.theme.HoudiniTheme

class MainActivity : ComponentActivity(), HoudiniCommunicationHandler {
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModel.create(
            this,
            this@MainActivity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager,
            this@MainActivity.getSystemService(USB_SERVICE) as UsbManager
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeHoudini()

        setContent {
            val homeState by mainViewModel.state.observeAsState(MainViewModelState.Empty)
            val loading by mainViewModel.loadingState.observeAsState(false)
            var navigationState by remember { mutableStateOf<NavigationState>(NavigationState.Home) }

            HoudiniTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xFF0061ff),
                                        Color(0xFF60efff)
                                    )
                                )
                            ),
                    ) {
                        when (navigationState) {
                            is NavigationState.Home -> {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Header("Welcome to Houdini.")
                                    FindPrinters(loading) {
                                        mainViewModel.findPrinters()
                                    }
                                    when (homeState) {
                                        is MainViewModelState.Printers -> {
                                            val printers = (homeState as MainViewModelState.Printers).printers
                                            Text("Printers Found: ${printers.size}")
                                            PrinterList(printers) {
                                                navigationState = NavigationState.Action(it)
                                                mainViewModel.connectToPrinter(it)
                                            }
                                        }

                                        else -> {}
                                    }
                                }
                            }

                            is NavigationState.Action -> {
                                val printer = (navigationState as NavigationState.Action).printer

                                var input by remember { mutableStateOf("") }

                                Column(modifier = Modifier.padding(16.dp)) {
                                    PrinterCard(printer) {
                                        mainViewModel.disconnectFromPrinter(printer)
                                        navigationState = NavigationState.Home
                                    }
                                    TextField(
                                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                                        value = input,
                                        onValueChange = { input = it },
                                        label = { Text("Print Text") })
                                    Header("Actions")
                                    HoudiniButton(onClick = { mainViewModel.printText(printer, input) }, "Print")
                                    HoudiniButton(onClick = { mainViewModel.feed(printer, 1) }, "Line Feed")
                                    HoudiniButton(onClick = { mainViewModel.feed(printer, 10) }, "Full Feed")
                                    HoudiniDoubleButton(
                                        button1 = ButtonState("Partial Cut") { mainViewModel.cut(printer, CutType.PARTIAL) },
                                        button2 = ButtonState("Full Cut") { mainViewModel.cut(printer, CutType.FULL) }
                                    )
                                    HoudiniButton(onClick = {
                                        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.tophat)
                                        mainViewModel.printImage(printer, bitmap)
                                    }, "Print Image")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun requestPermissions(permissions: List<String>) {
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}.apply {
            launch(permissions.toTypedArray())
        }
    }

    override fun initializeHoudini() {
        mainViewModel.initialize()
    }
}