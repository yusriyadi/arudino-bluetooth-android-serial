package tellabs.android.basekotlin.presentation.home

import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ajalt.timberkt.d
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.activity_paired_device.*
import me.aflak.bluetooth.Bluetooth
import me.aflak.bluetooth.interfaces.BluetoothCallback
import me.aflak.bluetooth.interfaces.DiscoveryCallback
import org.jetbrains.anko.toast
import tellabs.android.basekotlin.R
import tellabs.android.basekotlin.presentation.comunicate.SwitchControlActivity


class PairedDeviceActivity : AppCompatActivity() {
    private val groupAdapter = GroupAdapter<ViewHolder>()

    //    private val vm by viewModel<PairedDeviceViewModel>()
    lateinit var bluetooth: Bluetooth
    var devices = mutableListOf<BluetoothDevice>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bluetooth = Bluetooth(this)
        setContentView(R.layout.activity_paired_device)
        rvPaired.apply {
            layoutManager = LinearLayoutManager(this@PairedDeviceActivity)
            adapter = groupAdapter
        }

        bluetooth.setBluetoothCallback(blutoothCallback)

    }

    private fun setDeviceToAdapter(devices : List<BluetoothDevice>) {
        groupAdapter.clear()
        devices.forEach {
            groupAdapter.add(PairedDeviceItemAdapter(it) { device->
                val intent = Intent(this, SwitchControlActivity::class.java)
                intent.putExtra("device", device)
             startActivity(intent)
            })
        }
    }

    private fun discoverCallback() {
        bluetooth.setDiscoveryCallback(object : DiscoveryCallback {
            override fun onDevicePaired(device: BluetoothDevice?) {

            }

            override fun onDiscoveryStarted() {

            }

            override fun onDeviceUnpaired(device: BluetoothDevice?) {

            }

            override fun onError(errorCode: Int) {

            }

            override fun onDiscoveryFinished() {

            }

            override fun onDeviceFound(device: BluetoothDevice?) {

            }

        })
    }

    fun setDeviceToList(){
        d { "devices :" + bluetooth.pairedDevices }
        setDeviceToAdapter(bluetooth.pairedDevices)
    }


    private val blutoothCallback = object : BluetoothCallback {
        override fun onUserDeniedActivation() {
            toast("bluetooth denied")
        }

        override fun onBluetoothOff() {
            toast("bluetooth off")

        }

        override fun onBluetoothOn() {
            toast("bluetooth on")
        }

        override fun onBluetoothTurningOn() {
            toast("bluetooth turning on")

        }

        override fun onBluetoothTurningOff() {
            toast("bluetooth turning off")

        }

    }

    override fun onStart() {
        super.onStart()

        bluetooth.onStart()
        if (bluetooth.isEnabled()) {
            toast("enabled")
            setDeviceToList()
        } else {
            toast("disabled")
            bluetooth.showEnableDialog(this)
        }
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        bluetooth.onActivityResult(requestCode, resultCode);
    }

    override fun onStop() {
        super.onStop()
        bluetooth.onStop();
    }

}