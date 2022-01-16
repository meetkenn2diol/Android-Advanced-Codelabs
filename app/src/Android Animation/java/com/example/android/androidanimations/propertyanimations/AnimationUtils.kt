package com.example.android.androidanimations.propertyanimations

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View

/**
 * An extension function for [ObjectAnimator] to disable the view that started the animation
 */
fun ObjectAnimator.disableViewDuringAnimation(view: View) {
    addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator?) {
            view.isEnabled = false
        }

        override fun onAnimationEnd(animation: Animator?) {
            view.isEnabled = true
        }
    })
}

/**
 * An extension function for setting common animation property such as duration,count,...
 */
fun ObjectAnimator.applyBasicAnimatorProperty() {
    repeatCount = 1
    repeatMode = ObjectAnimator.REVERSE
    duration = 2000L
}