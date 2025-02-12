# Houdini

## Overview

Houdini is an Android library designed to interact with thermal printers on the `ESC/POS` protocol. This library supports both `USB` and `Bluetooth` connections, and provides functionality to:

- Write Text
- Print Images
- Feed Paper
- Cut Paper

Houdini also assumes responsibility for managing Bluetooth & connection permissions.

## Features

- **Permission Handling**: Request and handle necessary permissions for printer discovery and connection.
- **Printer Management**: Discover and manage multiple printers.
- **Connection Types**: Supports Bluetooth and USB connections.
- **Print Text**: Send text to the printer for printing.
- **Print Images**: Rasterize and print images with support for DPI and paper width in millimeters.
- **Feed Paper**: Feed paper by a specified number of lines.
- **Cut Paper**: Perform partial or full cuts of the paper.

## Getting Started

1. Implenent `HoudiniCommunicationHandler` in your Activity
```
class MainActivity : ComponentActivity(), HoudiniCommunicationHandler {

...

    override fun requestPermissions(permissions: List<String>) {
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}.apply {
            launch(permissions.toTypedArray())
        }
    }

    override fun initializeHoudini() {
        //Initialize Houdini -- preferably in a viewModel / ViewModelFactory
    }
```
2. Initialize `Houdini`
```
val houdini = Houdini(
    this,
    this@MainActivity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager,
    this@MainActivity.getSystemService(USB_SERVICE) as UsbManager
    ImageRasterizer()
)
```
3. Find Printers
```
houdini.findPrinters(): List<Printer>
```

4. Printer Communication
```
1. Connect to the printer
printer.connect() //This must be done before sending commands

2. Send commands
printer.printText("Hello, World!")

3. Cut the receipt paper
printer.cut(CutType.FULL)

4. Disconnect from the printer
printer.disconnect()
```

5. Run the `sample` app to view the Demo

## Screenshots
<img src="https://github.com/user-attachments/assets/f623c31c-0f6b-4ceb-805a-8cdb11c35add" width="400" />
<img src="https://github.com/user-attachments/assets/351fb199-3756-4019-a630-fc1c457fe6c8" width="400" />

## License

This project is licensed under the MIT License.
