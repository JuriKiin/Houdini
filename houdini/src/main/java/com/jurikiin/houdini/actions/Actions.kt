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
    val PRINT_IMAGE = byteArrayOf(0x1D, 0x28, 0x4C, 0x02, 0x00, 0x30, 0x00)
}