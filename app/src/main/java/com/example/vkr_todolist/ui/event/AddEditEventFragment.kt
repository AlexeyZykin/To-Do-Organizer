package com.example.vkr_todolist.ui.event

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.CountDownTimer
import android.text.format.DateFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.vkr_todolist.App
import com.example.vkr_todolist.NavGraphDirections
import com.example.vkr_todolist.R
import com.example.vkr_todolist.data.model.Event
import com.example.vkr_todolist.data.model.Task
import com.example.vkr_todolist.databinding.FragmentAddEditEventBinding
import com.example.vkr_todolist.ui.alarm.EventReceiver
import com.example.vkr_todolist.ui.alarm.TaskReceiver
import com.example.vkr_todolist.ui.main.MainViewModel
import com.example.vkr_todolist.utils.Constants
import com.example.vkr_todolist.utils.DateTimeManager
import com.example.vkr_todolist.utils.NotificationHelper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.launch
import java.util.*

class AddEditEventFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddEditEventBinding
    private lateinit var popupMenu: PopupMenu
    private lateinit var reminderMenu: PopupMenu
    private val args: AddEditEventFragmentArgs by navArgs()
    private var eventInfo = Event(null,"",null, "", null, null)
    private var isCategorySelected = false

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as App).database)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddEditEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bSaveEvent.setOnClickListener {
            createNewEvent()
        }
        binding.chipSelectList.setOnClickListener {
            initFilterList()
        }
        binding.chipDatePicker.setOnClickListener { showDatePicker() }
        binding.chipDatePicker.setOnCloseIconClickListener {
            eventInfo.date = null
            eventInfo.reminder = null
            binding.chipDatePicker.setText(R.string.due_date)
            binding.chipDatePicker.isCloseIconVisible=false
            binding.chipReminder.setText(R.string.reminder)
            binding.chipReminder.isCloseIconVisible=false
        }
        binding.chipReminder.setOnClickListener {
            if(eventInfo.date == null)
                Snackbar.make(binding.root, R.string.snackbar_datePicker, Snackbar.LENGTH_SHORT).setAction("Action", null).show()
            else
                setRemindTime()
        }
        binding.chipReminder.setOnCloseIconClickListener {
            eventInfo.reminder = null
            binding.chipReminder.setText(R.string.reminder)
            binding.chipReminder.isCloseIconVisible=false
        }

        NotificationHelper.createNotificationChannel(requireContext())
        initUpdate()
    }


    private fun createNewEvent()=with(binding){
        if(edTitle.text.isNullOrBlank())
            Snackbar.make(binding.root, R.string.snackbar_addTitle, Snackbar.LENGTH_SHORT).setAction("Action", null).show()
        else if(eventInfo.date == null)
            Snackbar.make(binding.root, R.string.snackbar_datePicker, Snackbar.LENGTH_SHORT).setAction("Action", null).show()
        else if(eventInfo.listId == null)
            Snackbar.make(binding.root, R.string.snackbar_selectList, Snackbar.LENGTH_SHORT).setAction("Action", null).show()
        else {
            if(args.event == null) {
                val tempEvent = Event(
                    null,
                    binding.edTitle.text.toString(),
                    eventInfo.listId,
                    eventInfo.listName,
                    eventInfo.date,
                    eventInfo.reminder,
                    eventInfo.isFinished
                )
                lifecycleScope.launch {
                    val eventId = viewModel.insertEvent(tempEvent).await()
                    Log.d("TAG", "eventId: ${eventId}")
                    // Устанавливаем уведомление
                    if (tempEvent.reminder != null)
                        setAlarm(requireContext(), eventId.toInt(), tempEvent)
                    findNavController().popBackStack()
                }
            }
            else{
                updateEvent()
                findNavController().popBackStack()
            }
        }

    }


    private fun initUpdate() = with (binding){
        if(args.event!=null){
            edTitle.setText(args.event!!.eventTitle)

            eventInfo.date = args.event?.date
            if(eventInfo.date != null) {
                chipDatePicker.text = DateTimeManager.convertTimeToString(eventInfo.date!!)
                chipDatePicker.isCloseIconVisible = true
            }

            eventInfo.reminder = args.event?.reminder
            if(eventInfo.reminder!=null){
                chipReminder.isCloseIconVisible = true
                when(eventInfo.reminder){
                    DateTimeManager.getHalfHourBeforeDate(eventInfo.date!!) -> {
                        chipReminder.text = getString(R.string.remind_half_hour_before)
                    }

                    DateTimeManager.getHourBeforeDate(eventInfo.date!!) -> {
                        chipReminder.text = getString(R.string.remind_hour_before)
                    }

                    DateTimeManager.getDayBeforeDate(eventInfo.date!!) -> {
                        chipReminder.text = getString(R.string.remind_day_before)
                    }
                }

            }


            eventInfo.listId=args.event?.listId!!
            eventInfo.listName = args.event?.listName!!
            isCategorySelected=true
            viewModel.getListById(eventInfo.listId!!).observe(viewLifecycleOwner){
                it.forEach {
                    chipSelectList.text = it.listTitle
                }
            }
        }

        if(args.listName!= null){
            chipSelectList.text = args.listName!!.listTitle
            eventInfo.listId = args.listName!!.listId
            eventInfo.listName = args.listName!!.listTitle
        }
    }


    private fun updateEvent() = with(binding){
        val tempEvent = args.event?.copy(
            eventTitle = edTitle.text.toString(),
            listId = eventInfo.listId,
            listName = eventInfo.listName,
            date = eventInfo.date,
            reminder = eventInfo.reminder,
            isFinished = eventInfo.isFinished
        )
        lifecycleScope.launch {
            viewModel.updateEvent(tempEvent!!)
            Log.d("TAG", "reminder ${tempEvent.reminder}")
            if (tempEvent.reminder != null)
                setAlarm(requireContext(), tempEvent.eventId!!, tempEvent)
            else
                removeAlarm(tempEvent.eventId!!, tempEvent)
        }
    }


    private fun initFilterList()= with(binding){
        popupMenu = PopupMenu(context?.applicationContext, chipSelectList)
        viewModel.allListItem.value?.forEach{listItem->
            popupMenu.menu.add(listItem.listTitle)
                .setIcon(R.drawable.ic_notes)
                .setOnMenuItemClickListener {menuItem->
                    chipSelectList.text=menuItem.title
                    eventInfo.listId = listItem.listId
                    eventInfo.listName = listItem.listTitle
                    isCategorySelected = true
                    true
                }
        }
        popupMenu.menu.add(Menu.NONE, R.id.addEditListFragment, Menu.NONE, R.string.add_new_list)
            .setIcon(R.drawable.ic_add)
            .setOnMenuItemClickListener {
                val action = NavGraphDirections.actionGlobalAddEditListFragment(null)
                findNavController().navigate(action)
                true
            }
        popupMenu.show()
    }

    private fun showDatePicker(){
        val datePicker = MaterialDatePicker.Builder.datePicker().build()
        val isSystem24Hour = DateFormat.is24HourFormat(requireContext())
        val clockFormat = if(isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(0)
            .setMinute(0)
            .setTitleText(R.string.time)
            .build()

        datePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            eventInfo.date = calendar.time
            binding.chipDatePicker.text = DateTimeManager.convertDateToString(eventInfo.date!!)
            timePicker.show(childFragmentManager, "TAG")
        }

        timePicker.addOnPositiveButtonClickListener {
            val cal = Calendar.getInstance()
            cal.time=eventInfo.date!!
            cal.set(Calendar.HOUR_OF_DAY, timePicker.hour)
            cal.set(Calendar.MINUTE, timePicker.minute)
            eventInfo.date = cal.time
            binding.chipDatePicker.text = DateTimeManager.convertTimeToString(eventInfo.date!!)
            binding.chipDatePicker.isCloseIconVisible = true

            eventInfo.reminder=null
            binding.chipReminder.setText(R.string.reminder)
            binding.chipReminder.isCloseIconVisible = false
        }
        datePicker.show(childFragmentManager,"TAG")
    }


    private fun setRemindTime()=with(binding){
        reminderMenu = PopupMenu(requireContext().applicationContext, chipReminder)
        reminderMenu.menuInflater.inflate(R.menu.filter_reminder_menu, reminderMenu.menu)
        reminderMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.rmHalfHourBefore -> {
                    chipReminder.text=menuItem.title
                    eventInfo.reminder=DateTimeManager.getHalfHourBeforeDate(eventInfo.date!!)
                    Log.d("TAG", "HalfHour ${eventInfo.reminder}")
                    chipReminder.isCloseIconVisible = true
                    true
                }
                R.id.rmHourBefore -> {
                    chipReminder.text=menuItem.title
                    eventInfo.reminder=DateTimeManager.getHourBeforeDate(eventInfo.date!!)
                    Log.d("TAG", "Hour ${eventInfo.reminder}")
                    chipReminder.isCloseIconVisible = true
                    true
                }
                R.id.rmDayBefore -> {
                    chipReminder.text=menuItem.title
                    eventInfo.reminder=DateTimeManager.getDayBeforeDate(eventInfo.date!!)
                    Log.d("TAG", "DayHour ${eventInfo.reminder}")
                    chipReminder.isCloseIconVisible = true
                    true
                }
                else -> true
            }
        }
        reminderMenu.show()
    }


    private fun setAlarm(context: Context, eventId: Int, event: Event){
        Log.d("TAG", "eventId ${eventId}")
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, EventReceiver::class.java).apply {
            putExtra(Constants.EVENT_ID, eventId)
            putExtra(Constants.EVENT_TITLE, event.eventTitle)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            eventId,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            time = event.reminder!!
        }

// Устанавливаем время уведомления
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }


    private fun removeAlarm(eventId: Int, event: Event){
        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, EventReceiver::class.java).apply {
            putExtra(Constants.EVENT_ID, eventId)
            putExtra(Constants.EVENT_TITLE, event.eventTitle)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            eventId,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddEditEventFragment()
    }
}