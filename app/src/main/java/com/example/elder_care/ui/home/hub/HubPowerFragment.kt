package com.example.elder_care.ui.home.hub

import androidx.navigation.fragment.findNavController
import com.example.elder_care.R
import com.example.elder_care.base.BaseFragment
import com.example.elder_care.databinding.FragmentHubPowerBinding
import com.example.elder_care.utils.extension.navigateSafe


class HubPowerFragment : BaseFragment<FragmentHubPowerBinding>(R.layout.fragment_hub_power) {

    override fun setLayout() {
        setButton()
    }

    private fun initSetting() {
        setButton()
    }

    private fun setButton() {
        binding.fragmentHomeGuideBtn.setOnClickListener {
            val action = HubPowerFragmentDirections.actionHubPowerFragmentToHubWiFiConnectionFragment()
            findNavController().navigateSafe(action.actionId)
        }
    }

}