package com.google.samples.apps.trackmysleepquality.sleeptracker

import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import com.google.samples.apps.trackmysleepquality.R
import com.google.samples.apps.trackmysleepquality.convertDurationToFormatted
import com.google.samples.apps.trackmysleepquality.convertNumericQualityToString
import com.google.samples.apps.trackmysleepquality.database.SleepNight

/**An extension function on AppCompatTextView to format a sleep duration*/
@BindingAdapter("sleepDurationFormatted")
fun AppCompatTextView.setSleepDurationFormatted(item: SleepNight?) {
    //The null check(?.) is very important for BindingAdapter
    item?.let {
        text = convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, context.resources)
    }
}

/**An extension function on AppCompatTextView to format a sleep quality*/
@BindingAdapter("sleepQualityString")
fun AppCompatTextView.setSleepQualityString(item: SleepNight?) {
    //The null check(?.) is very important for BindingAdapter
    item?.let { text = convertNumericQualityToString(item.sleepQuality, context.resources) }
}

/**An extension function on AppCompatTextView sets the image on an image view*/
@BindingAdapter("sleepImage")
fun AppCompatImageView.setSleepImage(item: SleepNight?) {
    //The null check(?.) is very important for BindingAdapter
    item?.let {
        setImageResource(
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
}