package com.example.lab13

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var tvMsg: TextView
    private var lastChannel: String? = null

    // 1. 建立 BroadcastReceiver，解析邏輯更簡潔
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            intent.getStringExtra("msg")?.let {
                tvMsg.text = it
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        initViews()
    }

    private fun initViews() {
        tvMsg = findViewById(R.id.tvMsg)
        val mainView = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.main)

        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.btnMusic).setOnClickListener { startBroadcastService("music") }
        findViewById<Button>(R.id.btnNew).setOnClickListener { startBroadcastService("new") }
        findViewById<Button>(R.id.btnSport).setOnClickListener { startBroadcastService("sport") }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 確保註冊與解除註冊成對出現
        unregisterReceiver(receiver)
    }

    private fun startBroadcastService(channel: String) {
        // 2. 防止重複註冊相同的 Filter
        if (lastChannel != channel) {
            // 先解除舊的（如果有），再註冊新的
            lastChannel?.let { unregisterReceiver(receiver) }

            val intentFilter = IntentFilter(channel)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(receiver, intentFilter, RECEIVER_EXPORTED)
            } else {
                registerReceiver(receiver, intentFilter)
            }
            lastChannel = channel
        }

        // 啟動 Service
        val i = Intent(this, MyService::class.java)
        startService(i.putExtra("channel", channel))
    }
}