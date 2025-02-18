package com.jurikiin.houdini.model

enum class PrinterMessage {
    OK,
    OFFLINE,
    COVER_OPEN,
    NO_PAPER,
    RECOVERABLE_ERROR,
    UNRECOVERABLE_ERROR,
    AUTO_RECOVERABLE_ERROR,
    AUTO_CUTTER_ERROR,
    ERROR;

    companion object {
        fun getOfflineMessage(statusByte: Byte): PrinterMessage {
            val status = statusByte.toInt() and 0xFF
            return when {
                status and 0x04 != 0 -> COVER_OPEN
                status and 0x20 != 0 -> NO_PAPER
                status and 0x40 != 0 -> ERROR
                else -> OK
            }
        }

        fun getErrorMessage(statusByte: Byte): PrinterMessage {
            val status = statusByte.toInt() and 0xFF
            return when {
                status and 0x04 != 0 -> RECOVERABLE_ERROR
                status and 0x08 != 0 -> AUTO_CUTTER_ERROR
                status and 0x20 != 0 -> UNRECOVERABLE_ERROR
                status and 0x40 != 0 -> AUTO_RECOVERABLE_ERROR
                else -> OK
            }
        }
    }
}