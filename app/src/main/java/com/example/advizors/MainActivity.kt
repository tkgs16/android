package com.example.advizors

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.ComponentActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // init drawer layout
        drawerLayout = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar);
        // init action bar drawer toggle
        // add a drawer listener into  drawer layout

        // show menu icon and back icon while drawer open
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // check conndition for drawer item with menu item
//        return if (actionBarDrawerToggle.onOptionsItemSelected(item)){
//            true
//        }else{
//            super.onOptionsItemSelected(item)
//        }
//
//    }
}

