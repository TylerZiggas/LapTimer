//AUTHOR: Tyler Ziggas
// Date: March 2021
// The purpose of this assignment is to have a timer that can be started, stopped, and reset.
// You can also hit lap while it is running, and it will display the lap number, the time of the lap, and the difference between the previous lap and the current lap
// We are also to make sure the timer works correctly if the screen rotates or if the user spam adds laps

package edu.umsl.tyler

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import edu.umsl.tyler.databinding.ActivityMainBinding
import edu.umsl.tyler.ui.main.LapViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding // Creation of binding
    private lateinit var viewModel: LapViewModel // Grabbing the view model

    private var running = false // Private variables that will get passed if screen is turned, but not necessary in view model
    private var startTime: Long = 0
    private val customHandler = Handler(Looper.myLooper()!!)
    private var timeInMilliseconds: Long = 0
    private var timeSwapBuff: Long = 0
    private var updatedTime: Long = 0
    private var previousTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) { // On creation
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LapViewModel::class.java) // Grab view model in case we need it
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) // Set the binding for the content view

        this.supportActionBar!!.hide() // Hide the support bar

        binding.startStopButton.setOnClickListener {// Checking if start or stop, by default running is false so first click is to start
            if (!running) {
                run() // Starting the timer
            } else {
                stop() // Stopping the timer
            }
        }

        binding.lapResetButton.setOnClickListener {// Checking if lap or reset, by default running is false, and reset should always show with run
            if (!running) {
                reset() // Reset everything for the timer
            } else {
                lap() // Add a new lap
            }
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putBoolean("running", running) // Saving all of the private local variables
        savedInstanceState.putLong("timeInMilliseconds",timeInMilliseconds)
        savedInstanceState.putLong("timeSwapBuff",timeSwapBuff)
        savedInstanceState.putLong("updatedTime",updatedTime)
        savedInstanceState.putLong("startTime", startTime)
        savedInstanceState.putLong("previousTime", previousTime)

     }

     override fun onRestoreInstanceState(savedInstanceState: Bundle) {
         super.onRestoreInstanceState(savedInstanceState)
         running = savedInstanceState.getBoolean("running") // Readd them to the screen
         timeInMilliseconds = savedInstanceState.getLong("timeInMilliseconds")
         timeSwapBuff = savedInstanceState.getLong("timeSwapBuff")
         updatedTime = savedInstanceState.getLong("updatedTime")
         startTime = savedInstanceState.getLong("startTime")
         previousTime = savedInstanceState.getLong("previousTime")
         if (running) { // Check if the program was running, if it was keep it going
             run()
         } else {
             fixScreen() // Need to fix what is on screen in case it was paused
         }
     }

    private fun update() { // Function to update the buttons depending on what is clicked
        if (running) {
            binding.startStopButton.text = "Stop"
            binding.lapResetButton.text = "Lap"
        } else {
            binding.startStopButton.text = "Start"
            binding.lapResetButton.text = "Reset"
        }
    }

    private fun run() {
        if (!running) { // This is specifically if we just restored the screen
            startTime = SystemClock.uptimeMillis()
        }
        running = true
        update()
        customHandler.postDelayed(updateTimerThread, 0) // Start doing the timer
    }

    private fun stop() {
        running = false
        update()
        timeSwapBuff += timeInMilliseconds
        customHandler.removeCallbacks(updateTimerThread) // Stopping the timer
    }

    private fun reset() {
        update()
        binding.minutes.text = "00"  // Setting the view to look reset
        binding.seconds.text = "00"
        binding.centiSeconds.text = "000"
        startTime = SystemClock.uptimeMillis() // Reset variables used for uptime and buffer
        timeSwapBuff = 0
        updatedTime = 0
        viewModel.resetLaps() // Call the view model and reset the laps in fragment
    }

    private fun lap() {
        timeInMilliseconds = (SystemClock.uptimeMillis()) - startTime // Grab the time when lap is clicked
        updatedTime = timeSwapBuff + timeInMilliseconds
        var seconds = (updatedTime / 1000)
        val minutes = seconds / 60
        seconds %= 60
        val lap = (String.format("%02d", minutes)).plus(":").plus(String.format("%02d", seconds)).plus(".").plus(String.format("%03d", (updatedTime % 1000))) // Format for placing in fragment

        if (previousTime == 0L) { // Check if previous is nothing, if so difference will be 0
            previousTime = updatedTime
        }
        val differenceTime = updatedTime - previousTime // Calculate difference

        var differenceSeconds = (differenceTime / 1000)
        val differenceMinutes = seconds / 60
        differenceSeconds %= 60
        val differenceLap = (String.format("%02d", differenceMinutes)).plus(":").plus(String.format("%02d", differenceSeconds)).plus(".").plus(String.format("%03d", (differenceTime % 1000))) // Format for placing in fragment
        previousTime = updatedTime
        viewModel.setLaps(lap, differenceLap) // Set the laps for each of these
    }

    private fun fixScreen() {
        var seconds = (updatedTime / 1000) // Fixing the screen in case paused
        val minutes = seconds / 60
        seconds %= 60
        binding.minutes.text = String.format("%02d", minutes)
        binding.seconds.text = String.format("%02d", seconds)
        binding.centiSeconds.text = String.format("%03d", (updatedTime % 1000))
    }

    private val updateTimerThread: Runnable = object : Runnable {
        override fun run() { // Function for running
            timeInMilliseconds = (SystemClock.uptimeMillis()) - startTime // Get the time in milliseconds
            updatedTime = timeSwapBuff + timeInMilliseconds // Add the buffer to the time to update it
            var seconds = (updatedTime / 1000)
            val minutes = seconds / 60
            seconds %= 60 // Calculate all times
            binding.minutes.text = String.format("%02d", minutes)
            binding.seconds.text = String.format("%02d", seconds)
            binding.centiSeconds.text = String.format("%03d", (updatedTime % 1000)) // Display it on screen

            customHandler.postDelayed(this, 0)
        }
    }
}