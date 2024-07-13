package com.example.elder_care.feature

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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

    private lateinit var navController: NavController
    private lateinit var homeFragment: HomeFragment

    override fun setLayout() {
        setBottomNavigation()
    }

    private fun setBottomNavigation() {
        binding.mainBottomNavigationBar.itemIconTintList = null

        val host = supportFragmentManager.findFragmentById(binding.mainNavHostFragment.id) as NavHostFragment ?: return
        navController = host.navController
        binding.mainBottomNavigationBar.setupWithNavController(navController)

        // 최초 실행시 홈 화면
        binding.mainBottomNavigationBar.selectedItemId = R.id.homeFragment
        navController.navigate(R.id.homeFragment)
    }
}