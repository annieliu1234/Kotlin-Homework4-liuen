package com.example.lab14

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    // 1. 定義地點座標常數，避免重複輸入錯誤
    companion object {
        private val TAIPEI_101 = LatLng(25.033611, 121.565000)
        private val TAIPEI_STATION = LatLng(25.047924, 121.517081)
        private val DAA_PARK = LatLng(25.032435, 121.534905)
        private const val REQUEST_LOCATION_PERMISSION = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadMap()
    }

    private fun loadMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        if (checkLocationPermissions()) {
            setupMap(map)
        } else {
            requestLocationPermissions()
        }
    }

    // 2. 封裝地圖初始化設定
    private fun setupMap(map: GoogleMap) {
        // 啟用目前位置藍點（需已獲得權限）
        try {
            map.isMyLocationEnabled = true
        } catch (e: SecurityException) {
            e.printStackTrace()
        }

        // 加入標記
        map.addMarker(MarkerOptions().position(TAIPEI_101).title("台北101").draggable(true))
        map.addMarker(MarkerOptions().position(TAIPEI_STATION).title("台北車站").draggable(true))

        // 繪製線段
        val polylineOptions = PolylineOptions()
            .add(TAIPEI_101)
            .add(DAA_PARK)
            .add(TAIPEI_STATION)
            .color(Color.BLUE)
            .width(10f)

        map.addPolyline(polylineOptions)

        // 移動視角
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(25.035, 121.54), 13f))
    }

    // 3. 簡化權限檢查邏輯
    private fun checkLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadMap()
            } else {
                finish()
            }
        }
    }
}