package com.jurikiin.houdini

import android.bluetooth.BluetoothManager
import android.content.Context
import android.hardware.usb.UsbManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.jurikiin.houdini.communication.HoudiniCommunicationHandler
import com.jurikiin.houdini.navigation.NavigationState
import com.jurikiin.houdini.ui.screens.MainApp
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

        setContent {
            val homeState by mainViewModel.state.observeAsState(MainViewModelState.Empty)
            val loading by mainViewModel.loadingState.observeAsState(false)
            var navigationState by remember { mutableStateOf<NavigationState>(NavigationState.Home) }

            HoudiniTheme {
                MainApp(homeState, loading, navigationState, mainViewModel) {
                    navigationState = it
                }
            }
        }
    }

    override fun requestPermissions(permissions: List<String>) {
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}.apply {
            launch(permissions.toTypedArray())
        }
    }
}