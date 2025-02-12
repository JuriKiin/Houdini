package com.jurikiin.houdini.model

/**
 * This data class represents the configuration of a printer.
 *
 * Each printer can have a unique DPI, which will change how images get printed.
 * Similarly, many printers allow the user to use several different widths of paper. In order to
 * calculate the correct scaling for the image, the width of the paper in millimeters is required.
 *
 * TODO: Figure out a way to detect this, or set this, or perhaps this is a standard that most printers follow?
 */
data class PrinterConfiguration(
    val dpi: Int,
    val paperWidthInMM: Int
) {
    companion object {
        val DEFAULT = PrinterConfiguration(203, 80)
    }

    fun toWidthInPixels() = (paperWidthInMM * dpi / 25.4).toInt()
}
