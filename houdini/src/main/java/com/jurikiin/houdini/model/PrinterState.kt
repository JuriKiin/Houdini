package com.jurikiin.houdini.model

enum class PrinterState {
    ONLINE,
    OFFLINE,
    ERROR;

    companion object {
        fun getState(statusByte: Byte, offlineStatusByte: Byte): PrinterState {
            val status = statusByte.toInt() and 0xFF
            return if (status and 0x08 != 0) {
                if (isError(offlineStatusByte)) ERROR else OFFLINE
            } else {
                ONLINE
            }
        }

        private fun isError(statusByte: Byte): Boolean {
            val status = statusByte.toInt() and 0xFF
            return status and 0x40 != 0
        }
    }
}