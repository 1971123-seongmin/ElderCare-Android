package com.example.elder_care.feature

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.CursorTreeAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.util.Util
import com.example.cleanarchitecturestudy.core.ui.BaseActivity
import com.example.elder_care.R
import com.example.elder_care.databinding.ActivityMainBinding
import com.example.elder_care.feature.home.HomeFragment
import com.example.elder_care.utils.ConnectThread
import java.util.UUID

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private val REQUEST_PERMISSIONS = 1
    private val permissions = listOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.RECORD_AUDIO
    )

    // 블루투스 관리
    private val bluetoothManager: BluetoothManager by lazy {
        getSystemService(BluetoothManager::class.java)
    }

    // 모든 Bluetooth 상호작용의 진입점
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        bluetoothManager.adapter
    }
    private val adapter = mutableMapOf<String, String>()

    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var intentFilter : IntentFilter

    private lateinit var navController: NavController

    override fun setLayout() {

        checkPermission(permissions)

        // Bluetooth 활성화 요청
        //setActivate()

        // 페어링된 디바이스 검색
        //getPairedDevices()

        // 주변 기기 검색
        //findDevice()

        setBottomNavigation()
        //isBluetoothSupport()


        // 블루투스 기기 검색 브로드캐스트
        //setupBroadcastReceiver()

        Log.d("로그", "uuid : ${getUUID(this)}")
    }

    fun getUUID(context : Context) : String {
        val uuid = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        return uuid
    }

    //  Bluetooth 기기 검색을 위한 BroadcastReceiver 설정 함수 -> 기기 이름, MAC 주소를 adapter에 저장
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
        registerReceiver(broadcastReceiver, intentFilter)
    }

    // BroadcastReceiver 등록해제
    override fun onDestroy() {
        unregisterReceiver(broadcastReceiver)
    }

    // 블루투스 지원 확인 함수
    private fun isBluetoothSupport():Boolean{
        return if(bluetoothAdapter ==null) {
            makeToast("Bluetooth 지원을 하지 않는 기기입니다.")
            false
        }else{
            makeToast("Bluetooth 지원 기기입니다.")
            true
        }
    }

    // 블루투스 활성화 요청 함수
    fun setActivate() {
        bluetoothAdapter?.let {

            if (!it.isEnabled) {
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(intent, REQUEST_PERMISSIONS)
            } else {
                getPairedDevices()
                findDevice()
            }
        }
    }

    // 페어링된 디바이스 검색
    fun getPairedDevices() {
        bluetoothAdapter?.let {

            if (it.isEnabled) {

                adapter.clear() // 저장된 Bluetooth 장치들을 초기화
                val pairedDevices: Set<BluetoothDevice> = it.bondedDevices // 페어링된 기기 확인
                Log.d("pairedDevices", "pairedDevices: $pairedDevices")

                // 페어링된 기기가 존재하는 경우
                if (pairedDevices.isNotEmpty()) {
                    pairedDevices.forEach { device ->
                        adapter[device.name] = device.address
                    }
                } else {
                    makeToast("페어링된 기기가 없습니다.")
                }
            } else {
                makeToast( "블루투스가 비활성화 되어 있습니다.")
            }
        }
    }

    // 주변 기기 검색
    fun findDevice() {
        bluetoothAdapter?.let {

            if (it.isEnabled) {
                // 현재 검색중이라면
                if (it.isDiscovering) {
                    // 검색 취소
                    it.cancelDiscovery()
                    makeToast( "기기검색이 중단되었습니다.")
                    return
                }

                // adapter 초기화 후 기기 검색 시작
                adapter.clear()
                it.startDiscovery()
                makeToast("기기 검색을 시작하였습니다")
            } else {
                makeToast("블루투스가 비활성화되어 있습니다")
            }
        }
    }

    // 디바이스에 연결
    private fun connectDevice(deviceAddress: String) {
        bluetoothAdapter?.let { adapter ->
            // 기기 검색을 수행중이라면 취소
            if (adapter.isDiscovering) {
                adapter.cancelDiscovery()
            }

            // 서버의 역할을 수행 할 Device 획득
            val device = adapter.getRemoteDevice(deviceAddress)
            // UUID 선언
            val uuid = UUID.fromString("00001101-0000-1000-8000-70dc7c4fffec5b05")
            try {
                val thread = ConnectThread(uuid, device)

                thread.run()
                makeToast( "${device.name}과 연결되었습니다.")
            } catch (e: Exception) { // 연결에 실패할 경우 호출됨
                makeToast("기기의 전원이 꺼져 있습니다. 기기를 확인해주세요.")
                return
            }
        }
    }

    // 블루투스 함수 X

    private fun setBottomNavigation() {
        binding.mainBottomNavigationBar.itemIconTintList = null

        val host =
            supportFragmentManager.findFragmentById(binding.mainNavHostFragment.id) as NavHostFragment
                ?: return
        navController = host.navController
        binding.mainBottomNavigationBar.setupWithNavController(navController)

        // 최초 실행시 홈 화면
        binding.mainBottomNavigationBar.selectedItemId = R.id.homeFragment
        navController.navigate(R.id.homeFragment)
    }

    // 권한 미 허용시 다이얼로그 표시 함수
    private fun permissionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage("앱 사용을 위해 필수인 근처, 마이크 및 위치 권한을 허용해 주세요.")
            setPositiveButton("설정으로 이동") { dialog, which ->
                // 설정 화면으로 이동
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            setNegativeButton("취소") { dialog, which ->
                makeToast("권한을 허용하지 않으면 앱 실행이 불가능합니다.")
                dialog.dismiss()
                finish()
            }
            setTitle("권한 요청")
            setCancelable(false) // 다이얼로그 바깥 영역을 터치해도 닫히지 않도록 설정
            create()
            show()
        }
    }

    private fun checkPermission(permissionList: List<String>) {

        val deniedPermissions = permissions.filter   {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_DENIED
        }

        if (deniedPermissions.isNotEmpty()) {
            Log.d("권한", "권한 거부됨, $deniedPermissions")
            ActivityCompat.requestPermissions(this, deniedPermissions.toTypedArray(), REQUEST_PERMISSIONS)
        } else {
            setActivate()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSIONS -> {
                permissionDialog()
            }
        }
    }

}

