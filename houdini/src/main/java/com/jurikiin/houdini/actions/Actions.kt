package com.jurikiin.houdini.actions

object Actions {

    /**
     *  ESC @ - Initializes the printer
     */
    val INIT = byteArrayOf(0x1B, 0x40)

    /**
     *  FL - Feeds a line to the printer
     */
    val FEED_LINE = byteArrayOf(0x0A)

    /**
     * ESC d - Print and feed n lines
     */
    val FEED_LINES = byteArrayOf(0x1B, 0x64)

    /**
     * GS V - Cuts the paper
     */
    val CUT_BASE = byteArrayOf(0x1D, 0x56)

    /**
     * GS L - Print graphics data to the print buffer
     */
    val PRINT_IMAGE = byteArrayOf(0x1D, 0x76, 0x30, 0x00)

    /**
     * GS L 0 0 - Set the left margin to 0
     */
    val LEFT_MARGIN = byteArrayOf(0x1D, 0x4C, 0x00, 0x00)

    /**
     * DLE EOT n - Print the printer status
     */
    val PRINTER_STATUS = byteArrayOf(0x10, 0x04, 0x01)

    /**
     * DLE EOT 2 - Printer offline status
     */
    val PRINTER_OFFLINE_STATUS = byteArrayOf(0x10, 0x04, 0x02)

    /**
     * DLE EOT 3 - Printer error status
     */
    val PRINTER_ERROR_STATUS = byteArrayOf(0x10, 0x04, 0x03)
}