package tellabs.android.basekotlin.presentation.comunicate

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.ajalt.timberkt.d
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface.OnMessageReceivedListener
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface.OnMessageSentListener
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.launch
import thortechasia.android.basekotlin.utils.bluetooth.BluetoothManager

class ComunicateViewModel() : ViewModel() {

    // Our BluetoothManager!
    private var bluetoothManager: BluetoothManager

    // Our Bluetooth Device! When disconnected it is null, so make sure we know that we need to deal with it potentially being null
    private var deviceInterface: SimpleBluetoothDeviceInterface? = null

    // The messages feed that the activity sees
    private val messagesData = MutableLiveData<String>()

    // The connection status that the activity sees
    private val connectionStatusData =
        MutableLiveData<ConnectionStatus>()

    // The device name that the activity sees
    private val deviceNameData = MutableLiveData<String>()

    // The message in the message box that the activity sees
    private val messageData = MutableLiveData<String>()


    // A variable to help us not double-connect
    private var connectionAttemptedOrMade = false

    init {
        bluetoothManager = BluetoothManager.instance!!
        connectionStatusData.postValue(ConnectionStatus.TERPUTUS)
    }


    fun connect(mac: String) {
        // Check we are not already connecting or connected
        if (!connectionAttemptedOrMade) {
            connectionStatusData.value = ConnectionStatus.MENGHUBUNGKAN
            d { "hitted" }
            // Connect asynchronously
            viewModelScope.launch {
                kotlin.runCatching {
                    bluetoothManager.openSerialDevice(mac)
                }.onSuccess { device ->
                    d { "success" }
                    connectionStatusData.value =ConnectionStatus.TERHUBUNG
                    deviceInterface = device.toSimpleDeviceInterface()
                    if (deviceInterface != null) {
                        device.toSimpleDeviceInterface().setListeners(
                            object : OnMessageReceivedListener {
                                override fun onMessageReceived(message: String) {
                                    d { "diterima : $message" }
                                }
                            },
                            object : OnMessageSentListener {
                                override fun onMessageSent(message: String) {
                                    d { "terkirim : $message" }
                                }
                            },
                            object : SimpleBluetoothDeviceInterface.OnErrorListener {
                                override fun onError(error: Throwable) {
                                    d { "error : $error" }
                                }

                            })
                    } else {
                        connectionStatusData.value=ConnectionStatus.TERPUTUS
                    }


                }.onFailure {
                    d { "error" }
                    connectionAttemptedOrMade = false
                    connectionStatusData.value=ConnectionStatus.TERPUTUS
                }
            }
            // Remember that we made a connection attempt.
            connectionAttemptedOrMade = true
            // Tell the activity that we are connecting.

        }
    }



    // Called when the user presses the disconnect button
    fun disconnect() {
        if (connectionAttemptedOrMade && deviceInterface != null) {
            connectionAttemptedOrMade = false
                bluetoothManager.closeDevice(deviceInterface!!)
            deviceInterface = null
            connectionStatusData.postValue(ConnectionStatus.TERPUTUS)
        }
    }
    fun killBluetoothManager(){
        bluetoothManager.close()
    }


    fun sendMessage(message: String?) {
        // Check we have a connected device and the message is not empty, then send the message
        if (deviceInterface != null && !TextUtils.isEmpty(message)) {
            deviceInterface!!.sendMessage(message!!)
        }
    }


    override fun onCleared() {

    }


    fun getMessages(): LiveData<String>? {
        return messagesData
    }

    // Getter method for the activity to use.
    fun getConnectionStatus(): LiveData<ConnectionStatus> {
        return connectionStatusData
    }

    // Getter method for the activity to use.
    fun getDeviceName(): LiveData<String>? {
        return deviceNameData
    }

    // Getter method for the activity to use.
    fun getMessage(): LiveData<String>? {
        return messageData
    }

    // An enum that is passed to the activity to indicate the current connection status
    enum class ConnectionStatus {
        TERPUTUS, MENGHUBUNGKAN, TERHUBUNG
    }


}