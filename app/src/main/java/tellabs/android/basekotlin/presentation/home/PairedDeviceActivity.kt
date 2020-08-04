package tellabs.android.basekotlin.presentation.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.activity_paired_device.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel
import tellabs.android.basekotlin.R
import tellabs.android.basekotlin.presentation.comunicate.ControllLampActivity
import tellabs.android.basekotlin.utils.UiState

class PairedDeviceActivity : AppCompatActivity() {
    private val groupAdapter = GroupAdapter<ViewHolder>()
    private val vm by viewModel<PairedDeviceViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paired_device)
        if (vm.checkBluetooth()) {
            toast("bluetooth not available")
        }
        vm.getPairedList()
        vm.observePairedDevice().observe(this, Observer {
            when (it) {
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    groupAdapter.clear()
                    it.data.forEach {
                        val item = PairedDevice(it.name, it.address)
                        groupAdapter.add(PairedDeviceItemAdapter(item){
                            startActivity<ControllLampActivity>("deviceName" to it.name, "deviceMac" to it.mac)
                        })
                    }
                }
                is UiState.Error -> {

                }
            }
        })

        rvPaired.apply {
            layoutManager = LinearLayoutManager(this@PairedDeviceActivity)
            adapter = groupAdapter
        }


    }
}