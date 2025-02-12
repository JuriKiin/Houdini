package com.jurikiin.houdini

import android.bluetooth.BluetoothManager
import android.graphics.Bitmap
import android.hardware.usb.UsbManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jurikiin.houdini.actions.ImageRasterizer
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

    fun initialize() {
        houdini.initialize()
    }

    fun findPrinters() {
        _loadingState.value = true

        CoroutineScope(Dispatchers.IO).launch {
            async {
                houdini.findPrinters().also {
                    _state.postValue(MainViewModelState.PrintersFound(it))
                }
            }.await()

            _loadingState.postValue(false)
        }
    }

    fun connectToPrinter(printer: Printer) = CoroutineScope(Dispatchers.IO).launch {
        printer.connect()
    }

    fun disconnectFromPrinter(printer: Printer) = printer.disconnect()

    fun printText(printer: Printer, text: String) = CoroutineScope(Dispatchers.IO).launch {
        printer.printText(text)
    }

    fun feed(printer: Printer, lines: Int) = CoroutineScope(Dispatchers.IO).launch {
        printer.feed(lines)
    }

    fun cut(printer: Printer, type: CutType) = CoroutineScope(Dispatchers.IO).launch {
        printer.cut(type)
    }

    fun printImage(printer: Printer, image: Bitmap) = CoroutineScope(Dispatchers.IO).launch {
        printer.printImage(image)
    }

    companion object {
        fun create(
            houdiniCommunicationHandler: HoudiniCommunicationHandler,
            bluetoothManager: BluetoothManager,
            usbManager: UsbManager
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val houdini = Houdini(
                    houdiniCommunicationHandler,
                    bluetoothManager,
                    usbManager,
                    ImageRasterizer()
                )
                MainViewModel(houdini)
            }
        }
    }
}

sealed class MainViewModelState {
    data class PrintersFound(val printers: List<Printer>) : MainViewModelState()
    data object Empty : MainViewModelState()
}