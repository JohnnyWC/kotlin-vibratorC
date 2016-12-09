package com.chinidea.vibratorckt.receiver

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Vibrator
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log

/**
 * Created by johnny on 2016/12/5.
 */
class IncomingCallReceiver : BroadcastReceiver() {

    private var vibrator: Vibrator? = null

    override fun onReceive(context: Context?, intent: Intent?) {

        val am = context!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (am.ringerMode != AudioManager.RINGER_MODE_VIBRATE) {
            return
        }

        val prefs = context!!.getSharedPreferences("patterns", 0)
        val value = prefs.getString("patterns", "")
        val patterns = getPatterns(value)

        vibrator = context!!.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        vibrator!!.vibrate(patterns, 0)

        val myPhoneStateListener = MFPhoneStateListener()
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)

    }

    private fun getPatterns(value: String): LongArray {
        var temp = value.split("#")
        var patterns = LongArray(temp.size - 1)
        for (i in temp.indices) {
            if (temp[i] == null || temp[i].equals("")) continue
            patterns[i] = java.lang.Long.valueOf(temp[i])!!
        }
        return patterns
    }

    private inner class MFPhoneStateListener : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, incomingNumber: String) {
            when (state) {
                TelephonyManager.CALL_STATE_IDLE -> vibrator!!.cancel()
                TelephonyManager.CALL_STATE_OFFHOOK -> vibrator!!.cancel()
                TelephonyManager.CALL_STATE_RINGING -> {
                }
                else -> {
                }
            }
        }
    }
}