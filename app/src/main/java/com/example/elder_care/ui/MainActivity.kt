package com.example.elder_care.ui

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.elder_care.R
import com.example.elder_care.base.BaseActivity
import com.example.elder_care.databinding.ActivityMainBinding
import com.example.elder_care.utils.permission.PermissionCheckList

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private lateinit var navController: NavController
    private lateinit var permissionList : List<String>
    private lateinit var requestMultiplePermissionsLauncher: ActivityResultLauncher<Array<String>>

    override fun setLayout() {
        checkPermissions()
        setBottomNavigation()
    }

    private fun setBottomNavigation() {
        val mainBottomNavigationBar = binding.mainBottomNavigationBar
        mainBottomNavigationBar.itemIconTintList = null

        val host =
            supportFragmentManager.findFragmentById(binding.mainNavHostFragment.id) as NavHostFragment
                ?: return
        navController = host.navController
        binding.mainBottomNavigationBar.setupWithNavController(navController)
    }

    // 권한 요청 결과 처리 런처 초기화 함수
    private fun initPermissionLaunchers() {
        requestMultiplePermissionsLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            Log.d("권한 런처", "Permissions result: $permissions")
            handlePermissionsResult(permissions)
        }
    }

    private fun checkPermissions() {
        permissionList = PermissionCheckList.permissions
        initPermissionLaunchers()
        checkPermissionsAndProceed()
    }

    // 권한 요청 결과 확인 -> 거부된 권한이 있는 경우 권한 거부 다이얼로그 표시 함수
    private fun handlePermissionsResult(permissions: Map<String, Boolean>) {
        val deniedPermissions = permissions.filter { !it.value }
        if (deniedPermissions.isNotEmpty()){
            showPermissionDeniedDialog()
        }
    }

    // 모든 권한이 허용되었는지 확인하고, 허용되지 않은 경우 권한 요청 다이얼로그 표시 함수
    private fun checkPermissionsAndProceed() {
        if (!areAllPermissionsGranted()) {
            showRequestDialog()
        }
    }

    // 권한 확인 함수
    private fun areAllPermissionsGranted(): Boolean {
        permissionList = PermissionCheckList.permissions
        return permissionList.all { permission ->
            ContextCompat.checkSelfPermission(this@MainActivity, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    // 권한 요청 생성 함수
    private fun askAllPermissions() {
        permissionList = PermissionCheckList.permissions
        requestMultiplePermissionsLauncher.launch(permissionList.toTypedArray())
    }

    // 시작시 권한 허용 다이얼로그 표시 함수
    private fun showRequestDialog() {
        AlertDialog.Builder(this)
            .setTitle("권한 필요")
            .setMessage("이 앱을 사용하려면 모든 권한을 허용해야 합니다.")
            .setPositiveButton("확인") { dialog, _ ->
                askAllPermissions()
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
                this@MainActivity.finish()
            }
            .setCancelable(false)
            .show()
    }

    // 권한 거부시 권한 요청 다이얼로그 (설정 앱으로 이동)
    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("권한 필요")
            .setMessage("필수 권한이 거부되었습니다. 설정에서 권한을 허용해 주세요.")
            .setPositiveButton("설정으로 이동") { dialog, _ ->
                dialog.dismiss()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent) // 설정 화면으로 이동
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
                this@MainActivity.finish()
            }
            .setCancelable(false)
            .show()
    }

}



