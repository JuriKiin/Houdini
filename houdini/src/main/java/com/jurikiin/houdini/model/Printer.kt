package com.jurikiin.houdini.model

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import com.jurikiin.houdini.actions.Actions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

@SuppressLint("MissingPermission")
class Printer(
    val name: String,
    val address: String,
    val connectionType: ConnectionType,
    private val bluetoothManager: BluetoothManager?
) {

    private companion object {
        val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
    }

    private var socket: BluetoothSocket? = null
    private var device: BluetoothDevice? = null

    suspend fun connect() {
        withContext(Dispatchers.IO) {
            println("Connecting to printer $name")

            if (connectionType == ConnectionType.USB) return@withContext

            try {
                device = bluetoothManager?.adapter?.bondedDevices?.find {
                    it.address == address
                }

                socket?.let {
                    println("Socket exists. Connecting...")
                    if (!it.isConnected) it.connect()
                    else println("Socket is already connected.")
                } ?: run {
                    println("Socket does not exist. Creating...")
                    socket = device?.createRfcommSocketToServiceRecord(SPP_UUID)
                    socket?.connect()
                    println("Socket is connected: ${socket?.isConnected}")
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

    suspend fun printText(text: String = "Hello, World!") {
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
