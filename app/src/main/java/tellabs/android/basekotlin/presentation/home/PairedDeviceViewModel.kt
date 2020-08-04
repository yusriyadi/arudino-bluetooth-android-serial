package tellabs.android.basekotlin.presentation.home

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tellabs.android.basekotlin.utils.UiState
import thortechasia.android.basekotlin.utils.bluetooth.BluetoothManager

class PairedDeviceViewModel() : ViewModel() {

    var bluetoothManager: BluetoothManager

    private val pairedDeviceList = MutableLiveData<UiState<Collection<BluetoothDevice>>>()


    init {
        bluetoothManager = BluetoothManager.instance!!
    }

    fun observePairedDevice() = pairedDeviceList

    fun checkBluetooth(): Boolean {
        if (bluetoothManager == null) {
            return true
        }
        return false
    }

    fun getPairedList() {
        viewModelScope.launch {
            kotlin.runCatching {
                pairedDeviceList.value = UiState.Loading()
                bluetoothManager.pairedDevices
            }.onSuccess {
                pairedDeviceList.value = UiState.Success(it)
            }.onFailure {
                pairedDeviceList.value = UiState.Error(it)
            }

        }
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothManager.close()
    }


}