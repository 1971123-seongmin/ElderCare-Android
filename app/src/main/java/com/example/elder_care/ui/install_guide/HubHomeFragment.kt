package com.example.elder_care.ui.install_guide

import android.util.Log
import androidx.core.view.MenuHost
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.elder_care.R
import com.example.elder_care.base.BaseFragment
import com.example.elder_care.databinding.FragmentHubHomeBinding
import com.example.elder_care.utils.bluetooth.BluetoothDataViewModel
import com.example.elder_care.utils.bluetooth.BluetoothManager
import com.example.elder_care.utils.extension.navigateSafe

class HubHomeFragment : BaseFragment<FragmentHubHomeBinding>(R.layout.fragment_hub_home) {

    private lateinit var menuHost: MenuHost
    private lateinit var bluetoothManager: BluetoothManager
    private val bluetoothDataViewModel by activityViewModels<BluetoothDataViewModel>()

    override fun setLayout() {
        initSetting()
    }

    private fun initSetting() {
        setButton()
        setBlueTooth()
    }

    private fun setButton() {
        binding.fragmentHomeGuideBtn.setOnClickListener {
            val action = HubHomeFragmentDirections.actionHubHomeFragmentToHubInfoFragment()
            findNavController().navigateSafe(action.actionId)
        }
    }

    private fun setBlueTooth() {

        bluetoothManager = BluetoothManager(requireContext())
        bluetoothManager.setViewModel(bluetoothDataViewModel)

        if (bluetoothManager.isBluetoothSupport()) {

            // 블루투스 OFF 이면 ON -> 페어링 가능한 블루투스 목록만 가져옴 (페어링 된 블루투스 목록은 못가져옴)
            binding.bluetoothBtn.setOnClickListener {
                bluetoothManager.findDevice()
            }

            // 블루투스 페어링 목록 검색 (블루투스 ON)
            binding.bluetoothGetBtn.setOnClickListener {
                bluetoothManager.getPairedDevices()
            }
            val data = bluetoothDataViewModel.getConnectedDeviceData()
            Log.d("로그", "센서 값 : $data")
        }
    }

}