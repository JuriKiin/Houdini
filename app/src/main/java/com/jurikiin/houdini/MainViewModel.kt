package com.jurikiin.houdini

import android.bluetooth.BluetoothManager
import android.hardware.usb.UsbManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jurikiin.houdini.communication.Houdini
import com.jurikiin.houdini.communication.HoudiniCommunicationHandler
import com.jurikiin.houdini.model.CutType
import com.jurikiin.houdini.model.Printer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainViewModel(private val houdini: Houdini) : ViewModel() {

    private val _state = MutableLiveData<MainViewModelState>()
    val state: LiveData<MainViewModelState> = _state

    private val _loadingState = MutableLiveData(false)
    val loadingState: LiveData<Boolean> = _loadingState

    private var currentPrinter: Printer? = null

    fun initialize() {
        houdini.initialize()
    }

    fun findPrinters() {
        _loadingState.value = true

        CoroutineScope(Dispatchers.IO).launch {
            async {
                val result = houdini.findPrinters()
                _state.postValue(MainViewModelState.Printers(result))
            }.await()

            _loadingState.postValue(false)
        }
    }

    fun setCurrentPrinter(printer: Printer?) {
        currentPrinter = printer
    }

    // Printer Actions
    fun write(text: String) {
        CoroutineScope(Dispatchers.IO).launch {
            currentPrinter?.let {
                houdini.write(it, text)
            }
        }
    }

    fun feed(lines: Int = 1) {
        CoroutineScope(Dispatchers.IO).launch {
            currentPrinter?.let {
                houdini.feed(it, lines)
            }
        }
    }

    fun cut(cutType: CutType = CutType.FULL) {
        CoroutineScope(Dispatchers.IO).launch {
            currentPrinter?.let {
                houdini.cut(it, cutType)
            }
        }
    }

    companion object {
        fun create(
            houdiniCommunicationHandler: HoudiniCommunicationHandler,
            bluetoothManager: BluetoothManager,
            usbManager: UsbManager
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val houdini = Houdini(houdiniCommunicationHandler, bluetoothManager, usbManager)
                MainViewModel(houdini)
            }
        }
    }
}

sealed class MainViewModelState {
    data class Printers(val printers: List<Printer>) : MainViewModelState()
    data object Empty : MainViewModelState()
}