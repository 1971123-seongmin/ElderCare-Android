package com.example.elder_care.ui.install_guide

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.elder_care.R
import com.example.elder_care.base.BaseFragment
import com.example.elder_care.databinding.FragmentHubNetworkSelectionBinding
import com.example.elder_care.utils.extension.navigateSafe
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HubNetworkSelectionFragment : BaseFragment<FragmentHubNetworkSelectionBinding>(R.layout.fragment_hub_network_selection) {

    override fun setLayout() {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(4000) // 4초 대기
            setButton()
        }
    }

    private fun setButton() {
        val action = HubNetworkSelectionFragmentDirections.actionHubNetworkSelectionFragmentToHubDownLoadFragment()
        findNavController().navigateSafe(action.actionId)
    }

}