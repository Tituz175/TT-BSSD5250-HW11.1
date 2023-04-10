package com.example.tt_bssd5250_111

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.commit

private const val VID_TAG:String = "video"
private const val LLID:Int = 123

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ll = findViewById<LinearLayout>(R.id.ll)
        ll.apply {
            orientation = LinearLayoutCompat.VERTICAL
            layoutParams = LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                LinearLayoutCompat.LayoutParams.MATCH_PARENT
            )
            id = LLID
        }

        if (savedInstanceState == null) {
            val path1 = "android.resource://$packageName/raw/undock"
            val path2 = "android.resource://$packageName/raw/small"
            val a: ArrayList<String> = arrayListOf(path1,path2)
            supportFragmentManager.commit {
                replace(ll.id, VideoFragment.newInstance(a), VID_TAG)
            }
        } else {
            val stepFragment = supportFragmentManager.findFragmentByTag(VID_TAG) as VideoFragment
            supportFragmentManager.commit {
                replace(ll.id, stepFragment, VID_TAG)
            }
        }
    }
}