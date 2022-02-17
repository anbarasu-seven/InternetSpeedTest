package com.example.internetspeedtest

import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream


class MainActivity : AppCompatActivity() {

    private var secondTextView: TextView? = null
    private var firstTextView: TextView? = null
    private var speed: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firstTextView = findViewById<TextView>(R.id.firstTextView)
        secondTextView = findViewById<TextView>(R.id.secondTextView)
        speed = findViewById<TextView>(R.id.speed)

        val checkButton = findViewById<TextView>(R.id.checkButton)
        checkButton.setOnClickListener {
            byDownloading()
        }
    }

    fun usingInternalSdk(){
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nc = cm.getNetworkCapabilities(cm.activeNetwork)
            val downSpeed = nc!!.linkDownstreamBandwidthKbps
            val upSpeed = nc.linkUpstreamBandwidthKbps
            firstTextView?.text = "${downSpeed / 1000} Mbps"
        } else {

        }
    }

    var startTime: Long = 0
    var endTime: Long = 0
    var fileSize: Long = 0

    private val _2_POINT_9MB = 2.9 * 1000 * 1000
    private val _7MB = 7 * 1000 * 1000
    private val _27MB = 25 * 1000 * 1000

    fun byDownloading() {
        var client = OkHttpClient()
        val builder = Request.Builder()
        builder.url("https://filesamples.com/samples/image/png/sample_640%C3%97426.png")//521kb image
        val request: Request = builder.build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                val input: InputStream = response.body!!.byteStream()
                startTime = System.currentTimeMillis()
                try {
                    val bos = ByteArrayOutputStream()
                    val buffer = ByteArray(1024)
                    while (input.read(buffer) !== -1) {
                        bos.write(buffer)
                    }
                    val docBuffer: ByteArray = bos.toByteArray()
                    fileSize = docBuffer.size.toLong()
                }catch (e: Exception){
                    e.printStackTrace()
                } finally {
                    input.close()
                }
                endTime = System.currentTimeMillis()

                // calculate how long it took by subtracting endtime from starttime
                val timeTakenMills = (endTime - startTime) // time taken in milliseconds
                val timeTakenInSecs = (timeTakenMills / 1000).toFloat() // divide by 1000 to get time in seconds

                // get the download speed by dividing the file size by time taken to download
                val bytesPerSecond = (fileSize * 1000) / timeTakenMills
                Log.d("TAG 1", "Time taken in milli sec: $timeTakenMills")
                Log.d("TAG 1", "Bytes per second: $bytesPerSecond")
                Log.d("TAG 1", "File size in bytes: $fileSize")

                if (bytesPerSecond >= _27MB) {
                    Log.d("TAG 1", "4K support")
                    updateUI(" >= 27Mb", " Able to stream 4k", (bytesPerSecond/1000).toString());
                }else if (bytesPerSecond >= _7MB) {
                    Log.d("TAG 1", "HDR support")
                    updateUI(" >= 7Mb"," Able to stream HDR",(bytesPerSecond/1000).toString());
                } else if (bytesPerSecond >= _2_POINT_9MB) {
                    Log.d("TAG 1", "HD support")
                    updateUI(" >= 2.9Mb"," Able to stream HD", (bytesPerSecond/1000).toString());
                }else{
                    Log.d("TAG 1", "Regular streaming support")
                    updateUI(" < 2.9Mb","You can stream regular content, but not HD|HDR|4K", (bytesPerSecond/1000).toString());
                }
            }
        })
    }

    fun updateUI(s1: String, s2: String, s3:String) {
        runOnUiThread {
            firstTextView?.text = s1
            secondTextView?.text = s2
            speed?.text = s3 + "Kb"
        }
    }

}