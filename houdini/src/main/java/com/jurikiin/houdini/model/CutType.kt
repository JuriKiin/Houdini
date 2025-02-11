package com.jurikiin.houdini.model

import com.jurikiin.houdini.actions.Actions

enum class CutType {
    PARTIAL,
    FULL;

    fun toCommand(): ByteArray {
        return when (this) {
            PARTIAL -> Actions.CUT_BASE + byteArrayOf(0x00)
            FULL -> Actions.CUT_BASE + byteArrayOf(0x01)
        }
    }
}