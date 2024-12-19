package com.jurikiin.houdini.navigation

import com.jurikiin.houdini.model.Printer

sealed class NavigationState {
    data object Home : NavigationState()
    data class Action(val printer: Printer) : NavigationState()
}