package com.example.vkr_todolist.presentation.features.task

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.format.DateFormat.is24HourFormat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.vkr_todolist.NavGraphDirections
import com.example.vkr_todolist.R
import com.example.vkr_todolist.databinding.FragmentAddEditTaskBinding
import com.example.vkr_todolist.presentation.model.ListUi
import com.example.vkr_todolist.presentation.model.TaskUi
import com.example.vkr_todolist.presentation.utils.Constants
import com.example.vkr_todolist.presentation.utils.DateTimeManager
import com.example.vkr_todolist.presentation.utils.NotificationHelper
import com.example.vkr_todolist.presentation.utils.PermissionsUtil
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class AddEditTaskFragment : Fragment() {
    private lateinit var binding: FragmentAddEditTaskBinding
    private lateinit var popupMenu: PopupMenu
    private val args: AddEditTaskFragmentArgs by navArgs()
    private val permissionLauncher =
        PermissionsUtil.registerForPermissionsResult(this@AddEditTaskFragment)
    private val listUi = ListUi(null, "")
    private val taskUi = TaskUi(
        null,
        "",
        null,
        false,
        listUi,
        null,
        Calendar.getInstance().time,
        false,
        null,
        null
    )
    private var isCategorySelected = false
    private val viewModel by viewModel<AddEditTaskViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddEditTaskBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.taskId?.let { viewModel.getTask(it.first()) }
        args.listId?.let { viewModel.getList(it.first()) }
        viewModel.getAllLists()
        subscribeObservers()
        NotificationHelper.createNotificationChannel(requireContext())
        binding.fabCreateTask.setOnClickListener { createNewTask() }
        binding.chipSelectList.setOnClickListener { initFilterList() }
        binding.chipDatePicker.setOnClickListener { showDatePicker() }
        binding.chipReminder.setOnClickListener { setAlarm() }
        binding.chipDatePicker.setOnCloseIconClickListener { closeIconDatePicker() }
        binding.chipReminder.setOnCloseIconClickListener { closeIconReminder() }
        binding.chipImage.setOnClickListener { pickImageFromGallery() }
        binding.chipImage.setOnCloseIconClickListener { closeIconImagePicker() }
    }

    private fun closeIconDatePicker() {
        taskUi.date = null
        binding.chipDatePicker.setText(R.string.due_date)
        binding.chipDatePicker.isCloseIconVisible = false
    }

    private fun closeIconReminder() {
        taskUi.reminder = null
        binding.chipReminder.setText(R.string.reminder)
        binding.chipReminder.isCloseIconVisible = false
    }

    private fun closeIconImagePicker() {
        taskUi.imagePath = null
        binding.imgCardView.visibility = View.GONE
        binding.imgPick.setImageBitmap(null)
        binding.chipImage.isCloseIconVisible = false
    }

    private fun initFilterList() {
        popupMenu = PopupMenu(context?.applicationContext, binding.chipSelectList)
        viewModel.lists.observe(viewLifecycleOwner) {
            it?.forEach { list ->
                popupMenu.menu.add(list.title)
                    .setIcon(R.drawable.ic_notes)
                    .setOnMenuItemClickListener { menuItem ->
                        taskUi.list = list
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


    private fun createNewTask() {
        when {
            binding.edTaskTitle.text.isNullOrBlank() ->
                Snackbar.make(binding.root, R.string.snackbar_addTitle, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show()

            binding.chipSelectList.text == getString(R.string.select_list) ->
                Snackbar.make(binding.root, R.string.snackbar_selectList, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show()

            else -> {
                if (args.taskId == null) {
                    taskUi.apply {
                        title = binding.edTaskTitle.text.toString()
                        description = binding.edTaskDesc.text.toString()
                        checked = false
                        createdDate = DateTimeManager.getCurrentDate()
                    }
                    lifecycleScope.launch {
                        val taskId = viewModel.addTask(taskUi).await()
                        if (taskUi.reminder != null)
                            setNotification(requireContext(), taskId.toInt(), taskUi)
                    }
                }
                else updateTask()
                findNavController().popBackStack()
            }
        }
    }


    private fun subscribeObservers() {
        viewModel.listItem.observe(viewLifecycleOwner) {
            it?.let {
                binding.chipSelectList.text = it.title
                taskUi.list = it
            }
        }
        viewModel.task.observe(viewLifecycleOwner) {
            it?.let {
                binding.edTaskTitle.setText(it.title)
                binding.edTaskDesc.setText(it.description)
                it.date?.let { date ->
                    binding.chipDatePicker.text = DateTimeManager.convertDateToString(date)
                    binding.chipDatePicker.isCloseIconVisible = true
                }
                it.reminder?.let { reminder ->
                    binding.chipReminder.text = DateTimeManager.convertTimeToString(reminder)
                    binding.chipReminder.isCloseIconVisible = true
                }
                binding.chipSelectList.text = it.list.title
                isCategorySelected = true
                it.imagePath?.let { imagePath ->
                    binding.imgPick.setImageBitmap(BitmapFactory.decodeFile(imagePath))
                    binding.chipImage.isCloseIconVisible = true
                }
                taskUi.apply {
                    id = it.id
                    title = it.title
                    description = it.description
                    checked = it.checked
                    list = it.list
                    date = it.date
                    createdDate = it.createdDate
                    isImportant = it.isImportant
                    reminder = it.reminder
                    imagePath = it.imagePath
                }
            }
        }
    }


    private fun updateTask() {
        lifecycleScope.launch {
            taskUi.apply {
                title = binding.edTaskTitle.text.toString()
                description = binding.edTaskDesc.text.toString()
            }
            viewModel.updateTask(taskUi)
            if (taskUi.reminder != null)
                taskUi.id?.let { setNotification(requireContext(), it, taskUi) }
            else
                taskUi.id?.let { removeAlarm(it, taskUi) }
        }
    }


    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker().build()
        datePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            taskUi.date = calendar.time
            binding.chipDatePicker.text = DateTimeManager.convertDateToString(taskUi.date!!)
            binding.chipDatePicker.isCloseIconVisible = true
        }
        datePicker.show(childFragmentManager, "TAG")
    }


    private fun setAlarm() {
        val datePicker = MaterialDatePicker.Builder.datePicker().build()
        val isSystem24Hour = is24HourFormat(requireContext())
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
            taskUi.reminder = calendar.time
            binding.chipReminder.text =
                DateTimeManager.convertTimeToString(taskUi.reminder!!)
            timePicker.show(childFragmentManager, "TAG")
        }

        timePicker.addOnPositiveButtonClickListener {
            val cal = Calendar.getInstance()
            cal.time = taskUi.reminder!!
            cal.set(Calendar.HOUR_OF_DAY, timePicker.hour)
            cal.set(Calendar.MINUTE, timePicker.minute)
            taskUi.reminder = cal.time
            binding.chipReminder.text =
                DateTimeManager.convertTimeToString(taskUi.reminder!!)
            binding.chipReminder.isCloseIconVisible = true
        }
        datePicker.show(childFragmentManager, "TAG")
    }


    @SuppressLint("ScheduleExactAlarm")
    fun setNotification(context: Context, taskId: Int, taskUi: TaskUi) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, TaskReceiver::class.java).apply {
            putExtra(Constants.TASK_ID, taskId)
            putExtra(Constants.TASK_TITLE, taskUi.title)
            putExtra(Constants.TASK_DESCRIPTION, taskUi.description)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            time = taskUi.reminder!!
        }

// Устанавливаем время уведомления
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }


    private fun removeAlarm(taskId: Int, taskUi: TaskUi) {
        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, TaskReceiver::class.java).apply {
            putExtra(Constants.TASK_ID, taskId)
            putExtra(Constants.TASK_TITLE, taskUi.title)
            putExtra(Constants.TASK_DESCRIPTION, taskUi.description)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            taskId,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }


    private fun pickImageFromGallery() {
        if (PermissionsUtil.hasGalleryPermission(this@AddEditTaskFragment)) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startForProfileImageResult.launch(intent)
            }
        } else {
            permissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }
    }

    private fun getPathFromUri(contentUri: Uri): String? {
        var filePath: String? = null
        val cursor = requireActivity().contentResolver.query(contentUri, null, null, null, null)
        if (cursor == null) {
            filePath = contentUri.path
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }


    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!

                // mProfileUri = fileUri
                binding.imgPick.setImageURI(fileUri)
                taskUi.imagePath = getPathFromUri(fileUri)!!
                binding.chipImage.isCloseIconVisible = true
                binding.imgCardView.visibility = View.VISIBLE
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
}