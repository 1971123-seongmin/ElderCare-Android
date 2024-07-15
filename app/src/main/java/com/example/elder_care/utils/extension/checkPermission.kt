package com.example.elder_care.utils.extension

import android.app.Activity
import android.content.pm.PackageManager.PERMISSION_GRANTED

fun Activity.checkPermission(vararg permission: String): Boolean {
    return permission.all { checkSelfPermission(it) == PERMISSION_GRANTED }
}