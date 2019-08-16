package com.majorik.sparklinelayout

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sparkline.setData(arrayListOf(
            298, 46, 87, 178, 446, 1167, 1855, 1543, 662, 1583
        ))
    }
}
