package com.example.lab13

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.*

class MyService : Service() {
    private var channel = ""
    private var job: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getStringExtra("channel")?.let {
            channel = it
        }

        // 發送初始廣播
        val welcomeMsg = when(channel) {
            "music" -> "歡迎來到音樂頻道"
            "new" -> "歡迎來到新聞頻道"
            "sport" -> "歡迎來到體育頻道"
            else -> "頻道錯誤"
        }
        sendCustomBroadcast(welcomeMsg)

        // 3. 取消前一個運作中的協程任務，防止多個 Thread 同時運行
        job?.cancel()

        job = serviceScope.launch {
            try {
                delay(3000) // 延遲三秒，掛起不阻塞
                val nextMsg = when(channel) {
                    "music" -> "即將播放本月TOP10音樂"
                    "new" -> "即將為您提供獨家新聞"
                    "sport" -> "即將播報本週NBA賽事"
                    else -> "頻道錯誤"
                }
                sendCustomBroadcast(nextMsg)
            } catch (e: CancellationException) {
                // 任務被正常取消
            }
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel() // 清理所有協程
    }

    private fun sendCustomBroadcast(msg: String) {
        val intent = Intent(channel).apply {
            putExtra("msg", msg)
        }
        sendBroadcast(intent)
    }
}