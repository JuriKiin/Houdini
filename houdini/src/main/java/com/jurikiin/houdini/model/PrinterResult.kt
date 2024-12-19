package com.jurikiin.houdini.model

sealed class PrinterResult {
    data object ValidPrinter : PrinterResult()
    data object InvalidPrinter : PrinterResult()
    data class Success(val printer: Printer) : PrinterResult()
    data class Error(val message: String) : PrinterResult()
}