package tellabs.android.basekotlin.presentation.comunicate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import coil.api.load
import kotlinx.android.synthetic.main.activity_controll_lamp.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel
import tellabs.android.basekotlin.R

class ControllLampActivity : AppCompatActivity() {
    val vm by viewModel<ComunicateViewModel>()
    var mac = ""
    private var doubleBackToExitPressedOnce: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controll_lamp)

        tvDeviceName.text = "Bluetooth Device Name : " + intent.getStringExtra("deviceName")
        mac = intent.getStringExtra("deviceMac")

        toggleButton.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                vm.sendMessage("1")
            } else {
                vm.sendMessage("0")
            }
        }
        observeConnectionStatus()

    }

    private fun observeConnectionStatus() {
        vm.getConnectionStatus().observe(this, Observer {
            when (it) {
                ComunicateViewModel.ConnectionStatus.TERHUBUNG -> {
                    imgStat.load(R.drawable.bg_green)
                    toggleButton.isEnabled = true
                    btnConn.setText("Putuskan")
                    btnConn.setOnClickListener {
                        vm.disconnect()
                    }

                }
                ComunicateViewModel.ConnectionStatus.TERPUTUS -> {
                    imgStat.load(R.drawable.bg_red)
                    toggleButton.isEnabled = false
                    btnConn.setText("Sambungkan")
                    btnConn.setOnClickListener {
                        vm.connect(mac)
                    }

                }
                ComunicateViewModel.ConnectionStatus.MENGHUBUNGKAN -> {
                    imgStat.load(R.drawable.bg_yellow)
                    toggleButton.isEnabled = false
                    btnConn.setText("Menghubungkan")
                }

            }

            toast(it.name)
        })
    }


    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            vm.disconnect()
            vm.killBluetoothManager()
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