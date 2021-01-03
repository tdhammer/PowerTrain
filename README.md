# PowerTrain App

Android app project to generate/record power (watts) information while training on a bicycle trainer
with multiple levels but no digital power output to capture (*non-smart trainer*).

(An alternative to purchasing a power meter.)

Based on the concepts of [TrainerRoad's VirtualPower](https://www.trainerroad.com/virtual-power)
and [working with non-smart trainers](https://support.trainerroad.com/hc/en-us/articles/115002491883-VirtualPower-What-if-my-trainer-isn-t-supported-)
I wanted a tool to capture the data for use in personal analysis.
I am primarily attempting to gather "relative" power measurements for determining my performance
improvements (or declines) over time. 
I am less interested in "accurate" power measurements to compare to other riders or to calculate a
"true" speed (*calculation of how fast you would be going outside based the current watts you are
generating, the grade, your weight and aerodynamic factor profile calculated from your weight*).

Bluetooth LE functionality based on [PunchThrough's](https://punchthrough.com/)
[Ultimate Guide to Android Bluetooth Low Energy](https://punchthrough.com/android-ble-guide/) (and
[example code](https://github.com/PunchThrough/ble-starter-android)). However, a number of other
resources were referenced during development, including but not limited to:
* [Android Developers BLE Overview](https://developer.android.com/guide/topics/connectivity/bluetooth-le)
* [The Wheels on the Bike part 2](https://www.bluetooth.com/blog/part-2-the-wheels-on-the-bike-are-bluetooth-smart-bluetooth-smart-bluetooth-smart/)
* [Tutorial: Intro to Developing Bluetooth Smart Applications for Android](https://www.protechtraining.com/blog/post/tutorial-intro-to-developing-bluetooth-smart-applications-for-android-716)
* [Introduction to Bluetooth Low Energy Development](https://www.bluetooth.com/bluetooth-resources/bluetooth-le-developer-starter-kit/)
* [CircuitPython BLE Heart Rate Zone Trainer Display](https://learn.adafruit.com/circuitpython-ble-heart-rate-monitor-gizmo/heart-rate-service)

Converting the Cycle Speed and Cadence information is a little challenging and I needed a lot of help:
* [Cycling speed and cadence sensor's BLE Gatt characteristic data parsing and operation](https://stackoverflow.com/questions/49813704/cycling-speed-and-cadence-sensors-ble-gatt-characteristic-data-parsing-and-oper)
* [OLED Bike Computer](https://github.com/amcolash/oled-bike-computer)
* [TrainerPi](https://github.com/kloppen/trainerpi)

Interestingly, while researching I found projects creating the sensors part of this!
* [BLE Bicycle Speed Sensor](https://www.hackster.io/neal_markham/ble-bicycle-speed-sensor-f60b80)
* [Poor man's cycling power meter](https://hackaday.io/project/28276-poor-mans-cycling-power-meter)
* [Arduino BLE Cycling Power Service](https://teaandtechtime.com/arduino-ble-cycling-power-service/)

For Power calculations, I started with the TrainerRoad information (above), but found more curves
for more trainers
* [PowerCurve Sensor - power curves for each trainer](http://www.powercurvesensor.com/cycling-trainer-power-curves/)

