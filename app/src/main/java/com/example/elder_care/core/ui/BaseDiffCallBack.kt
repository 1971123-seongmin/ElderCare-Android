package com.example.elder_care.core.ui

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

class BaseDiffCallBack<T:Any>() : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem == newItem

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem == newItem
}
