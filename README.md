# Android-BLECommunication
| Notice: This project is still being modified to meet the Bluetooth 5.0 standard. Please stay tuned. |
|---|

An android application that enable to use the smartphone as BLE Peripheral &amp; Central.<br/>
The project was developed by using Kotlin with AndroidStudio in Windows 10 OS, refered to Google's official documentations on Android BluetoothLE and package summary on bluetooth.le.<br/>
<br/>
The major goal of this project is to use smartphones(Android OS, minimum SDK 25) as two kind of BLE devices, Peripheral and Central.<br/>
Both functions operate independently in two different activities, of course, a smartphone can only operate with one type of device at a time. It is technically not difficult to operate with two types of equipment at once, but it reflects the properties of the actual BLE Peripheral (eg Beacon).<br/>
<br/>
## Functions
### BLE Peripheral
Transmits BLE Broadcast signal to others every 5 seconds.<br/>
The UUID used in this process is fixed and can be modified in the source code.
### BLE Central
Continuously receives nearby BLE Broadcast signals and repots the list on display and saves to file in the device.
The list contains the device name(address) and the RSSI, updates every 5 seconds.
It is designed to capture only the UUID that the same as the used one in BLE Peripheral on this project.
You can fixed it by removing the filter in the source code.<br/>
<br/>
## Getting Started
There are 2 methods for getting started.<br/>
I recommend the first method for only the users to try out this project.
The devlopers who want to utilize it to your own project, try the second method essentially.
### Use pre-compiled APK
just download and run the given precompiled APK on the smartphones, the easiest way.<br/>
### Clone project and run
Second, clone the project to your PC then compile&run with the smartphones or Virtual Devices.<br/>
It does not guarantee that the application will run successfully on the virtual device. However, this method has the advantage of being able to modify the source code more easily and can be further developed if necessary.
