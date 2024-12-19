package com.jurikiin.houdini.actions

object Actions {
    val INIT = byteArrayOf(0x1B, 0x40)
    val CUT_FULL = byteArrayOf(0x1D, 0x56, 0x00)
    val CUT_PARTIAL = byteArrayOf(0x1D, 0x56, 0x01)
    val FEED_LINE = byteArrayOf(0x0A)

    fun feed(lines: Int) = byteArrayOf(0x1B, 0x64, lines.toByte())
}