package com.cys.common.demo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cys.common.databinding.ActivityMainBinding
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val URI_PROVIDER = Uri.parse("content://com.hy.weather.mz.hy.plugin")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.dialogButton.setOnClickListener {
            startActivity(Intent(this, DialogActivity::class.java))
//            try {
//                val intent = Intent()
//                intent.data =
//                    (Uri.parse("mzweather://com.meizu.flyme.weather/main?title=未来三天江苏天气预报&key=http%3a%2f%2freader.res.meizu.com%2freader%2fview%2fview.html%3fid%3d1582682800936201"))
//                startActivity(intent)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }

        }
    }
}