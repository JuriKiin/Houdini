package com.jurikiin.houdini.communication

interface HoudiniCommunicationHandler {
    fun requestPermissions(permissions: List<String>)
    fun initializeHoudini()
}