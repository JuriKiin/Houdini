package com.jurikiin.houdini.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.jurikiin.houdini.MainViewModel
import com.jurikiin.houdini.MainViewModelState
import com.jurikiin.houdini.navigation.NavigationState
import com.jurikiin.houdini.ui.theme.PrimaryBackground

@Composable
fun MainApp(
    homeState: MainViewModelState,
    loading: Boolean,
    navigationState: NavigationState,
    viewModel: MainViewModel,
    onNavigationStateChange: (NavigationState) -> Unit
) =
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.background(color = PrimaryBackground)) {
            when (navigationState) {
                is NavigationState.Home -> {
                    Home(
                        loading,
                        homeState,
                        onFindPrinters = { viewModel.findPrinters() },
                        onSelectPrinter = {
                            viewModel.connectToPrinter(it)
                            onNavigationStateChange(NavigationState.Action(it))
                        }
                    )
                }

                is NavigationState.Action -> {
                    PrinterDetails(
                        homeState,
                        navigationState.printer,
                        LocalContext.current.resources,
                        viewModel,
                        onDisconnect = {
                            viewModel.disconnectFromPrinter(it)
                            onNavigationStateChange(NavigationState.Home)
                        }
                    )
                }
            }
        }
    }