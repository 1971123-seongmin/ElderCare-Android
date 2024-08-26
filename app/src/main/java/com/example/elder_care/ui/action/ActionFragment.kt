package com.example.elder_care.ui.action

import com.example.elder_care.base.BaseFragment
import com.example.elder_care.R
import com.example.elder_care.databinding.FragmentActionBinding

class ActionFragment : BaseFragment<FragmentActionBinding>(R.layout.fragment_action) {

    override fun setLayout() {
        setToolbar()
    }

    private fun setToolbar() {
        binding.titleText = "활동"
    }
}