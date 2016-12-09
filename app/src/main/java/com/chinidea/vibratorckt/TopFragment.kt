package com.chinidea.vibratorckt

import android.app.Service
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.os.Vibrator
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import java.util.*

/**
 * Created by johnny on 2016/12/5.
 */
class TopFragment : Fragment() {

    private val TAG = "TopFragment"

    private val VIBRATION_INTERVAL: Long = 0

    private var play: Button? = null;
    private var record: Button? = null;
    private var assign: Button? = null;
    private var patternZone: ImageView? = null
    private var rootView: View? = null;

    private var isRecording = false

    private var isRecordingPatterns: Boolean = false
    private var patterns: ArrayList<Long> = ArrayList()
    private var vibrator: Vibrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vibrator = context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator;
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        rootView = inflater!!.inflate(R.layout.fragment_top, container, false)

        initUI();

        return rootView
    }

    fun initUI() {

        play = rootView!!.findViewById(R.id.btn_play) as Button
        record = rootView!!.findViewById(R.id.btn_record) as Button
        assign = rootView!!.findViewById(R.id.btn_assign) as Button
        patternZone = rootView!!.findViewById(R.id.pattern_zone) as ImageView

        (assign as Button).setOnClickListener(View.OnClickListener { view ->
            val prefs: SharedPreferences = context.getSharedPreferences("patterns", 0);
            var value: StringBuilder = StringBuilder()


            for (i in patterns.indices) {
                value.append(patterns[i])
                if (i != (patterns.size - 1)) {
                    value.append("#")
                }
            }

            /*
            for (pattern in patterns) {
                value.append(pattern)
                value.append("#")
            }
            */

            prefs.edit().putString("patterns", value.toString()).commit()

            patterns.clear()
            Snackbar.make(view, getString(R.string.pattern_assigned), Snackbar.LENGTH_SHORT).show();

        })

        (play as Button).setOnClickListener(View.OnClickListener { view ->
            val values: LongArray = patterns.toLongArray()
            for (value in values) {
                Log.d(TAG, value.toString())
            }
            vibrator!!.vibrate(values, -1);
        })

        (record as Button).setOnClickListener(View.OnClickListener { view ->
            if (isRecording) {
                isRecording = false
                // setEnable()
                (play as Button).isEnabled = true
                (assign as Button).isEnabled = true
                (record as Button).text = getString(R.string.btn_record)
            } else {
                isRecording = true
                patterns.clear()
                patterns.add(VIBRATION_INTERVAL)
                (play as Button).isEnabled = false
                (assign as Button).isEnabled = false
                (record as Button).text = getString(R.string.btn_recording)
            }

        })

        (patternZone as ImageView).setOnTouchListener(View.OnTouchListener { v, event ->
            if (!isRecording) {
                return@OnTouchListener true;
            }

            if (event.action == MotionEvent.ACTION_DOWN) {
                isRecordingPatterns = true
                AsyncRecording().execute(Object());
            } else if (event.action == MotionEvent.ACTION_UP) {
                isRecordingPatterns = false
                AsyncInterval().execute(Object())
            }
            true;
        })

    }

    inner class AsyncRecording : AsyncTask<Object, Integer, Long>() {

        override fun doInBackground(vararg params: Object?): Long? {
            val startTime = System.currentTimeMillis()
            vibrator!!.vibrate(10000000)
            while (isRecordingPatterns) {
            }
            vibrator!!.cancel()
            val endTime = System.currentTimeMillis()

            return endTime - startTime
        }

        override fun onPostExecute(result: Long?) {
            patterns.add(result!!)
            super.onPostExecute(result)
        }

    }

    inner class AsyncInterval : AsyncTask<Object, Integer, Long>() {
        override fun doInBackground(vararg params: Object?): Long? {

            var startTime: Long = System.currentTimeMillis()
            while (!isRecordingPatterns) {

            }

            var endTime: Long = System.currentTimeMillis()
            if (patterns.size > 1) {
                patterns.add(endTime - startTime)
            }

            return (endTime - startTime)
        }
    }

}