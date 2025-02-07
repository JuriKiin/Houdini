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
}