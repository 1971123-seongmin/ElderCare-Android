package com.example.elder_care.ui.home

import androidx.navigation.fragment.findNavController
import com.example.elder_care.R
import com.example.elder_care.base.BaseFragment
import com.example.elder_care.databinding.FragmentHubPlacementBinding
import com.example.elder_care.utils.extension.navigateSafe

class HubPlacementFragment : BaseFragment<FragmentHubPlacementBinding>(R.layout.fragment_hub_placement) {

    override fun setLayout() {
        setButton()
    }

    private fun initSetting() {
        setButton()
    }

    private fun setButton() {
        binding.fragmentHomeGuideBtn.setOnClickListener {
            val action = HubPlacementFragmentDirections.actionHubPlacementFragmentToHubPowerFragment()
            findNavController().navigateSafe(action.actionId)
        }
    }

}