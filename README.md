# PowerTrain App

Android app project to generate/record power (watts) information while training on a bicycle trainer
with multiple levels but no digital power output to capture (*non-smart trainer*).

Based on the concepts of [TrainerRoad's VirtualPower](https://www.trainerroad.com/virtual-power)
and [working with non-smart trainers](https://support.trainerroad.com/hc/en-us/articles/115002491883-VirtualPower-What-if-my-trainer-isn-t-supported-) I wanted a tool to capture the data for use in personal analysis.

Bluetooth LE functionality based on [PunchThrough's](https://punchthrough.com/)
[Ultimate Guide to Android Bluetooth Low Energy](https://punchthrough.com/android-ble-guide/) (and
[example code](https://github.com/PunchThrough/ble-starter-android)). However, a number of other
resources were referenced during development, including but not limited to:
* [Android Developers BLE Overview](https://developer.android.com/guide/topics/connectivity/bluetooth-le)
* [The Wheels on the Bike part 2](https://www.bluetooth.com/blog/part-2-the-wheels-on-the-bike-are-bluetooth-smart-bluetooth-smart-bluetooth-smart/)
* [Tutorial: Intro to Developing Bluetooth Smart Applications for Android](https://www.protechtraining.com/blog/post/tutorial-intro-to-developing-bluetooth-smart-applications-for-android-716)
* [Introduction to Bluetooth Low Energy Development](https://www.bluetooth.com/bluetooth-resources/bluetooth-le-developer-starter-kit/)
* [CircuitPython BLE Heart Rate Zone Trainer Display](https://learn.adafruit.com/circuitpython-ble-heart-rate-monitor-gizmo/heart-rate-service)
