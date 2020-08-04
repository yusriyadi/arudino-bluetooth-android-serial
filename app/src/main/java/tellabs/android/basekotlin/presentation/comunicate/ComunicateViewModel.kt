package tellabs.android.basekotlin.presentation.comunicate

import android.app.Application
import android.text.TextUtils
import android.util.Log.d
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.ajalt.timberkt.d
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface.OnMessageReceivedListener
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface.OnMessageSentListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import thortechasia.android.basekotlin.utils.bluetooth.BluetoothManager
import thortechasia.android.basekotlin.utils.bluetooth.BluetoothSerialDevice

class ComunicateViewModel() : ViewModel() {
    // A CompositeDisposable that keeps track of all of our asynchronous tasks
    private val compositeDisposable = CompositeDisposable()

    // Our BluetoothManager!
    private var bluetoothManager: BluetoothManager? = null

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

    // Our modifiable record of the conversation
    private var messages = StringBuilder()

    // Our configuration
    private var deviceName: String? = null
    private var mac: String? = null

    // A variable to help us not double-connect
    private var connectionAttemptedOrMade = false

    // A variable to help us not setup twice
    private var viewModelSetup = false


    // Called in the activity's onCreate(). Checks if it has been called before, and if not, sets up the data.
    // Returns true if everything went okay, or false if there was an error and therefore the activity should finish.
    fun setupViewModel(mac: String?) {
        // Check we haven't already been called
        if (!viewModelSetup) {
            viewModelSetup = true

            // Setup our BluetoothManager
            bluetoothManager = BluetoothManager.instance
            if (bluetoothManager == null) {
                // Bluetooth unavailable on this device :( tell the user

                // Tell the activity there was an error and to close

            }


            // Tell the activity the device name so it can set the title
            deviceNameData.postValue(deviceName)
            // Tell the activity we are disconnected.
            connectionStatusData.postValue(ConnectionStatus.DISCONNECTED)
        }
        // If we got this far, nothing went wrong, so return true

    }

    // Called when the user presses the connect button
    fun connect(mac: String) {
        // Check we are not already connecting or connected
        if (!connectionAttemptedOrMade) {
            d { "hitted" }
            // Connect asynchronously

            
            compositeDisposable.add(
                bluetoothManager!!.openSerialDevice(mac)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ device ->
                        d { "success" }
                        onConnected(device.toSimpleDeviceInterface())
                    })
                    { t ->
                        d { "error" }
                        connectionAttemptedOrMade = false
                        connectionStatusData.postValue(ConnectionStatus.DISCONNECTED)
                    }
            )
            // Remember that we made a connection attempt.
            connectionAttemptedOrMade = true
            // Tell the activity that we are connecting.
            connectionStatusData.postValue(ConnectionStatus.CONNECTING)
        }
    }

    // Called when the user presses the disconnect button
    fun disconnect() {
        // Check we were connected
        if (connectionAttemptedOrMade && deviceInterface != null) {
            connectionAttemptedOrMade = false
            // Use the library to close the connection
            bluetoothManager!!.closeDevice(deviceInterface!!)
            // Set it to null so no one tries to use it
            deviceInterface = null
            // Tell the activity we are disconnected
            connectionStatusData.postValue(ConnectionStatus.DISCONNECTED)
        }
    }

    // Called once the library connects a bluetooth device
    private fun onConnected(deviceInterface: SimpleBluetoothDeviceInterface) {
        this.deviceInterface = deviceInterface
        if (this.deviceInterface != null) {
            // We have a device! Tell the activity we are connected.
            connectionStatusData.postValue(ConnectionStatus.CONNECTED)
            // Setup the listeners for the interface
            this.deviceInterface!!.setListeners(
                object : OnMessageReceivedListener {
                    override fun onMessageReceived(message: String) {
                        d { "diterima : $message" }
                        messageData.postValue("$message")
                    }
                },
                object : OnMessageSentListener {
                    override fun onMessageSent(message: String) {
                        d { "terkirim : $message" }
                        messageData.postValue("terkirim : $message")
                    }
                },
                object : SimpleBluetoothDeviceInterface.OnErrorListener {
                    override fun onError(error: Throwable) {
                        d { "error : $error" }
                        messageData.postValue("error : $error")
                    }

                }

            )
            // Tell the user we are connected.

            // Reset the conversation
            messages = StringBuilder()
            messagesData.postValue(messages.toString())
        } else {
            // deviceInterface was null, so the connection failed

            connectionStatusData.postValue(ConnectionStatus.DISCONNECTED)
        }
    }

    // Adds a received message to the conversation
    private fun onMessageReceived(message: String) {
        messages.append(deviceName).append(": ").append(message).append('\n')
        messagesData.postValue(messages.toString())
    }

    // Adds a sent message to the conversation
    private fun onMessageSent(message: String) {
        // Add it to the conversation
//        messages.append(getApplication<Application>().getString(R.string.you_sent)).append(": ")
//            .append(message).append('\n')
        messagesData.postValue(messages.toString())
        // Reset the message box
        messageData.postValue("")
    }

    // Send a message
    fun sendMessage(message: String?) {
        // Check we have a connected device and the message is not empty, then send the message
        if (deviceInterface != null && !TextUtils.isEmpty(message)) {
            deviceInterface!!.sendMessage(message!!)
        }
    }

    // Called when the activity finishes - clear up after ourselves.
    override fun onCleared() {
        // Dispose any asynchronous operations that are running
        compositeDisposable.dispose()
        // Shutdown bluetooth connections
        bluetoothManager!!.close()
    }


    // Getter method for the activity to use.
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
        DISCONNECTED, CONNECTING, CONNECTED
    }


}