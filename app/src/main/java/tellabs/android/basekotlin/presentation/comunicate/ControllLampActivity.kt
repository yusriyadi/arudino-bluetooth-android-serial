package tellabs.android.basekotlin.presentation.comunicate

import android.R.id
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log.d
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.api.load
import com.github.ajalt.timberkt.d
import kotlinx.android.synthetic.main.activity_controll_lamp.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.aflak.bluetooth.Bluetooth
import me.aflak.bluetooth.interfaces.DeviceCallback
import org.jetbrains.anko.toast
import tellabs.android.basekotlin.R
import tellabs.android.basekotlin.utils.DelimiterReader
import tellabs.android.basekotlin.utils.LineReader
import tellabs.android.basekotlin.utils.MyDialog
import tellabs.android.basekotlin.utils.visible


class ControllLampActivity : AppCompatActivity() {
    //val vm by viewModel<ComunicateViewModel>()

    lateinit var myDialog: MyDialog
    lateinit var device: BluetoothDevice
    lateinit var bluetooth: Bluetooth

    private var doubleBackToExitPressedOnce: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controll_lamp)
        myDialog = MyDialog(this)

        device = intent.getParcelableExtra("device")
        tvDeviceName.text = "Device Name : " + device.name
        btnConn.setText("Menghubungkan")

        bluetooth = Bluetooth(this)
        bluetooth.setCallbackOnUI(this)
        bluetooth.setDeviceCallback(deviceCallback)

        setIsEnableView(false)

        toggleButton.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                bluetooth.send("1")
            } else {
                bluetooth.send("0")
            }
        }

        toggleButton2.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                bluetooth.send("3")
            } else {
                bluetooth.send("2")
            }
        }

    }

    private fun setIsEnableView(state: Boolean) {
        toggleButton.isEnabled = state
        toggleButton2.isEnabled = state
    }


    val deviceCallback = object : DeviceCallback {
        override fun onDeviceDisconnected(device: BluetoothDevice?, message: String?) {
            myDialog.dismiss()
            setIsEnableView(false)
            resetToggle()
            toast("perangkat terputus : $message")
            imgStat.load(R.drawable.bg_red_rounded)
            btnConn.setText("Sambungkan")
            btnConn.setOnClickListener {
                myDialog.show()
                btnConn.setText("Menghubungkan")
                imgStat.load(R.drawable.bg_yellow_rounded)
                bluetooth.connectToDevice(device)
            }
        }

        override fun onDeviceConnected(device: BluetoothDevice?) {
            myDialog.dismiss()
            setIsEnableView(true)
            toast("terhubung ke perangkat : ${device?.name}")
            loadLastStatus()
            btnConn.visible()
            imgStat.load(R.drawable.bg_green_rounded)
            btnConn.setText("Putuskan")
            btnConn.setOnClickListener {
                myDialog.show()
                bluetooth.disconnect()
            }
        }

        override fun onConnectError(device: BluetoothDevice?, message: String?) {
            imgStat.load(R.drawable.bg_red_rounded)
            myDialog.dismiss()
            toast("gagal terhubung, akan mengubungkan ulang...")
            setIsEnableView(false)
            lifecycleScope.launch {
                delay(2000)
                bluetooth.connectToDevice(device)
                myDialog.show()
                btnConn.setText("Menghubungkan")
                imgStat.load(R.drawable.bg_yellow_rounded)
            }
        }

        override fun onMessage(message: ByteArray?) {
            val text = String(message!!)

            d { text }

            if (text.contains("0")) {
                toggleButton.isChecked = false
            } else if (text.contains("1")) {
                toggleButton.isChecked = true
            } else if (text.contains("2")) {
                toggleButton2.isChecked = false
            } else if (text.contains("3")) {
                toggleButton2.isChecked = true
            }
        }

        override fun onError(errorCode: Int) {

        }

    }

    private fun resetToggle() {
        toggleButton.isChecked = false
        toggleButton2.isChecked = false
    }

    private fun loadLastStatus() {
        bluetooth.send("l")
        bluetooth.send("k")
    }


    override fun onStart() {
        super.onStart()
        if (!bluetooth.isConnected){
            bluetooth.onStart();
            bluetooth.connectToDevice(device)
            imgStat.load(R.drawable.bg_yellow_rounded)
            myDialog.show()
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            bluetooth.disconnect()
            lifecycleScope.launch {
                delay(100)
                super.onBackPressed()
            }
        }
        this.doubleBackToExitPressedOnce = true
        toast("jika kembali anda akan ter putus dengan perangkat")
        lifecycleScope.launch {
            delay(2000)
            doubleBackToExitPressedOnce = false
        }
    }
}