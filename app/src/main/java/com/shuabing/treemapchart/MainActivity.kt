package com.shuabing.treemapchart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var list = arrayListOf<Double>()
        list.add(6.0)
        list.add(6.0)
        list.add(4.0)
        list.add(3.0)
        list.add(5.0)
        list.add(2.0)
        list.add(1.0)
        Handler().postDelayed({
            treemap1.initTreeMap(list)
            //treemap1.initTreeMap(list)
        },300L)

    }
}