package com.jurikiin.houdini.model

import android.bluetooth.BluetoothSocket
import com.jurikiin.houdini.actions.Actions

data class PrinterStatus(
    val state: PrinterState,
    val message: PrinterMessage = PrinterMessage.OK
) {
    companion object {
        fun fromStatus(socket: BluetoothSocket): PrinterStatus {
            val genericStatus = getStatus(socket, Actions.PRINTER_STATUS)
            val offlineStatus = getStatus(socket, Actions.PRINTER_OFFLINE_STATUS)

            val status = PrinterState.getState(genericStatus, offlineStatus)

            return when (status) {
                PrinterState.ONLINE -> PrinterStatus(PrinterState.ONLINE)
                PrinterState.OFFLINE -> PrinterStatus(PrinterState.OFFLINE, PrinterMessage.getOfflineMessage(offlineStatus))
                PrinterState.ERROR -> {
                    val errorStatus = getStatus(socket, Actions.PRINTER_ERROR_STATUS)
                    PrinterStatus(PrinterState.ERROR, PrinterMessage.getErrorMessage(errorStatus))
                }
            }
        }

        private fun getStatus(socket: BluetoothSocket, status: ByteArray): Byte {
            socket.outputStream?.write(status)
            socket.outputStream?.flush()
            val statusByte = ByteArray(1)
            socket.inputStream?.read(statusByte)
            return statusByte[0]
        }
    }
}