package com.example.lab12

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.*

class MyService : Service() {

    // 1. 定義協程範疇，並與 Service 生命周期綁定
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())

    override fun onCreate() {
        super.onCreate()

        // 2. 使用協程處理延遲任務
        serviceScope.launch {
            try {
                delay(3000) // 協程的延遲，不會阻塞執行緒

                // 跳轉至 SecActivity
                val intent = Intent(this@MyService, SecActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)

                // 3. 任務完成後，主動停止 Service 以節省資源
                stopSelf()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        // 4. 當 Service 被銷毀時，取消所有運作中的協程任務，防止洩漏
        serviceScope.cancel()
    }
}