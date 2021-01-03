package com.ballpein.powertrain

import android.app.Service
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.ParcelUuid
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.*


// TODO !! ACCESS_FINE_LOCATION permission (if not set silent failure?!?!)
// TODO :: recover connection/data stream when lost?!?!
class BleService : Service() {
    private val TAG = "BleService"


    // TODO :: remove temporary method to do a send data
    private lateinit var spdHandler: android.os.Handler
    private lateinit var spdRunnable: Runnable
    private lateinit var cadHandler: android.os.Handler
    private lateinit var cadRunnable: Runnable


    companion object {
        val HR_RAW_BROADCAST: String = BleService::class.java.getName() + "HeartRateRaw"
        val EXTRA_HR_RAW_DATA: String = "HRRawData"
        val SPD_RAW_BROADCAST: String = BleService::class.java.getName() + "SpeedRaw"
        val EXTRA_SPD_RAW_DATA: String = "SpeedRawData"
        val CAD_RAW_BROADCAST: String = BleService::class.java.getName() + "CadenceRaw"
        val EXTRA_CAD_RAW_DATA: String = "CadenceRawData"
        val EXTRA_TIME_RAW_DATA: String = "TimeRawData"
    }

    override fun onCreate() {
        Log.i(TAG, "Service onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.i(TAG, "Service onStartCommand " + startId)

        // initialize sensor connections
        connectSensors()

        // TODO :: remove temporary methods to do a send data
//        spdHandler = android.os.Handler()
//        spdRunnable = Runnable { showRandomSpeed() }
//        spdHandler.postDelayed(spdRunnable, 4250)
//        cadHandler = android.os.Handler()
//        cadRunnable = Runnable { showRandomCadence() }
//        cadHandler.postDelayed(cadRunnable, 1250)

        Log.i(TAG, "Service running")
        return Service.START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.i(TAG, "Service onBind")
        return null
    }

    override fun onDestroy() {
        Log.i(TAG, "Service onDestroy")
        // TODO :: release BLE device resources
        // ?? bluetoothGatt?.close()
        // ?? bluetoothGatt = null
    }

    private fun connectSensors(){
        // if sensors are configured...
        // TODO :: do scan in settings and save info so just a connect here!
        startBleScan() // not necessary if sensors are configured?
        // start also connects...
    }


    // TODO :: remove temporary method to do a send data
    private var randomSpeed = 0.0F
    private fun showRandomSpeed() {
        val rand = java.util.Random()
        val number = kotlin.math.abs(rand.nextInt() % 1000)
        randomSpeed += 1.1F
        if (randomSpeed > 99.0F) randomSpeed = 0.52F
//        randomSpeed += (number.toFloat() / 1000)
        Log.d(TAG, "random speed = $randomSpeed")
        val intent = Intent(SPD_RAW_BROADCAST)
        intent.putExtra(EXTRA_SPD_RAW_DATA, randomSpeed)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        spdHandler.postDelayed(spdRunnable, 2350)
    }
    // TODO :: remove temporary method to do a send data
    private fun showRandomCadence() {
        val rand = java.util.Random()
        val randomCadence = kotlin.math.abs(rand.nextInt() % 1000)
        Log.d(TAG, "random cadence = $randomCadence")
        val intent = Intent(CAD_RAW_BROADCAST)
        intent.putExtra(EXTRA_CAD_RAW_DATA, randomCadence)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        cadHandler.postDelayed(cadRunnable, 2350)
    }


    /*******************************************
     * ble support
     *******************************************/
    // Bluetooth Base UUID = 00000000-0000-1000-8000-00805F9B34FB
    // Bluetooth LE Heart Rate Service UUID = 180D
    //              HR Measurement Char UUID = 2A37
    private val HEARTRATE_SERVICE_UUID = "0000180D-0000-1000-8000-00805F9B34FB"
    private val UUID_HEARTRATE_SERVICE = UUID.fromString(HEARTRATE_SERVICE_UUID)
    private val HEARTRATE_CHAR_UUID = "00002A37-0000-1000-8000-00805F9B34FB"
    private val UUID_HEARTRATE_CHAR = UUID.fromString(HEARTRATE_CHAR_UUID)
    // TODO :: add Speed & Cadence sensors
    // Bluetooth LE Cycling Speed and Cadence Service UUID = 1816
    //              Speed/Cadence Measurement Char UUID = 2A5B
    private val CYCLING_SERVICE_UUID = "00001816-0000-1000-8000-00805F9B34FB"
    private val UUID_CYCLING_SERVICE = UUID.fromString(CYCLING_SERVICE_UUID)
    private val CYCLING_CHAR_UUID = "00002A5B-0000-1000-8000-00805F9B34FB"
    private val UUID_CYCLING_CHAR = UUID.fromString(CYCLING_CHAR_UUID)
    // Bluetooth LE Client Characteristic Configuration Descriptor = 2902
    private val CCCD_UUID = "00002902-0000-1000-8000-00805F9B34FB"
    private val UUID_CCCD = UUID.fromString(CCCD_UUID)

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    // From the previous section:
    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val hrmFilter = ScanFilter.Builder().setServiceUuid(
        ParcelUuid.fromString(HEARTRATE_SERVICE_UUID)
    ).build()
    private val cscFilter = ScanFilter.Builder().setServiceUuid(
        ParcelUuid.fromString(CYCLING_SERVICE_UUID)
    ).build()
    private val bleDeviceFilter = listOf<ScanFilter>(hrmFilter, cscFilter)

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    private val scanResults = mutableListOf<ScanResult>()
    private val gattConnections = mutableListOf<BluetoothGatt>()

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            Log.i(TAG, "onScanResult")
            val indexQuery = scanResults.indexOfFirst { it.device.address == result.device.address }
            if (indexQuery != -1) { // A scan result already exists with the same address
                with(result.device) {
                    Log.i(TAG, "Found same device! Name: ${name ?: "Unnamed"}, address: $address")
                }
                scanResults[indexQuery] = result
            } else {
                with(result.device) {
                    Log.i(TAG, "Found BLE device! Name: ${name ?: "Unnamed"}, address: $address")
                }
                scanResults.add(result)
            }
            // TODO :: timeout? better method to determine all sensors found?
            if (scanResults.count() == 3) {
                stopBleScan()

                // connect to each sensor
                scanResults.forEach {
                    // only handling one sensor at the moment!!
                    with(it.device) {
                        Log.i(TAG, "Connecting to $address")
                        connectGatt(getApplicationContext(), false, gattCallback)
                    }
                }
            }
        }

