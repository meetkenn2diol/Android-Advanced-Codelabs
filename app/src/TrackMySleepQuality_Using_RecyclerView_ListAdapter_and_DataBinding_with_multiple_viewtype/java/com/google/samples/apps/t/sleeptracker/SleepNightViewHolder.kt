package com.google.samples.apps.trackmysleepquality.sleeptracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.samples.apps.trackmysleepquality.database.SleepNight
import com.google.samples.apps.trackmysleepquality.databinding.ListItemSleepNightBinding

class SleepNightViewHolder private constructor(private val binding: ListItemSleepNightBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: SleepNight, clickListener: SleepNightListener) {
        binding.sleep = item
        binding.clickListener = clickListener
        binding.executePendingBindings()//This call is an optimization that asks data binding to execute any pending bindings right away
    }

    companion object {
        fun from(parent: ViewGroup): SleepNightViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ListItemSleepNightBinding.inflate(layoutInflater, parent, false)
            return SleepNightViewHolder(binding)
        }
    }
}
