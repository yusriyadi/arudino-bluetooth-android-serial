package tellabs.android.basekotlin.presentation.home

import android.bluetooth.BluetoothDevice
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_row_paired_device.view.*
import tellabs.android.basekotlin.R



class PairedDeviceItemAdapter (val device : BluetoothDevice, val listener : (BluetoothDevice)->Unit):Item(){

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            itemView.tvDeviceName.text = device.name
            itemView.tvMacAddress.text = device.address
            itemView.setOnClickListener {
                listener(device)
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_row_paired_device

}