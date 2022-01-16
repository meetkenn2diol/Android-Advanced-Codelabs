package com.example.android.customviews.minipaint

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.os.Parcelable
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.example.android.customviews.R

private const val STROKE_WIDTH = 12f // has to be float

class MyCanvasView(context: Context) : View(context) {

    private val drawColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)

    // Set up the paint with which to draw.
    private val paint = Paint().apply {
        color = drawColor
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        //Dithering is the process of juxtaposing pixels of two colors to create the illusion that a third color is present.
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
    }

    //object to store the path that is being drawn when following the user's touch on the screen
    private var path = Path()

    //Call them extraCanvas and extraBitmap. These are your bitmap and canvas for caching what has been drawn before.
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap

    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)

    // motionTouchEventX and motionTouchEventY variables for caching the x and y coordinates of the current touch event (the MotionEvent coordinates). Initialize them to 0f.
    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    //add variables to cache the latest x and y values.After the user stops moving and lifts their touch, these are the starting point for the next path (the next segment of the line to draw).
    private var currentX = 0f
    private var currentY = 0f

    /**
     *Defines the touch tolerance.Using a path, there is no need to draw every pixel and each time request a refresh of the display. Instead, you can (and will) interpolate a path between points for much better performance.
     * * If the finger has barely moved, there is no need to draw.
     * * If the finger has moved less than the touchTolerance distance, don't draw.
     * * [scaledTouchSlop] returns the distance in pixels a touch can wander before the system thinks the user is scrolling.
     */
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    //holds a rectangle reference
    private lateinit var frame: Rect


    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        if (::extraBitmap.isInitialized) extraBitmap.recycle() //Do this to avoid memory leak on oldWidth,oldHeight

        // ARGB_8888 stores each color in 4 bytes and is recommended.
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(backgroundColor)

        // Calculate a rectangular frame around the picture.
        val inset = 40
        frame = Rect(inset, inset, width - inset, height - inset)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)

        // Draw a frame around the canvas.
        canvas.drawRect(frame, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }
        return true
    }

    private fun touchStart() {
        path.reset()
        path.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    private fun touchMove() {
        val dx = Math.abs(motionTouchEventX - currentX)
        val dy = Math.abs(motionTouchEventY - currentY)
        if (dx >= touchTolerance || dy >= touchTolerance) {
            // QuadTo() adds a quadratic bezier from the last point,
            // approaching control point (x1,y1), and ending at (x2,y2).
            //Using quadTo() instead of lineTo() create a smoothly drawn line without corners. See Bezier Curves
            path.quadTo(
                currentX,
                currentY,
                (motionTouchEventX + currentX) / 2,
                (motionTouchEventY + currentY) / 2
            )
            currentX = motionTouchEventX
            currentY = motionTouchEventY
            // Draw the path in the extra bitmap to cache it.
            extraCanvas.drawPath(path, paint)
        }

        //force redrawing of the screen with the updated path
        invalidate()
    }

    private fun touchUp() {
        // Reset the path so it doesn't get drawn again.
        path.reset()
    }

}