package com.jurikiin.houdini.communication

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import com.jurikiin.houdini.actions.ImageRasterizer
import com.jurikiin.houdini.model.ConnectionType
import com.jurikiin.houdini.model.Printer
import java.util.UUID

@SuppressLint("MissingPermission")
class Houdini(
    private val explorer: HoudiniCommunicationHandler,
    private val bluetoothManager: BluetoothManager,
    private val usbManager: UsbManager,
    private val imageRasterizer: ImageRasterizer
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
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    fun findPrinters(): List<Printer> = mutableListOf<Printer>()
        .plus(findPrintersByConnectionType(ConnectionType.BLUETOOTH))
        .plus(findPrintersByConnectionType(ConnectionType.USB))

    private fun findPrintersByConnectionType(
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
                ConnectionType.USB,
                imageRasterizer,
                null
            )
        }
    }

    private fun getBluetoothDevices(): List<Printer> =
        bluetoothManager.adapter.bondedDevices
            .filter { it.uuids?.any { u -> u.uuid == SPP_UUID } ?: false }
            .map { printer ->
                Printer(
                    printer.alias.toString(),
                    printer.address,
                    ConnectionType.BLUETOOTH,
                    imageRasterizer,
                    bluetoothManager
                )
            }
}