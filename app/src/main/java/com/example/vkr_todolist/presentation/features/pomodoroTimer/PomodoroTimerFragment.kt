package com.example.vkr_todolist.presentation.features.pomodoroTimer

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import com.example.vkr_todolist.app.App
import com.example.vkr_todolist.R
import com.example.vkr_todolist.data.source.local.model.PomodoroTimer
import com.example.vkr_todolist.databinding.FragmentPomodoroTimerBinding
import com.example.vkr_todolist.presentation.main.MainViewModel
import com.example.vkr_todolist.utils.Constants
import com.example.vkr_todolist.utils.NotificationHelper
import java.util.*


class PomodoroTimerFragment : Fragment() {
    private lateinit var binding: FragmentPomodoroTimerBinding
    private var timerInfo = PomodoroTimer()
    private var timer: CountDownTimer? = null

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as App).database)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPomodoroTimerBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NotificationHelper.createNotificationChannel(requireContext())

        binding.bPlayPauseTimer.setOnClickListener {
            if(!timerInfo.isTimerRunning){
                val theme = requireContext().theme
                val iconPauseId = R.drawable.ic_pause
                val chipIconPause = ResourcesCompat.getDrawable(getResources(), iconPauseId, theme)
                binding.bPlayPauseTimer.setChipIcon(chipIconPause)
                binding.bPlayPauseTimer.text = getString(R.string.pause_timer)
                startTimer()
            }
            else{
                val theme = requireContext().theme
                val iconPlayId = R.drawable.ic_play
                val chipIconPlay = ResourcesCompat.getDrawable(getResources(), iconPlayId, theme)
                binding.bPlayPauseTimer.setChipIcon(chipIconPlay)
                binding.bPlayPauseTimer.text = getString(R.string.play_timer)
                pauseTimer()
            }
        }

        binding.bStopTimer.setOnClickListener {
            stopTimer()
        }
        observer()
        updateTimerUI()
        init()
    }

    private fun init(){
        binding.timerBar.max = (timerInfo.workTimeInMillis / 1000).toInt()
        binding.timerBar.progress = 0
    }


    private fun startTimer(){
        if(!timerInfo.isTimerPaused){
            timerInfo.isTimerRunning = true
            startCountDownTimer()
        }
        else{
            resumeTimer()
        }
    }


    private fun pauseTimer(){
        timer?.cancel()
        timerInfo.isTimerRunning = false
        timerInfo.isTimerPaused = true
        saveTimerState()
    }


    private fun resumeTimer(){
        startCountDownTimer()
        timerInfo.isTimerRunning = true
        timerInfo.isTimerPaused = false
        saveTimerState()
    }


    private fun stopTimer(){
        timer?.cancel()
        timerInfo.isTimerRunning = false
        timerInfo.isTimerPaused = false
        timerInfo.currentTimerType = 0
        timerInfo.timeLeftInMillis = timerInfo.workTimeInMillis
        binding.bPlayPauseTimer.text = getString(R.string.play_timer)
        updateTimerUI()
        saveTimerState()
        init()
    }


    private fun startCountDownTimer(){
        timer = object : CountDownTimer(timerInfo.timeLeftInMillis!!, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerInfo.timeLeftInMillis = millisUntilFinished
                updateTimerUI()
            }

            override fun onFinish() {
                // срабатывает, когда таймер закончит работу
                if (timerInfo.currentTimerType == 0) {
                    timerInfo.currentTimerType = 1 // 5 минут в миллисекундах
                    timerInfo.timeLeftInMillis = timerInfo.breakTimeInMillis
                    val message = requireContext().getString(R.string.pomodoro_msg_break)
                    sendNotification(message)
                    binding.timerBar.max = (timerInfo.breakTimeInMillis / 1000).toInt()
                    startCountDownTimer()
                } else {
                    // если закончился второй таймер, останавливаем
                    binding.timerBar.max = (timerInfo.workTimeInMillis / 1000).toInt()
                    stopTimer()
                    val message = requireContext().getString(R.string.pomodoro_msg_work)
                    sendNotification(message)
                }
            }
        }.start()

        saveTimerState()
    }


    private fun saveTimerState(){
        viewModel.updateTimerState(timerInfo)
    }


    private fun observer(){
        viewModel.lastTimerState.observe(viewLifecycleOwner){
            Log.d("TAG", "timeLeft: ${it}")
            if(it != null){
                Log.d("TAG", "timeLeft: ${it.timeLeftInMillis}")
                timerInfo.currentTimerType = it.currentTimerType
                timerInfo.isTimerRunning = it.isTimerRunning
                timerInfo.isTimerPaused = it.isTimerPaused
                updateTimerUI()
            }
        }
    }


    private fun updateTimerUI() {
        if(binding.timerBar.max == (timerInfo.workTimeInMillis / 1000).toInt())
            binding.timerBar.progress = (timerInfo.workTimeInMillis / 1000).toInt() - (timerInfo.timeLeftInMillis / 1000).toInt()
        else
            binding.timerBar.progress = (timerInfo.breakTimeInMillis / 1000).toInt() - (timerInfo.timeLeftInMillis / 1000).toInt()

        Log.d("TAG", "Progress: ${binding.timerBar.progress}")

        val minutes = (timerInfo.timeLeftInMillis / 1000) / 60
        val seconds = (timerInfo.timeLeftInMillis / 1000) % 60
        val timeLeftFormatted =
            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        binding.tvTimer.text = timeLeftFormatted

        binding.bStopTimer.isEnabled = timerInfo.isTimerRunning
    }


    private fun sendNotification(msg: String){
        val title = getString(R.string.pomodoro_timer)
        val intent = Intent(context, PomodoroReceiver::class.java).apply {
            putExtra(Constants.POMODORO_TITLE, title)
            putExtra(Constants.POMODORO_DESC, msg)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(requireContext(), Constants.CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(msg)
            .setSmallIcon(R.drawable.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }

    companion object {
        @JvmStatic
        fun newInstance() = PomodoroTimerFragment()

    }
}