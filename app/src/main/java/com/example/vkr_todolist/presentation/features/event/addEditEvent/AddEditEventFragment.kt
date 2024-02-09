package com.example.vkr_todolist.presentation.features.event.addEditEvent

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.vkr_todolist.NavGraphDirections
import com.example.vkr_todolist.R
import com.example.vkr_todolist.databinding.FragmentAddEditEventBinding
import com.example.vkr_todolist.presentation.features.event.EventReceiver
import com.example.vkr_todolist.presentation.model.EventUi
import com.example.vkr_todolist.presentation.model.ListUi
import com.example.vkr_todolist.presentation.utils.Constants
import com.example.vkr_todolist.presentation.utils.DateTimeManager
import com.example.vkr_todolist.presentation.utils.NotificationHelper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class AddEditEventFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddEditEventBinding
    private lateinit var popupMenu: PopupMenu
    private lateinit var reminderMenu: PopupMenu
    private val args: AddEditEventFragmentArgs by navArgs()
    private var isCategorySelected = false
    private val viewModel by viewModel<AddEditEventViewModel>()
    private val listUi = ListUi(null, "")
    private val eventUi = EventUi(
        null,
        "",
        listUi,
        Calendar.getInstance().time,
        Calendar.getInstance().time,
        false
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddEditEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.eventId?.let { viewModel.getEvent(it.first()) }
        args.listId?.let { viewModel.getList(it.first()) }
        viewModel.getAllLists()
        subscribeObservers()
        NotificationHelper.createNotificationChannel(requireContext())
        binding.bSaveEvent.setOnClickListener { createNewEvent() }
        binding.chipSelectList.setOnClickListener { initFilterList() }
        binding.chipDatePicker.setOnClickListener { showDatePicker() }
        binding.chipDatePicker.setOnCloseIconClickListener { closeIconChipDatePicker() }
        binding.chipReminder.setOnCloseIconClickListener { closeIconChipReminder() }
        binding.chipReminder.setOnClickListener {
            if (binding.chipDatePicker.text == getString(R.string.due_date))
                Snackbar.make(binding.root, R.string.snackbar_datePicker, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show()
            else
                setRemindTime()
        }
    }

    private fun closeIconChipReminder() {
        eventUi.reminder = null
        binding.chipReminder.setText(R.string.reminder)
        binding.chipReminder.isCloseIconVisible = false
    }

    private fun closeIconChipDatePicker() {
        binding.chipDatePicker.setText(R.string.due_date)
        binding.chipDatePicker.isCloseIconVisible = false
        closeIconChipReminder()
    }

    private fun subscribeObservers() {
        viewModel.listItem.observe(viewLifecycleOwner) {
            it?.let {
                binding.chipSelectList.text = it.title
                eventUi.list = it
            }
        }
        viewModel.event.observe(viewLifecycleOwner) { event ->
            binding.edTitle.setText(event.title)
            binding.chipDatePicker.text = DateTimeManager.convertTimeToString(event.date)
            binding.chipDatePicker.isCloseIconVisible = true
            event.reminder?.let {
                binding.chipReminder.isCloseIconVisible = true
                when (it) {
                    DateTimeManager.getHalfHourBeforeDate(event.date) -> {
                        binding.chipReminder.text = getString(R.string.remind_half_hour_before)
                    }

                    DateTimeManager.getHourBeforeDate(event.date) -> {
                        binding.chipReminder.text = getString(R.string.remind_hour_before)
                    }

                    DateTimeManager.getDayBeforeDate(event.date) -> {
                        binding.chipReminder.text = getString(R.string.remind_day_before)
                    }
                }
            }
            isCategorySelected = true
            binding.chipSelectList.text = event.list.title
            eventUi.apply {
                id = event.id
                title = event.title
                list = event.list
                date = event.date
                reminder = event.reminder
                isFinished = event.isFinished
            }
        }
    }

    private fun createNewEvent() {
        when {
            binding.edTitle.text.isNullOrBlank() -> Snackbar.make(
                binding.root,
                R.string.snackbar_addTitle,
                Snackbar.LENGTH_SHORT
            ).setAction("Action", null).show()

            binding.chipDatePicker.text == getString(R.string.due_date) -> Snackbar.make(
                binding.root,
                R.string.snackbar_datePicker,
                Snackbar.LENGTH_SHORT
            )
                .setAction("Action", null).show()

            binding.chipSelectList.text == getString(R.string.select_list) -> Snackbar.make(
                binding.root,
                R.string.snackbar_selectList,
                Snackbar.LENGTH_SHORT
            )
                .setAction("Action", null).show()

            else -> {
                if (args.eventId == null) {
                    eventUi.title = binding.edTitle.text.toString()

                    lifecycleScope.launch {
                        val eventId = viewModel.addEvent(eventUi).await()

                        if (eventUi.reminder != null)
                            setAlarm(requireContext(), eventId.toInt(), eventUi)
                        findNavController().popBackStack()
                    }
                } else {
                    updateEvent()
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun updateEvent() {
        lifecycleScope.launch {
            eventUi.apply {
                title = binding.edTitle.text.toString()
            }
            viewModel.updateEvent(eventUi)
            if (eventUi.reminder != null)
                eventUi.id?.let { setAlarm(requireContext(), it, eventUi) }
            else
                eventUi.id?.let { removeAlarm(it, eventUi) }
        }
    }

    private fun initFilterList() {
        popupMenu = PopupMenu(context?.applicationContext, binding.chipSelectList)
        viewModel.lists.observe(viewLifecycleOwner) {
            it?.forEach { list ->
                popupMenu.menu.add(list.title)
                    .setIcon(R.drawable.ic_notes)
                    .setOnMenuItemClickListener { menuItem ->
                        eventUi.list.id = list.id
                        eventUi.list.title = list.title
                        binding.chipSelectList.text = menuItem.title
                        isCategorySelected = true
                        true
                    }
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

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker().build()
        val isSystem24Hour = DateFormat.is24HourFormat(requireContext())
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
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
            eventUi.date = calendar.time
            binding.chipDatePicker.text = DateTimeManager.convertDateToString(eventUi.date)
            timePicker.show(childFragmentManager, "TAG")
        }

        timePicker.addOnPositiveButtonClickListener {
            val cal = Calendar.getInstance()
            cal.time = eventUi.date
            cal.set(Calendar.HOUR_OF_DAY, timePicker.hour)
            cal.set(Calendar.MINUTE, timePicker.minute)
            eventUi.date = cal.time
            binding.chipDatePicker.text = DateTimeManager.convertTimeToString(eventUi.date)
            binding.chipDatePicker.isCloseIconVisible = true
            eventUi.reminder=null
            closeIconChipReminder()
        }
        datePicker.show(childFragmentManager, "TAG")
    }

    private fun setRemindTime() = with(binding) {
        reminderMenu = PopupMenu(requireContext().applicationContext, chipReminder)
        reminderMenu.menuInflater.inflate(R.menu.filter_reminder_menu, reminderMenu.menu)
        reminderMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.rmHalfHourBefore -> {
                    chipReminder.text = menuItem.title
                    eventUi.reminder = DateTimeManager.getHalfHourBeforeDate(eventUi.date)
                    chipReminder.isCloseIconVisible = true
                    true
                }

                R.id.rmHourBefore -> {
                    chipReminder.text = menuItem.title
                    eventUi.reminder = DateTimeManager.getHourBeforeDate(eventUi.date)
                    chipReminder.isCloseIconVisible = true
                    true
                }

                R.id.rmDayBefore -> {
                    chipReminder.text = menuItem.title
                    eventUi.reminder = DateTimeManager.getDayBeforeDate(eventUi.date)
                    chipReminder.isCloseIconVisible = true
                    true
                }
                else -> true
            }
        }
        reminderMenu.show()
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setAlarm(context: Context, eventId: Int, eventUi: EventUi) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, EventReceiver::class.java).apply {
            putExtra(Constants.EVENT_ID, eventId)
            putExtra(Constants.EVENT_TITLE, eventUi.title)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            eventId,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val calendar = Calendar.getInstance().apply {
            time = eventUi.reminder!!
        }
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    private fun removeAlarm(eventId: Int, eventUi: EventUi) {
        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, EventReceiver::class.java).apply {
            putExtra(Constants.EVENT_ID, eventId)
            putExtra(Constants.EVENT_TITLE, eventUi.title)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            eventId,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}