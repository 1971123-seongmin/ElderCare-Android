package com.example.elder_care.ui.install_guide

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.elder_care.R
import com.example.elder_care.base.BaseFragment
import com.example.elder_care.databinding.FragmentHubOnlineBinding
import com.example.elder_care.utils.extension.navigateSafe

class HubOnlineFragment : BaseFragment<FragmentHubOnlineBinding>(R.layout.fragment_hub_online) {

    override fun setLayout() {
        setButton()
    }

    private fun setButton() {
        binding.fragmentHomeGuideBtn.setOnClickListener {
            val action = HubDownLoadFragmentDirections.actionHubDownLoadFragmentToHubOnlineFragment()
            findNavController().navigateSafe(action.actionId)
        }
    }

}