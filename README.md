# Android Tablet Server for OpenTabletDriver

This is a modification of USB/IP server for Android. It lets you use your Android pen tablet as a PC graphics tablet with the help of OpenTabletDriver. Tested on a Samsung Galaxy Tab S9+. It currently supports pressure and up to two pen buttons, no tilt yet.

# Installation

1. Download the latest version of [usbip-win2](https://github.com/cezanne/usbip-win).
2. Install it by running PowerShell as an Administrator and executing the command `usbip.exe install`, **DO THIS AT YOUR OWN RISK, AS I'VE GOTTEN INTO BSOD LOOPS BECAUSE OF IT WHEN IT FAILS**.
3. Compile the Android app using Android Studio or download one of the releases and install it.
4. If you want to use a wired connection, enable USB debugging in the developer settings of your tablet.
5. Create a `Configurations` folder next to `OpenTabletDriver.UX.Wpf.exe` and paste [AndroidTablet.json](https://gist.github.com/dbalatoni13/8f57ebf32a07724df076a1165a520dbd) there.
6. Figure out your tablet screen's dimensions and edit the config accurately: `Width` and `Height` are the size of the screen in millimeters. You can see the `MaxX` and `MaxY` values by simply looking at the notification of the USB/IP service after touching the screen with your pen.

# Usage

This works over the network, but using a wired connection gives much better results. It doesn't matter at which step your start OpenTabletDriver.

## Wireless

1. Figure out the local IP address of your tablet.
2. Go into the folder of usbwin and in an Administrator shell, run `usbip.exe attach -b 1-1 -r <IP address>`.
3. Enjoy!

## Wired

1. Connect your tablet to your PC using a USB cable and run the app.
2. Run `adb forward tcp:3240 tcp:3240`.
3. Go into the folder of usbwin and in an Administrator shell, run `usbip.exe attach -b 1-1 -r 127.0.0.1`.
4. Enjoy!

Now you'll see a new USB device in the Device Manager. If there are issues with it, you might have to [install Zadig’s WinUSB onto it](https://opentabletdriver.net/Wiki/Install/Windows).

The attach command has to be rerun everytime the app gets closed by you or by the OS for battery/RAM saving.

# TODO
Add installation instructions for linux, there's no BSOD problem there.