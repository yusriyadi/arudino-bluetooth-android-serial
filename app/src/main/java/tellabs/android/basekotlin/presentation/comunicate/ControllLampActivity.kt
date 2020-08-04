package tellabs.android.basekotlin.presentation.comunicate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import coil.api.load
import kotlinx.android.synthetic.main.activity_controll_lamp.*
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel
import tellabs.android.basekotlin.R

class ControllLampActivity : AppCompatActivity() {
    val vm by viewModel<ComunicateViewModel>()
    var mac = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controll_lamp)

        tvDeviceName.text = "Bluetooth Device Name : " + intent.getStringExtra("deviceName")
        mac = intent.getStringExtra("deviceMac")

        vm.setupViewModel(mac)
        toggleButton.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                vm.sendMessage("1")
            } else {
                vm.sendMessage("0")
            }
        }
        vm.getConnectionStatus().observe(this, Observer {
            when (it) {
                ComunicateViewModel.ConnectionStatus.CONNECTED -> {
                    imgStat.load(R.drawable.bg_green)
                    toggleButton.isEnabled = true
                    btnConn.setText("Putuskan")
                    btnConn.setOnClickListener {
                        vm.disconnect()
                    }

                }
                ComunicateViewModel.ConnectionStatus.DISCONNECTED -> {
                    imgStat.load(R.drawable.bg_red)
                    toggleButton.isEnabled = false
                    btnConn.setText("Sambungkan")
                    btnConn.setOnClickListener {
                        vm.connect(mac)
                    }

                }
                ComunicateViewModel.ConnectionStatus.CONNECTING -> {
                    imgStat.load(R.drawable.bg_yellow)
                    toggleButton.isEnabled = false
                    btnConn.setText("Menghubungkan")

                }

            }

            toast(it.name)
        })

    }



    override fun onBackPressed() {
        super.onBackPressed()
    }
}