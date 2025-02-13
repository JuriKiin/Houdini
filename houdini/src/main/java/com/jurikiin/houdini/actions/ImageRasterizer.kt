package com.jurikiin.houdini.actions

import android.graphics.Bitmap
import com.jurikiin.houdini.model.PrinterConfiguration

class ImageRasterizer {
    fun rasterize(image: Bitmap, printerConfiguration: PrinterConfiguration): ByteArray {
        val maxWidth = printerConfiguration.toWidthInPixels()
        val scaledImage = scaleImageToWidth(image, maxWidth)

        val bytesPerRow = (scaledImage.width + 7) / 8
        val imageData = convertToMonochrome(scaledImage, scaledImage.width, scaledImage.height, bytesPerRow)

        val commandSize = Actions.LEFT_MARGIN.size + Actions.PRINT_IMAGE.size + 4 + imageData.size
        val command = ByteArray(commandSize)
        System.arraycopy(Actions.LEFT_MARGIN, 0, command, 0, Actions.LEFT_MARGIN.size)
        System.arraycopy(Actions.PRINT_IMAGE, 0, command, Actions.LEFT_MARGIN.size, Actions.PRINT_IMAGE.size)

        val offset = Actions.LEFT_MARGIN.size + Actions.PRINT_IMAGE.size

        command[offset + 0] = (bytesPerRow and 0xFF).toByte()
        command[offset + 1] = ((bytesPerRow shr 8) and 0xFF).toByte()
        command[offset + 2] = (scaledImage.height and 0xFF).toByte()
        command[offset + 3] = ((scaledImage.height shr 8) and 0xFF).toByte()
        System.arraycopy(imageData, 0, command, offset + 4, imageData.size)

        return command
    }

    private fun convertToMonochrome(image: Bitmap, width: Int, height: Int, bytesPerRow: Int): ByteArray {
        val imageData = ByteArray(bytesPerRow * height)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = image.getPixel(x, y)
                val alpha = (pixel shr 24) and 0xFF
                if (alpha < 128) {
                    continue
                }
                val luminance = (0.299 * (pixel shr 16 and 0xFF) + 0.587 * (pixel shr 8 and 0xFF) + 0.114 * (pixel and 0xFF)).toInt()
                if (luminance < 128) {
                    imageData[y * bytesPerRow + (x / 8)] = (imageData[y * bytesPerRow + (x / 8)].toInt() or (0x80 shr (x % 8))).toByte()
                }
            }
        }

        return imageData
    }

    private fun scaleImageToWidth(image: Bitmap, width: Int): Bitmap =
        Bitmap.createScaledBitmap(
            image,
            width,
            (image.height * width / image.width),
            false
        )
}