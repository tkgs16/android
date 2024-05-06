package com.example.advizors.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.advizors.R

class LogoutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logout)

        // Add the fragment to the activity
        supportFragmentManager.beginTransaction()
            .replace(R.id.logoutContainerView, LogoutFragment())
            .commit()
    }
}