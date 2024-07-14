package com.example.elder_care.feature.action

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import kotlin.jvm.internal.RepeatableContainer

class ActionOutVPAdapter(
    private val fragmentList: ArrayList<Fragment>,
    container: AppCompatActivity
) : FragmentStateAdapter(container.supportFragmentManager, container.lifecycle) {

    override fun getItemCount(): Int = fragmentList.count()
    override fun createFragment(position: Int): Fragment = fragmentList[position]
}