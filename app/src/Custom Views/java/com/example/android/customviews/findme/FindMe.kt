package com.example.android.customviews.findme

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.example.android.customviews.BuildConfig
import com.example.android.customviews.R

private const val HOW_MANY_TIMES_SHOWN = "HOW_MANY_TIMES_SHOWN"

class FindMe : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_find_me)

        val dialog = createInstructionDialog()

        //TODO [my addition] the instruction dialog should only show once when the game starts for the first time
       if (shouldShowDialog()) {
            dialog.show()
        }
    }

    private fun shouldShowDialog(): Boolean {
        val prefs =getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        var timesShown = prefs.getInt(HOW_MANY_TIMES_SHOWN, 1)
        return when (timesShown) {
            1 -> {
                prefs.edit().putInt(HOW_MANY_TIMES_SHOWN, ++timesShown).apply()
                true
            }
            4 -> {
                prefs.edit().putInt(HOW_MANY_TIMES_SHOWN, 1).apply()
                false
            }
            else -> {
                prefs.edit().putInt(HOW_MANY_TIMES_SHOWN, ++timesShown).apply()
                false
            }
        }
    }

    private fun createInstructionDialog(): Dialog {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setIcon(R.drawable.android)
            setTitle(R.string.instructions_title)
            setMessage(R.string.instructions)
            setPositiveButtonIcon(
                ContextCompat.getDrawable(
                    this@FindMe,
                    android.R.drawable.ic_media_play
                )
            )
        }
        return builder.create()
    }
}