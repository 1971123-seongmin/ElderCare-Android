package com.example.elder_care.utils

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import java.util.UUID

class BluetoothManager(private val context: Context) {

    private val REQUEST_ENABLE_BT=1

    private val bluetoothManager: BluetoothManager by lazy {
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        bluetoothManager.adapter
    }

    private val adapter = mutableMapOf<String, String>()

    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var intentFilter: IntentFilter

    init {
        setupBroadcastReceiver()
    }

    fun getUUID(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    private fun setupBroadcastReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                when (intent?.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                        val deviceName = device?.name
                        val deviceHardwareAddress = device?.address
                        Log.d("로그", "deviceName: $deviceName")
                        if (deviceName != null && deviceHardwareAddress != null) {
                            adapter[deviceName] = deviceHardwareAddress
                        }
                    }
                }
            }
        }
        intentFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(broadcastReceiver, intentFilter)
    }

    fun onDestroy() {
        context.unregisterReceiver(broadcastReceiver)
    }

    // 블루투스 지원 여부 반환 함수
    fun isBluetoothSupport(): Boolean {
        return bluetoothAdapter != null
    }

    // 기기와 페어링 가능한 블루투스 목록 가져오는 함수
    fun getPairedDevices() {
        bluetoothAdapter?.let {
            if (it.isEnabled) {
                adapter.clear()
                val pairedDevices: Set<BluetoothDevice> = it.bondedDevices
                if (pairedDevices.isNotEmpty()) {
                    pairedDevices.forEach { device ->
                        adapter[device.name] = device.address
                    }
                    showPairedDevicesDialog(pairedDevices) // 페어링 목록 다이얼로그 생성
                } else {
                    makeToast("페어링된 기기가 없습니다.")
                }
            } else {
                makeToast("블루투스가 비활성화 되어 있습니다.")
            }
        }
    }

    // // 블루투스가 OFF 이면 블루투스 ON 함수
    fun findDevice() {
        bluetoothAdapter?.let {
            if (it.isEnabled) {
               makeToast("블루투스가 켜져있습니다.")
                return
            } else {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                (context as Activity).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                adapter.clear()
                it.startDiscovery()
            }
        }
    }

    // 선택된 Bluetooth 기기에 연결 시도
    private fun connectDevice(device: BluetoothDevice) {
        bluetoothAdapter?.let { adapter ->
            if (adapter.isEnabled) {
                if (adapter.isDiscovering) {
                    adapter.cancelDiscovery()
                }
                try {
                    val uuid = UUID.fromString("00001101-0000-1000-8000-70dc7c4fffec5b05")
                    val thread = ConnectThread(uuid, device)
                    thread.run()
                    makeToast("${device.name}과 연결되었습니다.")
                } catch (e: Exception) {
                    makeToast("기기의 전원이 꺼져 있습니다. 기기를 확인해주세요.")
                }
            } else {
                makeToast("블루투스가 비활성화되어 있습니다")
            }
        }
    }

     //기기와 페어링된 Bluetooth 기기 목록 다이얼로그 표시
     private fun showPairedDevicesDialog(pairedDevices: Set<BluetoothDevice>) {
        if (pairedDevices.isNotEmpty()) {
            val deviceNames = pairedDevices.map { it.name }.toTypedArray()
            AlertDialog.Builder(context)
                .setTitle("페어링된 Bluetooth 목록")
                .setItems(deviceNames) { dialog, which ->
                    val selectedDevice = pairedDevices.elementAtOrNull(which)
                    selectedDevice?.let { device ->
                        connectDevice(device)
                    }
                    dialog.dismiss()
                }
                .show()
        } else {
            makeToast("페어링된 기기가 없습니다.")
        }
    }

    // 토스트 메시지 생성 함수
    private fun makeToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

}
