package com.example.elder_care.feature.action

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cleanarchitecturestudy.core.ui.BaseFragment
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