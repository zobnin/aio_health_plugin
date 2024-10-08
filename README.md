Plugin for [AIO Launcher](https://aiolauncher.app) that displays health data (steps, distance, heart rate, calories). It retrieves data from Health Connect using the official API.

The implementation of this plugin differs from others by using a ForegroundService. This is necessary because Health Connect requires the app requesting data to be in a Foreground state.

The advantage of this approach is that the system does not apply battery-saving restrictions to the plugin. The downside is that the service must be started manually. To handle this, the plugin sends a notification with the message "Tap to enable." In response, it launches the plugin's main activity, which in turn starts the service.
