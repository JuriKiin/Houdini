package com.jurikiin.houdini.model

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.graphics.Bitmap
import android.util.Log
import com.jurikiin.houdini.actions.Actions
import com.jurikiin.houdini.actions.ImageRasterizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

@SuppressLint("MissingPermission")
class Printer(
    val name: String,
    val address: String,
    val connectionType: ConnectionType,
    private val configuration: PrinterConfiguration,
    private val imageRasterizer: ImageRasterizer,
    private val bluetoothManager: BluetoothManager?
) {
    private companion object {
        val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
    }

    private var socket: BluetoothSocket? = null
    private var device: BluetoothDevice? = null

    private var printerConfiguration = configuration

    suspend fun connect() {
        withContext(Dispatchers.IO) {
            Log.i("Houdini", "Connecting to printer $name")

            if (connectionType == ConnectionType.USB) return@withContext

            try {
                device = bluetoothManager?.adapter?.bondedDevices?.find {
                    it.address == address
                }

                socket?.let {
                    Log.i("Houdini", "Socket exists. Connecting...")
                    if (!it.isConnected) it.connect()
                    else Log.i("Houdini", "Socket is already connected")
                } ?: run {
                    Log.i("Houdini", "Creating new socket...")
                    socket = device?.createRfcommSocketToServiceRecord(SPP_UUID)
                    socket?.connect()
                    Log.i("Houdini", "Socket connected")
                }

                socket?.outputStream?.write(Actions.INIT)
                socket?.outputStream?.flush()
            } catch (e: Throwable) {
                println(e)
            }
        }
    }

    fun disconnect() {
        socket?.close()
        socket = null
    }

    fun setPaperSize(configuration: PrinterConfiguration) { printerConfiguration = configuration }

    suspend fun cut(type: CutType) {
        withContext(Dispatchers.IO) {
            try {
                socket?.outputStream?.write(type.toCommand())
                socket?.outputStream?.flush()
            } catch (e: Throwable) {
                println(e)
            }
        }
    }

    suspend fun feed(lines: Int = 1) {
        withContext(Dispatchers.IO) {
            try {
                socket?.outputStream?.write(Actions.FEED_LINES)
                socket?.outputStream?.write(byteArrayOf(lines.toByte()))
                socket?.outputStream?.flush()
            } catch (e: Throwable) {
                println(e)
            }
        }
    }

    suspend fun printImage(image: Bitmap) {
        withContext(Dispatchers.IO) {
            try {
                val rasterizedImage = imageRasterizer.rasterize(image, configuration)
                socket?.outputStream?.write(rasterizedImage)
                socket?.outputStream?.flush()
            } catch (e: Throwable) {
                println(e)
            }
        }
    }

    suspend fun printText(text: String) {
        withContext(Dispatchers.IO) {
            try {
                socket?.outputStream?.write(text.toByteArray(charset = Charsets.UTF_8))
                socket?.outputStream?.write(Actions.FEED_LINE)

                socket?.outputStream?.flush()
            } catch (e: Throwable) {
                println(e)
            }
        }
    }
}
