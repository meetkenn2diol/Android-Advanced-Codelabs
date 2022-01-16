package com.android.example.aboutme

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.android.example.aboutme.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    //binding object
    private lateinit var binding: ActivityMainBinding

    //MyName.kt instance
    private val myName: MyName = MyName("Kennedy Grain")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.myName = myName

        binding.doneButton.setOnClickListener { addNickname() }
        binding.nicknameText.setOnClickListener { updateNickname() }


    }

    private fun addNickname() {
        binding.apply {
            //hide the EditText
            nicknameEdit.clearFocus()
            nicknameEdit.visibility = View.GONE

            //Make the nickname_text TextView visible after setting it's text
            myName?.nickname = nicknameEdit.text.toString()
            nicknameText.visibility = View.VISIBLE

            doneButton.visibility = View.GONE

            invalidateAll()
        }
        // Hide the keyboard.
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.doneButton.windowToken, 0)
    }

    private fun updateNickname() {
        binding.apply {
            //show the edit text, show the DONE button, and hide the text view.
            nicknameEdit.visibility = View.VISIBLE
            doneButton.visibility = View.VISIBLE
            nicknameText.visibility = View.GONE
            // Set the focus to the edit text.
            nicknameEdit.requestFocus()
        }

        // Show the keyboard.
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.nicknameEdit, 0)
    }
}