package com.example.elder_care.utils.bluetooth

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BluetoothDataViewModel : ViewModel() {

    private val connectedDeviceData = MutableLiveData<List<String>>()

    // 연결된 기기 데이터 설정
    fun setConnectedDeviceData(data: String) {
        connectedDeviceData.value = listOf(data)
    }

    // 연결된 기기 데이터 추가
    fun addConnectedDeviceData(data: String) {
        val currentList = connectedDeviceData.value ?: emptyList()
        val newList = currentList.toMutableList().apply {
            add(data)
        }
        connectedDeviceData.value = newList
    }

    // 연결된 기기 데이터 가져오기
    fun getConnectedDeviceData(): LiveData<List<String>> {
        return connectedDeviceData
    }
}