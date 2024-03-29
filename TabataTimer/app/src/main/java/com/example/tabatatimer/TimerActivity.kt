package com.example.tabatatimer

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TimerActivity : ActivityBase() {
    private val sequencesViewModel: SequencesViewModel by viewModels()
    private var seqPos: Int = 0
    private lateinit var currSeq: Sequence

    private lateinit var timerStatusReceiver: BroadcastReceiver
    private lateinit var timerTimeReceiver: BroadcastReceiver
    private lateinit var timerCurrPhaseReceiver: BroadcastReceiver

    private var isTimerRunning: Boolean = false
    var currPhase: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("TimerActivity", "onCreate()")

        seqPos = intent.getIntExtra("currentSequencePosition", 0)
        currSeq = sequencesViewModel.sequencesList.value!![seqPos]

        setTextTheme()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        val phasesRecyclerView: RecyclerView = findViewById(R.id.timer_phases_recyclerview)
        phasesRecyclerView.layoutManager = LinearLayoutManager(this)
        phasesRecyclerView.adapter = TimerPhaseRecyclerAdapter(
            sequencesViewModel.sequencesList.value!![seqPos],
            this
        )

        val pauseButton: Button = findViewById(R.id.timer_pause_button)
        pauseButton.setOnClickListener {
            if (isTimerRunning) { pauseTimer() } else { startTimer() }
        }

        val prevPhaseButton: Button = findViewById(R.id.timer_back_button)
        prevPhaseButton.setOnClickListener {
            setPrevTimerPhase()
        }

        val nextPhaseButton: Button = findViewById(R.id.timer_forth_button)
        nextPhaseButton.setOnClickListener {
            setNextTimerPhase()
        }

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPressed()
            }
        })

        initializeTimer()
    }

    private fun stopTimerService() {
        Log.d("TimerActivity", "Stop timer service")

        //resetTimer()
        //pauseTimer()
        val intent = Intent(this, TimerService::class.java)
        //intent.putExtra(TimerService.TIMER_ACTION, TimerService.STOP)
        //startService(intent)
        stopService(intent)
    }

    private fun handleBackPressed() {
        stopTimerService()

        val context = this
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()

        // Moving the service to background when the app is visible
        moveToBackground()
    }

    // We will also unregister the receivers here to avoid leaks
    override fun onPause() {
        super.onPause()
        unregisterReceiver(timerStatusReceiver)
        unregisterReceiver(timerTimeReceiver)
        unregisterReceiver(timerCurrPhaseReceiver)
        // Moving the service to foreground when the app is in background / not visible
        moveToForeground()
    }

    override fun onResume() {
        super.onResume()

        getTimerStatus()

        // Receiving stopwatch status from service
        val statusFilter = IntentFilter()
        statusFilter.addAction(TimerService.TIMER_STATUS)
        timerStatusReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val isRunning = intent?.getBooleanExtra(TimerService.IS_TIMER_RUNNING, false)!!
                isTimerRunning = isRunning
                val timeRemaining = intent.getIntExtra(TimerService.TIME_REMAINING, 0)
                val currPhaseIndex = intent.getIntExtra(TimerService.CURRENT_PHASE, 0)

                updateButtons(isTimerRunning)
                updateTimerValue(timeRemaining)
                updatePhase(currPhaseIndex)
            }
        }
        registerReceiver(timerStatusReceiver, statusFilter)

        // Receiving time values from service
        val timeFilter = IntentFilter()
        timeFilter.addAction(TimerService.TIMER_TICK)
        timerTimeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val timeRemaining = intent?.getIntExtra(TimerService.TIME_REMAINING, 0)!!
                updateTimerValue(timeRemaining)
            }
        }
        registerReceiver(timerTimeReceiver, timeFilter)

        val phaseFilter = IntentFilter()
        phaseFilter.addAction(TimerService.TIMER_PHASE)
        timerCurrPhaseReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val currPhaseIndex = intent!!.getIntExtra(TimerService.CURRENT_PHASE, 0)
                updatePhase(currPhaseIndex)
                val timeRemaining = intent.getIntExtra(TimerService.TIME_REMAINING, 0)
                updateTimerValue(timeRemaining)
            }
        }
        registerReceiver(timerCurrPhaseReceiver, phaseFilter)
    }

    private fun updateTimerValue(value: Int) {
        /*val hours: Int = (timeElapsed / 60) / 60
        val minutes: Int = timeElapsed / 60
        val seconds: Int = timeElapsed % 60*/

        val timerValueTextView: TextView = findViewById(R.id.remaining_seconds_textview)
        timerValueTextView.text = value.toString()
        //timerValueTextView.text = "${"%02d".format(hours)}:${"%02d".format(minutes)}:${"%02d".format(seconds)}"
    }

    private fun updateButtons(isTimerRunning: Boolean) {
        if (isTimerRunning) {
            val pauseButton: Button =
                findViewById(R.id.timer_pause_button)
            pauseButton.text = getString(R.string.pause)
            /*binding.toggleButton.icon =
                ContextCompat.getDrawable(this, R.drawable.ic_pause)
            binding.resetImageView.visibility = View.INVISIBLE*/
        } else {
            val pauseButton: Button =
                findViewById(R.id.timer_pause_button)
            pauseButton.text = getString(R.string.resume)
            /*binding.toggleButton.icon =
                ContextCompat.getDrawable(this, R.drawable.ic_play)
            binding.resetImageView.visibility = View.VISIBLE*/
        }
    }

    private fun updatePhase(phaseIndex: Int) {
        //currPhase = if (phaseIndex >= currSeq.phasesList.size) { 0 } else { phaseIndex }
        currPhase = phaseIndex
        if (currPhase < 0) { currPhase = 0 }

        Log.d("TimerActivity", "current phase: $currPhase")

        val phaseRecyclerView: RecyclerView = findViewById(R.id.timer_phases_recyclerview)
        phaseRecyclerView.adapter!!.notifyDataSetChanged()
        /*Log.d("TimerActivity", "current phase: $phaseIndex")
        val phaseRecyclerView: RecyclerView = findViewById(R.id.timer_phases_recyclerview)
        val phaseAdapter: TimerPhaseRecyclerAdapter = phaseRecyclerView.adapter as TimerPhaseRecyclerAdapter
        phaseAdapter.setCurrentPhase(phaseIndex)*/
    }

    private fun initializeTimer() {
        val intent = Intent(this, TimerService::class.java)
        intent.putExtra(TimerService.TIMER_ACTION, TimerService.INITIALIZE)
        intent.putExtra("currentSequencePosition", seqPos)
        intent.putExtra("numRepetitions", currSeq.numRepetitions)
        val phasesDurations = arrayListOf<Int>()
        for (phase in currSeq.phasesList) {
            phasesDurations.add(phase.durationSec)
        }
        intent.putIntegerArrayListExtra("phasesDurations", phasesDurations)
        startService(intent)
    }

    private fun getTimerStatus() {
        val intent = Intent(this, TimerService::class.java)
        intent.putExtra(TimerService.TIMER_ACTION, TimerService.GET_STATUS)
        /*intent.putExtra("numRepetitions", currSeq.numRepetitions)

        val phasesDurations = arrayListOf<Int>()
        for (phase in currSeq.phasesList) {
            phasesDurations.add(phase.durationSec)
        }
        intent.putIntegerArrayListExtra("phasesDurations", phasesDurations)*/
        startService(intent)
        //startTimer()
    }

    private fun setNextTimerPhase() {
        val intent = Intent(this, TimerService::class.java)
        intent.putExtra(TimerService.TIMER_ACTION, TimerService.SWITCH_TO_NEXT_PHASE)
        startService(intent)
    }

    private fun setPrevTimerPhase() {
        val intent = Intent(this, TimerService::class.java)
        intent.putExtra(TimerService.TIMER_ACTION, TimerService.SWITCH_TO_PREV_PHASE)
        startService(intent)
    }

    private fun startTimer() {
        val intent = Intent(this, TimerService::class.java)
        intent.putExtra(TimerService.TIMER_ACTION, TimerService.START)
        startService(intent)
    }

    private fun pauseTimer() {
        val intent = Intent(this, TimerService::class.java)
        intent.putExtra(TimerService.TIMER_ACTION, TimerService.PAUSE)
        startService(intent)
    }

    private fun resetTimer() {
        val intent = Intent(this, TimerService::class.java)
        intent.putExtra(TimerService.TIMER_ACTION, TimerService.RESET)
        startService(intent)
    }

    private fun moveToForeground() {
        val intent = Intent(this, TimerService::class.java)
        intent.putExtra(TimerService.TIMER_ACTION, TimerService.MOVE_TO_FOREGROUND)
        startService(intent)
    }

    private fun moveToBackground() {
        val intent = Intent(this, TimerService::class.java)
        intent.putExtra(TimerService.TIMER_ACTION, TimerService.MOVE_TO_BACKGROUND)
        startService(intent)
    }
}