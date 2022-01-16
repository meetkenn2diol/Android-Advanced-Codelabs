/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.androidtrivia

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.samples.apps.androidtrivia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    //data binding variable
    private lateinit var binding: ActivityMainBinding
    //object to register the drawerlayout in the activity_main.xml
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("UNUSED_VARIABLE")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        drawerLayout = binding.drawerLayout


        //region Add support for an Up button. Ensure to override onSupportNavigateUp()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.my_Nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        //Connect the drawer to the navigation controller
        NavigationUI.setupWithNavController(binding.navView, navController)
        // link the navigation controller to the app bar
        //Set up the drawer button in the app bar
        NavigationUI.setupActionBarWithNavController(this,navController,drawerLayout)
        //endregion


    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.my_Nav_host_fragment)
        return NavigationUI.navigateUp(navController,drawerLayout)
    }



    // TODO (01) Create the new TitleFragment
    // Select File->New->Fragment->Fragment (Blank)

    // TODO (02) Clean up the new TitleFragment
    // In our new TitleFragment

    // TODO (03) Use DataBindingUtil.inflate to inflate and return the titleFragment in onCreateView
    // In our new TitleFragment
    // R.layout.fragment_title
}