        // TODO :: Error 133 disconnect handle!!! (other disconnects?)

        override fun onScanFailed(errorCode: Int) {
            Log.e(TAG, "onScanFailed: code $errorCode")
//            Log.i(TAG, "(?? it might, but stop/start not run?!??)")
//            stopBleScan()
//            startBleScan()
        }
    }

    private var isScanning = false

    fun startBleScan() {
        Log.i(TAG, "startBleScan()")
        scanResults.clear()
        isScanning = true
//        bleScanner.startScan(null, scanSettings, scanCallback)
        bleScanner.startScan(bleDeviceFilter, scanSettings, scanCallback)
    }

    private fun stopBleScan() {
        bleScanner.stopScan(scanCallback)
        isScanning = false
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            val deviceAddress = gatt.device.address

            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.w(TAG, "Successfully connected to $deviceAddress")
                    Handler(Looper.getMainLooper()).post {
                        gatt?.discoverServices()
                    }
                    // TODO: Store a reference to BluetoothGatt
                    gattConnections.add(gatt)
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.w(TAG, "Successfully disconnected from $deviceAddress")
                    gatt.close()
                }
            } else {
                Log.w(TAG, "Error $status encountered for $deviceAddress! Disconnecting...")
                gatt.close()
            }
        }
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            with(gatt) {
                Log.w(TAG, "Discovered ${services.size} services for ${device.name.substring(0,3)} (${device.address})")
//                printGattTable()

                // enable notifications of heart rate measurement
                if (device.name.substring(0, 3) == "COO") {
                    val hrmChar = gatt
                        .getService(UUID_HEARTRATE_SERVICE)?.getCharacteristic(UUID_HEARTRATE_CHAR)
                    gatt.setCharacteristicNotification(hrmChar, true)
                    val cccDescriptor = hrmChar?.getDescriptor(UUID_CCCD)
                    cccDescriptor?.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                    gatt.writeDescriptor(cccDescriptor)
                } else {
                    // enable notifications of speed/cadence measurement
                    val cscChar = gatt
                        .getService(UUID_CYCLING_SERVICE)?.getCharacteristic(UUID_CYCLING_CHAR)
                    gatt.setCharacteristicNotification(cscChar, true)
                    val cccDescriptor = cscChar?.getDescriptor(UUID_CCCD)
                    cccDescriptor?.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                    gatt.writeDescriptor(cccDescriptor)
                }
                // Consider connection setup as complete here
            }
        }
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            with(characteristic) {
                when (status) {
                    BluetoothGatt.GATT_SUCCESS -> {
                        Log.i(TAG, "Read characteristic $uuid:\n${value.toHexString()}")
                    }
                    BluetoothGatt.GATT_READ_NOT_PERMITTED -> {
                        Log.e(TAG, "Read not permitted for $uuid!")
                    }
                    else -> {
                        Log.e(TAG, "Characteristic read failed for $uuid, error: $status")
                    }
                }
            }
        }
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            val devName = gatt.device.name
            val devAddr = gatt.device.address
            with(characteristic) {
                Log.i(TAG, "[$devName ($devAddr)] Char $uuid changed | value: ${value.toHexString()}")
            }
            processCharacteristicChange(characteristic)
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            super.onDescriptorWrite(gatt, descriptor, status)
            with(descriptor) {
                val uuid = descriptor?.uuid
                val value = descriptor?.value
                when (status) {
                    android.bluetooth.BluetoothGatt.GATT_SUCCESS -> {
                        android.util.Log.i(TAG, "Write descriptor $uuid:\n${value?.toHexString()}")
                    }
                    else -> {
                        android.util.Log.e(TAG, "Descriptor write failed for $uuid, error: $status")
                    }
                }
            }
        }
    }


    fun ByteArray.toHexString(): String =
        joinToString(separator = " ", prefix = "0x") { String.format("%02X", it) }

    private fun BluetoothGatt.printGattTable() {
        if (services.isEmpty()) {
            Log.i(TAG, "No service and characteristic available, call discoverServices() first?")
            return
        }
        services.forEach { service ->
            val characteristicsTable = service.characteristics.joinToString(
                separator = "\n|--",
                prefix = "|--"
            ) { it.uuid.toString() }
            Log.i(
                TAG, "\nService ${service.uuid}\nCharacteristics:\n$characteristicsTable"
            )
        }
    }

    private fun processCharacteristicChange(characteristic: BluetoothGattCharacteristic) {
        var msg: String? = null
        if (UUID_HEARTRATE_CHAR.equals(characteristic.uuid)) {
            val flag = characteristic.properties
            var format = -1
            if (flag and 0x01 != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16
                Log.d(TAG, "Heart rate format UINT16.")
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8
                Log.d(TAG, "Heart rate format UINT8.")
            }
            val heartRate = characteristic.getIntValue(format, 1)
            Log.d(TAG, String.format("Received heart rate: %d", heartRate))
            val intent = Intent(HR_RAW_BROADCAST)
            intent.putExtra(EXTRA_HR_RAW_DATA, heartRate)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            msg = heartRate.toString()
        } else if (UUID_CYCLING_CHAR.equals(characteristic.uuid)) {
            // get measurement type (0x01 - speed; 0x02 - cadence; 0x03 - BOTH)
            var cscMeasurement = characteristic.getIntValue(
                BluetoothGattCharacteristic.FORMAT_UINT8, 0)
            if (cscMeasurement == 0x01) {
                // Speed
                cscMeasurement = characteristic.getIntValue(
                    BluetoothGattCharacteristic.FORMAT_UINT32, 1)
                var time = characteristic.getIntValue(
                    BluetoothGattCharacteristic.FORMAT_UINT16, 5)
                Log.d(TAG, String.format("Received speed (rev): 0x%x (%d)",
                    cscMeasurement,cscMeasurement))
                Log.d(TAG, String.format("                time: 0x%x (%d)",time,time))
                val intent = Intent(SPD_RAW_BROADCAST)
                intent.putExtra(EXTRA_SPD_RAW_DATA, cscMeasurement)
                intent.putExtra(EXTRA_TIME_RAW_DATA, time)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            } else if (cscMeasurement == 0x02) {
                // Cadence
                cscMeasurement = characteristic.getIntValue(
                    BluetoothGattCharacteristic.FORMAT_UINT16, 1)
                var time = characteristic.getIntValue(
                    BluetoothGattCharacteristic.FORMAT_UINT16, 3)
                Log.d(TAG, String.format("Received cadence (rev): 0x%x (%d)",
                    cscMeasurement,cscMeasurement))
                Log.d(TAG, String.format("                  time: 0x%x (%d)",time,time))
                val intent = Intent(CAD_RAW_BROADCAST)
                intent.putExtra(EXTRA_CAD_RAW_DATA, cscMeasurement)
                intent.putExtra(EXTRA_TIME_RAW_DATA, time)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            } else {
                // Both? Other?
                Log.e(TAG, String.format("Unknown CSC Measurement type: 0x%x", cscMeasurement))
            }
        } else {
            // For all other profiles, writes the data formatted in HEX.
            val data = characteristic.value
            if (data != null && data.size > 0) {
                val stringBuilder = StringBuilder(data.size)
                for (byteChar in data) stringBuilder.append(String.format("%02X ", byteChar))
                msg = """
                ${String(data)}
                $stringBuilder
                """.trimIndent()
            }
        }
//       if (mBLEServiceCb != null) {
//            mBLEServiceCb.displayData(msg)
//        }
    }

}