package com.example.elder_care.ui.home

import androidx.navigation.fragment.findNavController
import com.example.elder_care.R
import com.example.elder_care.base.BaseFragment
import com.example.elder_care.databinding.FragmentHubInfoBinding
import com.example.elder_care.utils.extension.navigateSafe

class HubInfoFragment : BaseFragment<FragmentHubInfoBinding>(R.layout.fragment_hub_info) {

    override fun setLayout() {
        initSetting()
    }

    private fun initSetting() {
        setButton()
    }

    private fun setButton() {
        binding.fragmentHomeGuideBtn.setOnClickListener {
            val action = HubInfoFragmentDirections.actionHubInfoFragmentToHubPlacementFragment()
            findNavController().navigateSafe(action.actionId)
        }
    }

}