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
     * GS V - Cuts the paper
     */
    val CUT_BASE = byteArrayOf(0x1D, 0x56)


    @OptIn(ExperimentalStdlibApi::class)
    fun feed(lines: Int): ByteArray {
        var bytes = byteArrayOf()

        repeat(lines) {
            bytes = bytes.plus(FEED_LINE)
        }

        println(bytes.toHexString(HexFormat.UpperCase))
        return bytes
    }
}