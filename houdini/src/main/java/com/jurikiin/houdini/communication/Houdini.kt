package com.jurikiin.houdini.communication

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import com.jurikiin.houdini.actions.Actions
import com.jurikiin.houdini.model.ConnectionType
import com.jurikiin.houdini.model.CutType
import com.jurikiin.houdini.model.Printer
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.util.UUID

@SuppressLint("MissingPermission")
class Houdini(
    private val explorer: HoudiniCommunicationHandler,
    private val bluetoothManager: BluetoothManager,
    private val usbManager: UsbManager
) {

    private companion object {
        val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
    }

    fun initialize() {
        explorer.requestPermissions(
            listOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    suspend fun findPrinters(): List<Printer> {
        return coroutineScope {
            val deferredResults = listOf(
                async { findPrintersByConnectionType(ConnectionType.BLUETOOTH) },
                async { findPrintersByConnectionType(ConnectionType.USB) }
            )
            deferredResults.awaitAll().flatten()
        }
    }

    private suspend fun findPrintersByConnectionType(
        connectionType: ConnectionType,
    ): List<Printer> = when (connectionType) {
        ConnectionType.BLUETOOTH -> {
            getBluetoothDevices()
        }

        ConnectionType.USB -> {
            getUsbDevices()
        }
    }

    private fun getUsbDevices(): List<Printer> {
        val deviceList: HashMap<String, UsbDevice> = usbManager.deviceList

        return deviceList.map {
            Printer(
                it.key,
                it.value.deviceName,
                ConnectionType.USB
            )
        }
    }

    private suspend fun getBluetoothDevices(): List<Printer> {
        return withContext(Dispatchers.IO) {
            val devices = bluetoothManager.adapter.bondedDevices

            val validPrinters = mutableListOf<Printer>()
            val deferredValidations = mutableListOf<Deferred<Unit>>()

            devices.forEach { device ->
                deferredValidations.add(async {
                    validatePrinter(device) { validDevice ->
                        validPrinters.add(
                            Printer(
                                validDevice.alias.toString(),
                                validDevice.address,
                                ConnectionType.BLUETOOTH
                            )
                        )
                    }
                })
            }
            deferredValidations.awaitAll() // Wait for all validations
            validPrinters
        }
    }

    private suspend fun validatePrinter(
        device: BluetoothDevice,
        onValidated: (BluetoothDevice) -> Unit
    ) {
        return withContext(Dispatchers.IO) {
            var socket: BluetoothSocket? = null
            try {
                socket = device.createRfcommSocketToServiceRecord(device.uuids[0].uuid)
                socket.connect()

                val output = socket.outputStream

                output.write(Actions.INIT)

                val input = socket.inputStream
                val buffer = ByteArray(1024)
                withTimeout(50) {
                    input.read(buffer)
                }.apply {
                    if (this > 0) {
                        onValidated(device)
                    }
                }
            } catch (_: Exception) {
            } finally {
                socket?.close()
            }
        }
    }

    private fun getDeviceByAddress(address: String): BluetoothDevice? {
        return bluetoothManager.adapter.bondedDevices.find { it.address == address }
    }

    //Actions

    fun write(printer: Printer, text: String) {
        getDeviceByAddress(printer.address)?.let { device ->
            device.createRfcommSocketToServiceRecord(device.uuids[0].uuid).let { socket ->
                socket.connect()
                socket.outputStream.write(text.toByteArray())
                socket.close()
            }
        }
    }

    suspend fun feed(printer: Printer, lines: Int = 1) {
        getDeviceByAddress(printer.address)?.let { device ->
            device.createRfcommSocketToServiceRecord(SPP_UUID)
                .let { socket ->
                    try {
                        socket.connect()
                        socket.outputStream.write(Actions.INIT)
                        socket.outputStream.write(Actions.feed(lines))
                        socket.outputStream.flush()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        withTimeout(100) {
                            socket.close()
                        }
                    }
                }
        }
    }

    fun cut(printer: Printer, cutType: CutType) {
        getDeviceByAddress(printer.address)?.let { device ->
            device.createRfcommSocketToServiceRecord(device.uuids[0].uuid).let { socket ->
                socket.connect()
                when (cutType) {
                    CutType.FULL -> {
                        socket.outputStream.write(Actions.CUT_FULL)
                    }

                    CutType.PARTIAL -> {
                        socket.outputStream.write(Actions.CUT_PARTIAL)
                    }
                }
                socket.close()
            }
        }
    }
}