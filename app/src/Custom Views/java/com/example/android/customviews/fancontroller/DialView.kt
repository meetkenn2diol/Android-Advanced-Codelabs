package com.example.android.customviews.fancontroller

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.content.withStyledAttributes
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.example.android.customviews.R
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

private enum class FanSpeed(val label: Int) {
    OFF(R.string.fan_off),
    LOW(R.string.fan_low),
    MEDIUM(R.string.fan_medium),
    HIGH(R.string.fan_high);

    fun next() = when (this) {
        OFF -> LOW
        LOW -> MEDIUM
        MEDIUM -> HIGH
        HIGH -> OFF
    }
}

private const val RADIUS_OFFSET_LABEL = 15
private const val RADIUS_OFFSET_INDICATOR = -20

/**
 * A custom view
 * In order to properly draw a custom view that extends View, you need to:
 * * Calculate the view's size when it first appears, and each time that view's size changes, by overriding the onSizeChanged() method.
 * * Override the onDraw() method to draw the custom view, using a Canvas object styled by a Paint object.
 * * Call the invalidate() method when responding to a user click that changes how the view is drawn to invalidate the entire view, thereby forcing a call to onDraw() to redraw the view.
 */
class DialView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var radius = 0.0f                   // Radius of the circle.
    private var fanSpeed = FanSpeed.OFF         // The active selection.

    // position variable which will be used to draw label and indicator circle position
    private val pointPosition: PointF = PointF(0.0f, 0.0f)

    //these styles are initialized here to help speed up the drawing step.
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 25.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    //TODO  declare variables to cache attribute values
    private var fanSpeedLowColor = 0
    private var fanSpeedMediumColor = 0
    private var fanSpeedMaxColor = 0

    init {
        isClickable = true

        context.withStyledAttributes(attrs, R.styleable.DialView) {
            fanSpeedLowColor = getColor(R.styleable.DialView_fanColor1, 0)
            fanSpeedMediumColor = getColor(R.styleable.DialView_fanColor2, 0)
            fanSpeedMaxColor = getColor(R.styleable.DialView_fanColor3, 0)
        }

        //add this to immediatly activate talkback
        updateContentDescription()

        //TODO  You add information about the view's action (here, a click or tap action) to an accessibility node info object, by way of an accessibility delegate.
        ViewCompat.setAccessibilityDelegate(this, object : AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityNodeInfo(host: View,
                                                           info: AccessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                val customClick = AccessibilityNodeInfoCompat.AccessibilityActionCompat(
                    AccessibilityNodeInfo.ACTION_CLICK,
                    context.getString(if (fanSpeed != FanSpeed.HIGH)
                        R.string.change else R.string.reset
                    )
                )

                //use the addAction() method to add the new accessibility action to the node info object.
                info.addAction(customClick)
            }
        })
   }

    override fun performClick(): Boolean {
        //The call to super.performClick() must happen first, which enables accessibility events as well as calls onClickListener().
        if (super.performClick()) return true

        fanSpeed = fanSpeed.next()
        updateContentDescription()


        invalidate()
        return true
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        //TODO to calculate the current radius of the dial's circle element.
        radius = (min(width, height) / 2.0 * 0.8).toFloat()
    }

    /**
     *  This extension function on the PointF class calculates the X, Y coordinates on the screen for the text label and current indicator (0, 1, 2, or 3), given the current FanSpeed position and radius of the dial.
     */
    private fun PointF.computeXYForSpeed(pos: FanSpeed, radius: Float) {
        // Angles are in radians.
        val startAngle = Math.PI * (9 / 8.0)
        val angle = startAngle + pos.ordinal * (Math.PI / 4)
        x = (radius * cos(angle)).toFloat() + width / 2
        y = (radius * sin(angle)).toFloat() + height / 2
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Set dial background color to green if selection not off.
        paint.color = when (fanSpeed) {
            FanSpeed.OFF -> Color.GRAY
            FanSpeed.LOW -> fanSpeedLowColor
            FanSpeed.MEDIUM -> fanSpeedMediumColor
            FanSpeed.HIGH -> fanSpeedMaxColor
        }
        // Draw the dial.
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius, paint)
        // Draw the indicator circle.
        val markerRadius = radius + RADIUS_OFFSET_INDICATOR
        pointPosition.computeXYForSpeed(fanSpeed, markerRadius)
        paint.color = Color.BLACK

        canvas.drawCircle(pointPosition.x, pointPosition.y, radius / 12, paint)
        // Draw the text labels.
        val labelRadius = radius + RADIUS_OFFSET_LABEL
        for (i in FanSpeed.values()) {
            pointPosition.computeXYForSpeed(i, labelRadius)
            val label = resources.getString(i.label)
            canvas.drawText(label, pointPosition.x, pointPosition.y, paint)
        }
    }

    private fun updateContentDescription() {
        contentDescription = resources.getString(fanSpeed.label)
    }
}