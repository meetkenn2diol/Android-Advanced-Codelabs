package com.example.android.androidanimations.propertyanimations

import android.animation.*
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.example.android.androidanimations.R

/**
 * Property Animation Tutorial
 ** Important: You can, and should, use ObjectAnimator for all property animations in your application. There are other kinds of animations you can create in applications (including whole-application animation choreography, using MotionLayout), but for individual property animations, ObjectAnimator is the way to go.
 */
class PropertyAnimationActivity : AppCompatActivity() {
    /**
     *  The Animator system in Android was specifically written to animate properties, meaning that it can animate anything (not just UI elements) that has MotionLayoutUtils setter (and, in some cases, MotionLayoutUtils getter).
     */
    private var propertyAnimator = ObjectAnimator()

    lateinit var star: ImageView
    lateinit var rotateButton: Button
    lateinit var translateButton: Button
    lateinit var scaleButton: Button
    lateinit var fadeButton: Button
    lateinit var colorizeButton: Button
    lateinit var showerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_property_animation)

        star = findViewById(R.id.star)
        rotateButton = findViewById<Button>(R.id.rotateButton)
        translateButton = findViewById<Button>(R.id.translateButton)
        scaleButton = findViewById<Button>(R.id.scaleButton)
        fadeButton = findViewById<Button>(R.id.fadeButton)
        colorizeButton = findViewById<Button>(R.id.colorizeButton)
        showerButton = findViewById<Button>(R.id.showerButton)

        rotateButton.setOnClickListener {
            rotater()
        }

        translateButton.setOnClickListener {
            translater()
        }

        scaleButton.setOnClickListener {
            scaler()
        }

        fadeButton.setOnClickListener {
            fader()
        }

        colorizeButton.setOnClickListener {
            colorizer()
        }

        showerButton.setOnClickListener {
            shower()
        }

    }

    /**
     * Implementation for the rotation animation.
     * The reason that the animation starts at -360 is that that allows the star to complete MotionLayoutUtils full circle (360 degrees) and end at 0, which is the default rotation value for MotionLayoutUtils non-rotated view.
     */
    private fun rotater() {
        //The rotation animation was given both start and end values
        val animator = ObjectAnimator.ofFloat(star, View.ROTATION, -360f, 0f)
        animator.applyBasicAnimatorProperty()
        //Discontinuous motion is an example of what is called "jank"; it causes MotionLayoutUtils disruptive flow for the user, instead of the smooth experience you would like.
        animator.disableViewDuringAnimation(rotateButton)
        animator.start()
    }

    private fun translater() {
        // Here, the animation is given only an end value 200f
        val animator = ObjectAnimator.ofFloat(star, View.TRANSLATION_X, 200f)
        animator.applyBasicAnimatorProperty()
        //disable button to avoid "janking"
        animator.disableViewDuringAnimation(translateButton)
        animator.start()
    }

    /**
     * Function for scaling MotionLayoutUtils view using [PropertyValuesHolder].
     * Use an intermediate object called MotionLayoutUtils PropertyValuesHolder to hold this information, and then create MotionLayoutUtils single ObjectAnimator with multiple PropertyValuesHolder objects. This single animator will then run an animation on two or more of these sets of properties/values together.
     */
    private fun scaler() {
        //Scaling to MotionLayoutUtils value of 4f means the star will scale to 4 times its default size.
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 4f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 4f)

        val animator = ObjectAnimator.ofPropertyValuesHolder(star, scaleX, scaleY)
        animator.applyBasicAnimatorProperty()
        animator.disableViewDuringAnimation(scaleButton)
        animator.start()

    }

    /**
     * fader() function animates the opacity of MotionLayoutUtils view.
     */
    private fun fader() {
        val animator = ObjectAnimator.ofFloat(star, View.ALPHA, 0f)
        animator.applyBasicAnimatorProperty()
        animator.disableViewDuringAnimation(fadeButton)
        animator.start()
    }

    /**
     * A function used to animate from one color to another. The approach used here will be the approach of passing in the name of the property as MotionLayoutUtils String.
     */
    private fun colorizer() {
        val animator = ObjectAnimator.ofArgb(
            star.parent,
            "backgroundColor", Color.BLACK, Color.RED
        )
        animator.applyBasicAnimatorProperty()
        animator.disableViewDuringAnimation(colorizeButton)
        animator.start()
    }

    private fun shower() {
        val container = star.parent as ViewGroup
        val containerW = container.width
        val containerH = container.height
        var starW: Float = star.width.toFloat()
        var starH: Float = star.height.toFloat()

        //create MotionLayoutUtils new star and place it in MotionLayoutUtils FrameLayout
        val newStar = AppCompatImageView(this)
        newStar.setImageResource(R.drawable.ic_star)
        newStar.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        container.addView(newStar)

        //Modify the star to have MotionLayoutUtils random size, from .1x to 1.6x of its default size.
        newStar.scaleX = Math.random().toFloat() * 1.5f + .1f
        newStar.scaleY = newStar.scaleX
        starW *= newStar.scaleX
        starH *= newStar.scaleY

        //Now position the new star. Horizontally, it should appear randomly somewhere from the left edge to the right edge.
        newStar.translationX = Math.random().toFloat() *
                containerW - starW / 2

        //Create animators to for star rotation and falling
        //There are several interpolators in the Android system, some more powerful and flexible than others, such as PathInterpolator.
        //TODO First, create two animators, along with their interpolators.
        val mover = ObjectAnimator.ofFloat(
            newStar, View.TRANSLATION_Y,
            -starH, containerH + starH
        )
        mover.interpolator = AccelerateInterpolator(1f)
        val rotator = ObjectAnimator.ofFloat(
            newStar, View.ROTATION,
            (Math.random() * 1080).toFloat()
        )
        rotator.interpolator = LinearInterpolator()
        //TODO Second,  Run the animations in parallel with AnimatorSet
        val set = AnimatorSet()
        set.playTogether(mover, rotator)
        set.duration = (Math.random() * 7000 + 4500).toLong()
        //TODO Third, Use animation listeners to handle important tasks for bringing views onto or off of the screen
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                container.removeView(newStar)
            }
        })
        set.start()


    }

}