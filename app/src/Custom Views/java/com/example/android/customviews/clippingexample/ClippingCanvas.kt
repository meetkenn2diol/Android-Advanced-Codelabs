package com.example.android.customviews.clippingexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.android.customviews.R

class ClippingCanvas : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ClippedView(this))
    }
}