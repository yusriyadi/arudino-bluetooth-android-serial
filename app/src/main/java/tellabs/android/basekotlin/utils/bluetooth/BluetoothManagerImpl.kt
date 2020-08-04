package thortechasia.android.basekotlin.utils.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface
import io.reactivex.Single
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * Implementation of BluetoothManager, package-private
 */
internal class BluetoothManagerImpl(private val adapter: BluetoothAdapter) : BluetoothManager {
    private val devices: MutableMap<String, BluetoothSerialDeviceImpl> = mutableMapOf()

    override val pairedDevices: Collection<BluetoothDevice>
        get() = adapter.bondedDevices

    override suspend fun openSerialDevice(mac: String): BluetoothSerialDevice {
        return openSerialDevice(mac, StandardCharsets.UTF_8)
    }

    override suspend fun openSerialDevice(mac: String, charset: Charset): BluetoothSerialDevice {
        return if (devices.containsKey(mac)) {
           devices[mac]!!
        } else {
                try {
                    val device = adapter.getRemoteDevice(mac)
                    val socket = device.createInsecureRfcommSocketToServiceRecord(SPP_UUID)
                    adapter.cancelDiscovery()
                    socket.connect()
                    val serialDevice = BluetoothSerialDeviceImpl(mac, socket, charset)
                    devices[mac] = serialDevice
                    return serialDevice
                } catch (e: Exception) {
                    throw BluetoothConnectException(e)
                }

        }
    }

    override fun closeDevice(mac: String) {
        devices.remove(mac)?.close()
    }

    override fun closeDevice(device: BluetoothSerialDevice) {
        closeDevice(device.mac)
    }

    override fun closeDevice(deviceInterface: SimpleBluetoothDeviceInterface) {
        closeDevice(deviceInterface.device.mac)
    }

    override fun close() {
        for (device in devices.values) {
            try {
                device.close()
            } catch (ignored: Throwable) {
            }
        }
        devices.clear()
    }

    companion object {
        val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }
}
