package com.example.elder_care.feature

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.cleanarchitecturestudy.core.ui.BaseActivity
import com.example.elder_care.R
import com.example.elder_care.databinding.ActivityMainBinding
import com.example.elder_care.feature.home.HomeFragment

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private val REQUEST_PERMISSIONS = 1
    private val permissions = listOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.RECORD_AUDIO
    )

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

    private fun checkPermission(permissionList: List<String>) {
        val requestList = ArrayList<String>()

        // 권한 허용여부 확인
        for (permission in permissionList) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestList.add(permission)
            }
        }

        if (requestList.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, requestList.toTypedArray(), REQUEST_PERMISSIONS)
            false // 권한 요청 필요, 즉시 false 반환
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSIONS) {

            val deniedPermission = ArrayList<String>()

            // 사용자가 거부한 권한을 찾음
            for ((index, result) in grantResults.withIndex()) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    deniedPermission.add(permissions[index])
                }
            }

            if (deniedPermission.isNotEmpty()) {
                for (permission in deniedPermission) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {

                        Toast.makeText(
                            this, "앱 사용을 위해 필수인 근처, 마이크 및 위치 권한을 허용해 주세요.", Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                        return
                    }
                }
            }
        }
    }

}

