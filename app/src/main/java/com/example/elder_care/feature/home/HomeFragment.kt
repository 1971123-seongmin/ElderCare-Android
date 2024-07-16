package com.example.elder_care.feature.home

import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import com.example.cleanarchitecturestudy.core.ui.BaseFragment
import com.example.elder_care.R
import com.example.elder_care.databinding.FragmentHomeBinding
import com.example.elder_care.utils.bluetooth.BluetoothDataViewModel
import com.example.elder_care.utils.bluetooth.BluetoothManager

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private lateinit var menuHost: MenuHost
    private lateinit var bluetoothManager: BluetoothManager
    private val bluetoothDataViewModel by activityViewModels<BluetoothDataViewModel>()

    override fun setLayout() {
        setCustomToolbar()
        setBlueTooth()
    }

    private fun setBlueTooth() {

        bluetoothManager = BluetoothManager(requireContext())
        bluetoothManager.setViewModel(bluetoothDataViewModel)

        if (bluetoothManager.isBluetoothSupport()) {

            // 블루투스 OFF 이면 ON -> 페어링 가능한 블루투스 목록만 가져옴 (페어링 된 블루투스 목록은 못가져옴)
            binding.homeFindBluetoothBtn.setOnClickListener {
                bluetoothManager.findDevice()
            }

            // 블루투스 페어링 목록 검색 (블루투스 ON)
            binding.homeGetBluetoothBtn.setOnClickListener {
                bluetoothManager.getPairedDevices()
            }
            val data = bluetoothDataViewModel.getConnectedDeviceData()
            Log.d("로그", "센서 값 : $data")
        }
    }

    private fun setCustomToolbar() {

        menuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.home_toolbar_menu -> {
                        Toast.makeText(requireContext(), "메뉴 버튼", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

}