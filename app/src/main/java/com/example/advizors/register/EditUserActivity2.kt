package com.example.advizors.register

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.advizors.R

class EditUserActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        // Add the fragment to the activity
        supportFragmentManager.beginTransaction()
            .replace(R.id.editContainerView, EditUserFragment())
            .commit()
    }
}