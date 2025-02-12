package com.jurikiin.houdini.actions

import android.graphics.Bitmap
import com.jurikiin.houdini.model.PrinterConfiguration

class ImageRasterizer {
    fun rasterize(image: Bitmap, printerConfiguration: PrinterConfiguration): ByteArray {
        try {
            val scaledImage = scaleImageToWidth(image, printerConfiguration.toWidthInPixels())

            val bytesPerRow = (image.width + 7) / 8
            val imageData = convertImageToMonochrome(scaledImage, bytesPerRow)

            val command = ByteArray(8 + imageData.size)

            command[0] = 0x1D
            command[1] = 0x76
            command[2] = 0x30
            command[3] = 0x00
            command[4] = (bytesPerRow and 0xFF).toByte()
            command[5] = ((bytesPerRow shr 8) and 0xFF).toByte()
            command[6] = (image.height and 0xFF).toByte()
            command[7] = ((image.height shr 8) and 0xFF).toByte()

            System.arraycopy(imageData, 0, command, 8, imageData.size)
            return imageData
        } catch (e: Throwable) {
            println(e)
            return byteArrayOf()
        }
    }

    private fun convertImageToMonochrome(image: Bitmap, bytesPerRow: Int): ByteArray {
        val imageData = ByteArray(bytesPerRow * image.height)
        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                val pixel = image.getPixel(x, y)
                val alpha = (pixel shr 24 and 0xFF)
                if (alpha < 128) {
                    continue
                }
                val luminance =
                    (0.299 * (pixel shr 16 and 0xFF) + 0.587 * (pixel shr 8 and 0xFF) + 0.114 * (pixel and 0xFF)).toInt()
                if (luminance < 128) {
                    imageData[y * bytesPerRow + (x / 8)] =
                        (imageData[y * bytesPerRow + (x / 8)].toInt() or (0x80 shr (x % 8))).toByte()
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