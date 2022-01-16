/*
 *   Copyright (C) 2019 The Android Open Source Project
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.example.android.androidanimations.motionlayout

import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import com.example.android.androidanimations.R

/**
 * A class that allows you build an animation that moves a view from the top start of the screen to the bottom end in response to user clicks.
 *
 * To create an animation from the starter code, you'll need the following major pieces:
*A MotionLayout, which is a subclass of ConstraintLayout. You specify all the views to be animated inside the MotionLayout tag.
*A MotionScene, which is an XML file that describes an animation for MotionLayout.
*A Transition, which is part of the MotionScene that specifies the animation duration, trigger, and how to move the views.
*A ConstraintSet that specifies both the start and the end constraints of the transition.
 */
class Step1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step1)
    }
}
