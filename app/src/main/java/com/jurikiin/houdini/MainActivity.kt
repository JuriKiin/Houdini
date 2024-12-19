package com.jurikiin.houdini

import android.bluetooth.BluetoothManager
import android.content.Context
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
import com.jurikiin.houdini.communication.HoudiniCommunicationHandler
import com.jurikiin.houdini.model.CutType
import com.jurikiin.houdini.navigation.NavigationState
import com.jurikiin.houdini.ui.components.FindPrinters
import com.jurikiin.houdini.ui.components.Header
import com.jurikiin.houdini.ui.components.HoudiniButton
import com.jurikiin.houdini.ui.components.PrinterCard
import com.jurikiin.houdini.ui.components.PrinterList
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
                                            PrinterList((homeState as MainViewModelState.Printers).printers) {
                                                navigationState = NavigationState.Action(it)
                                            }
                                        }

                                        else -> {}
                                    }
                                }
                            }

                            is NavigationState.Action -> {
                                val printer = (navigationState as NavigationState.Action).printer
                                mainViewModel.setCurrentPrinter(printer)
                                var input by remember { mutableStateOf("") }

                                Column(modifier = Modifier.padding(16.dp)) {
                                    PrinterCard(printer) {
                                        navigationState = NavigationState.Home
                                        mainViewModel.setCurrentPrinter(null)
                                    }
                                    Header("Actions")
                                    TextField(
                                        modifier = Modifier.fillMaxWidth(),
                                        value = input,
                                        onValueChange = { input = it },
                                        label = { Text("Print Text") })
                                    HoudiniButton(onClick = { mainViewModel.write(input)}) { Text("Print") }
                                    HoudiniButton(onClick = { mainViewModel.feed(1)}) { Text("Feed") }
                                    HoudiniButton(onClick = { mainViewModel.cut(CutType.FULL)}) { Text("Full Cut") }
                                    HoudiniButton(onClick = { mainViewModel.cut(CutType.PARTIAL)}) { Text("Partial Cut") }
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