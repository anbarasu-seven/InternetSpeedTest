package com.example.internetspeedtest

import android.net.TrafficStats
import java.util.*

class NetworkSpeedUtils{

    companion object{
        val GB : Long = 1000000000
        val MB : Long = 1000000
        val KB : Long = 1000

        fun getNetworkSpeed() : String{

            var downloadSpeedOutput = ""
            var units = ""
            val mBytesPrevious = TrafficStats.getTotalRxBytes()

            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            val mBytesCurrent = TrafficStats.getTotalRxBytes()

            val mNetworkSpeed = mBytesCurrent - mBytesPrevious

            val mDownloadSpeedWithDecimals: Float

            if (mNetworkSpeed >= GB) {
                mDownloadSpeedWithDecimals = mNetworkSpeed.toFloat() / GB.toFloat()
                units = " GB"
            } else if (mNetworkSpeed >= MB) {
                mDownloadSpeedWithDecimals = mNetworkSpeed.toFloat() / MB.toFloat()
                units = " MB"
            } else {
                mDownloadSpeedWithDecimals = mNetworkSpeed.toFloat() / KB.toFloat()
                units = " KB"
            }

            downloadSpeedOutput = if (units != " KB" && mDownloadSpeedWithDecimals < 100) {
                String.format(Locale.US, "%.1f", mDownloadSpeedWithDecimals)
            } else {
                Integer.toString(mDownloadSpeedWithDecimals.toInt())
            }

            return (downloadSpeedOutput + units)
        }
    }
}