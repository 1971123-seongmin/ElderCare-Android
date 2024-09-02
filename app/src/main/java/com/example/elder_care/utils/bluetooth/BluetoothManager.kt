package com.example.elder_care.utils.bluetooth

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
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import java.util.UUID

class BluetoothManager(private val context: Context) {

    private val REQUEST_ENABLE_BT=1
    private val adapter = mutableMapOf<String, String>()

    private val bluetoothManager: BluetoothManager by lazy {
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        bluetoothManager.adapter
    }

    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var intentFilter: IntentFilter
    private lateinit var bluetoothDataViewModel: BluetoothDataViewModel

    // BluetoothDataViewModel 설정
    fun setViewModel(viewModel: BluetoothDataViewModel) {
        bluetoothDataViewModel = viewModel
    }

    init {
        setupBroadcastReceiver()
    }

    private fun setupBroadcastReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun onReceive(c: Context?, intent: Intent?) {
                when (intent?.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        // 권한이 있는지 확인
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            // getParcelableExtra(String, Class<T>) 사용
                            val device = intent?.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                            val deviceName = device?.name
                            val deviceHardwareAddress = device?.address
                            Log.d("로그", "deviceName: $deviceName")
                            if (deviceName != null && deviceHardwareAddress != null) {
                                adapter[deviceName] = deviceHardwareAddress
                            }
                        } else {
                            Log.w("로그", "권한이 없어 기기 이름을 가져올 수 없습니다.")
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
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
                    makeToast("위치 권한이 필요합니다.")
                }
            } else {
                makeToast("블루투스가 비활성화 되어 있습니다.")
            }
        }
    }

    // 블루투스가 OFF 이면 블루투스 ON 함수
    fun findDevice() {
        bluetoothAdapter?.let {
            if (it.isEnabled) {
                makeToast("블루투스가 켜져있습니다.")
                return
            } else {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    (context as Activity).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                    adapter.clear()
                    it.startDiscovery()
                } else {
                    makeToast("위치 권한이 필요합니다.")
                }
            }
        }
    }


    // 선택된 Bluetooth 기기에 연결 시도
    private fun connectDevice(device: BluetoothDevice) {
        bluetoothAdapter?.let { adapter ->
            if (adapter.isEnabled) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (adapter.isDiscovering) {
                        adapter.cancelDiscovery()
                    }
                    try {
                        val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
                        val thread = ConnectThread(uuid, device, bluetoothDataViewModel)
                        thread.start()
                        makeToast("${device.name}과 연결되었습니다.")
                    } catch (e: Exception) {
                        makeToast("기기의 전원이 꺼져 있습니다. 기기를 확인해주세요.")
                    }
                } else {
                    makeToast("위치 권한이 필요합니다.")
                }
            } else {
                makeToast("블루투스가 비활성화되어 있습니다")
            }
        }
    }

    //기기와 페어링된 Bluetooth 기기 목록 다이얼로그 표시
    private fun showPairedDevicesDialog(pairedDevices: Set<BluetoothDevice>) {
        if (pairedDevices.isNotEmpty()) {
            // 권한이 있는지 확인
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                val deviceNames = pairedDevices.map { it.name }.toTypedArray()
                AlertDialog.Builder(context)
                    .setTitle("페어링된 Bluetooth 목록")
                    .setItems(deviceNames) { dialog, which ->
                        val selectedDevice = pairedDevices.elementAtOrNull(which)
                        selectedDevice?.let { device ->
                            dialog.dismiss()
                            connectDevice(device)
                        }
                    }
                    .setNegativeButton("취소") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            } else {
                makeToast("위치 권한이 필요합니다.")
            }
        } else {
            makeToast("페어링된 기기가 없습니다.")
        }
    }

    // 토스트 메시지 생성 함수
    private fun makeToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

}
