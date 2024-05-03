package com.example.advizors.login


import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.advizors.R
import com.example.advizors.models.user.UserModel


class LogoutActivity : AppCompatActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_logout)

        UserModel.instance.logOff();

        startLoginActivity()
//        val i = Intent(getActivity(), LoginActivity::class.java)
//        startActivity(i)
//        (getActivity() as Activity?).overridePendingTransition(0, 0)

//        private fun loggedInHandler() {
//            Toast.makeText(
//                this@LoginActivity,
//                "Welcome ${auth.currentUser?.displayName}!",
//                Toast.LENGTH_SHORT
//            ).show()
//            val intent = Intent(this@LoginActivity, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
