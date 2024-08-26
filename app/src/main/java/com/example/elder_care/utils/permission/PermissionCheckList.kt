package com.example.elder_care.utils.permission

import android.Manifest
import android.os.Build

object PermissionCheckList {
    val permissions = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> { // Android 13 (API 33) 이상
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.RECORD_AUDIO
                // Manifest.permission.ACCESS_BACKGROUND_LOCATION // 별도 권한 요청 필요
            )
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> { // Android 12 (API 31) 부터 Android 12L (API 32)까지
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.RECORD_AUDIO
                // Manifest.permission.ACCESS_BACKGROUND_LOCATION // 별도 권한 요청 필요
            )
        }
        Build.VERSION.SDK_INT > Build.VERSION_CODES.P -> { // Android 10 (API 29) 부터 Android 11 (API 30)까지
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.RECORD_AUDIO,
                // Manifest.permission.ACCESS_BACKGROUND_LOCATION // 별도 권한 요청 필요
            )
        }
        else -> { // Android 9 (API 28) 이하
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.RECORD_AUDIO
            )
        }
    }

}