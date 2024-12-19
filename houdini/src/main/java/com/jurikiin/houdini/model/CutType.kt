package com.jurikiin.houdini.model

enum class CutType {
    FULL,
    PARTIAL;

    fun toCommand(): ByteArray {
        return when (this) {
            FULL -> "GS V 0".toByteArray()
            PARTIAL -> "GS V 1".toByteArray()
        }
    }
}