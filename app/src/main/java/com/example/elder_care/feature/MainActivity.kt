package com.example.elder_care.feature

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
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.CursorTreeAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
        setBottomNavigation()
    }

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
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_DENIED
        }

        if (deniedPermissions.isNotEmpty()) {
            Log.d("권한", "권한 거부됨, $deniedPermissions")
            ActivityCompat.requestPermissions(this, deniedPermissions.toTypedArray(), REQUEST_PERMISSIONS)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_PERMISSIONS ->
                // 권한 요청 결과 확인
                for (i in permissions.indices) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        // 하나 이상의 권한이 거부된 경우
                        permissionDialog()
                        return
                   }
                }
            }
        }
}



