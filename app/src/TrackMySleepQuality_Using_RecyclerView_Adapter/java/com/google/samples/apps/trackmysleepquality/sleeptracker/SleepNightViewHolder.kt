package com.google.samples.apps.trackmysleepquality.sleeptracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.google.samples.apps.trackmysleepquality.R
import com.google.samples.apps.trackmysleepquality.convertDurationToFormatted
import com.google.samples.apps.trackmysleepquality.convertNumericQualityToString
import com.google.samples.apps.trackmysleepquality.database.SleepNight

class SleepNightViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val sleepLength: AppCompatTextView = itemView.findViewById(R.id.sleep_length)
    val quality: AppCompatTextView = itemView.findViewById(R.id.quality_string)
    val qualityImage: AppCompatImageView = itemView.findViewById(R.id.quality_image)

    fun bind(item: SleepNight) {
        val res = itemView.context.resources
        sleepLength.text =
            convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, res)
        quality.text = convertNumericQualityToString(item.sleepQuality, res)
        qualityImage.setImageResource(
            when (item.sleepQuality) {
                0 -> R.drawable.ic_sleep_0
                1 -> R.drawable.ic_sleep_1
                2 -> R.drawable.ic_sleep_2
                3 -> R.drawable.ic_sleep_3
                4 -> R.drawable.ic_sleep_4
                5 -> R.drawable.ic_sleep_5
                else -> R.drawable.ic_sleep_active
            }
        )
    }

    companion object {
        fun from(parent: ViewGroup): SleepNightViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater
                .inflate(R.layout.list_item_sleep_night, parent, false)
            return SleepNightViewHolder(view)
        }
    }
}
