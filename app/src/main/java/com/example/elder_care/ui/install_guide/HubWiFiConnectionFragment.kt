package com.example.elder_care.ui.install_guide

import androidx.navigation.fragment.findNavController
import com.example.elder_care.R
import com.example.elder_care.base.BaseFragment
import com.example.elder_care.databinding.FragmentHubWifiConnectionBinding
import com.example.elder_care.utils.extension.navigateSafe

class HubWiFiConnectionFragment : BaseFragment<FragmentHubWifiConnectionBinding>(R.layout.fragment_hub_wifi_connection) {

    override fun setLayout() {
        setButton()
    }

    private fun initSetting() {
        setButton()
    }

    private fun setButton() {
        binding.fragmentHomeGuideBtn.setOnClickListener {
            val action = HubWiFiConnectionFragmentDirections.actionHubWiFiConnectionFragmentToHubNetworkSelectionFragment()
            findNavController().navigateSafe(action.actionId)
        }
    }

}