package com.jurikiin.houdini.model

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.graphics.Bitmap
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

    suspend fun feed(lines: Int = 1) {
        withContext(Dispatchers.IO) {
            try {
                socket?.outputStream?.write(Actions.feed(lines))
                socket?.outputStream?.flush()
            } catch (e: Throwable) {
                println(e)
            }
        }
    }

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

    suspend fun printImage(image: Bitmap) {
        withContext(Dispatchers.IO) {
            try {
                val width = image.width
                val height = image.height
                val bytesPerRow = (width + 7) / 8
                val imageData = ByteArray(bytesPerRow * height)

                for (y in 0 until height) {
                    for (x in 0 until width) {
                        val pixel = image.getPixel(x, y)
                        val luminance = (0.299 * (pixel shr 16 and 0xFF) + 0.587 * (pixel shr 8 and 0xFF) + 0.114 * (pixel and 0xFF)).toInt()
                        if (luminance < 128) {
                            imageData[y * bytesPerRow + (x / 8)] = (imageData[y * bytesPerRow + (x / 8)].toInt() or (0x80 shr (x % 8))).toByte()
                        }
                    }
                }

                val command = ByteArray(8 + imageData.size)
                command[0] = 0x1D
                command[1] = 0x76
                command[2] = 0x30
                command[3] = 0x00
                command[4] = (bytesPerRow and 0xFF).toByte()
                command[5] = ((bytesPerRow shr 8) and 0xFF).toByte()
                command[6] = (height and 0xFF).toByte()
                command[7] = ((height shr 8) and 0xFF).toByte()
                System.arraycopy(imageData, 0, command, 8, imageData.size)

                socket?.outputStream?.write(command)
                socket?.outputStream?.flush()
            } catch (e: Throwable) {
                println(e)
            }
        }
    }
}
