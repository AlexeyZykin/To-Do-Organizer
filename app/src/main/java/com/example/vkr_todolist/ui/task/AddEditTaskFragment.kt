package com.example.vkr_todolist.ui.task

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
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.vkr_todolist.App
import com.example.vkr_todolist.NavGraphDirections
import com.example.vkr_todolist.R
import com.example.vkr_todolist.data.model.Task
import com.example.vkr_todolist.databinding.FragmentAddEditTaskBinding
import com.example.vkr_todolist.ui.alarm.TaskReceiver
import com.example.vkr_todolist.ui.main.MainViewModel
import com.example.vkr_todolist.utils.Constants
import com.example.vkr_todolist.utils.DateTimeManager
import com.example.vkr_todolist.utils.NotificationHelper
import com.example.vkr_todolist.utils.PermissionsUtil
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.launch
import java.util.*


class AddEditTaskFragment : Fragment() {
    private lateinit var binding: FragmentAddEditTaskBinding
    private lateinit var popupMenu: PopupMenu
    private val args: AddEditTaskFragmentArgs by navArgs()
    private val permissionLauncher = PermissionsUtil.registerForPermissionsResult(this@AddEditTaskFragment)

    private var taskInfo =
        Task(null, "","",false, null,
            null, DateTimeManager.getCurrentDate(),false, null,null)
    private var isCategorySelected = false


    private val viewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as App).database)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddEditTaskBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabCreateTask.setOnClickListener {
            createNewTask()
        }
        binding.chipSelectList.setOnClickListener {
            initFilterList()
        }
        binding.chipDatePicker.setOnClickListener { showDatePicker() }
        binding.chipReminder.setOnClickListener { setAlarm() }
        binding.chipDatePicker.setOnCloseIconClickListener {
            taskInfo.date = null
            binding.chipDatePicker.setText(R.string.due_date)
            binding.chipDatePicker.isCloseIconVisible=false
        }
        binding.chipReminder.setOnCloseIconClickListener {
            taskInfo.reminder = null
            binding.chipReminder.setText(R.string.reminder)
            binding.chipReminder.isCloseIconVisible=false
        }
        binding.chipImage.setOnClickListener {
            pickImageFromGallery()
        }
        binding.chipImage.setOnCloseIconClickListener {
            binding.imgCardView.visibility = View.GONE
            taskInfo.imagePath = null
            binding.imgPick.setImageBitmap(null)
            binding.chipImage.isCloseIconVisible=false
        }
        NotificationHelper.createNotificationChannel(requireContext())
        initUpdate()
    }


    private fun initFilterList()= with(binding){
        popupMenu = PopupMenu(context?.applicationContext, chipSelectList)
        viewModel.allListItem.value?.forEach{listItem->
            popupMenu.menu.add(listItem.listTitle)
                .setIcon(R.drawable.ic_notes)
                .setOnMenuItemClickListener {menuItem->
                chipSelectList.text=menuItem.title
                taskInfo.listId = listItem.listId
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


    private fun createNewTask()=with(binding){
        if(edTaskTitle.text.isNullOrBlank())
            Snackbar.make(binding.root, R.string.snackbar_addTitle, Snackbar.LENGTH_SHORT).setAction("Action", null).show()
        else if(taskInfo.listId == null)
            Snackbar.make(binding.root, R.string.snackbar_selectList, Snackbar.LENGTH_SHORT).setAction("Action", null).show()
        else {
            if(args.task == null) {
                val tempTask = Task(
                    null,
                    binding.edTaskTitle.text.toString(),
                    binding.edTaskDesc.text.toString(),
                    false,
                    taskInfo.listId,
                    taskInfo.date,
                    taskInfo.createdDate,
                    taskInfo.isImportant,
                    taskInfo.reminder,
                    taskInfo.imagePath
                )

                lifecycleScope.launch {
                    val taskId = viewModel.insertTask(tempTask).await()
                    Log.d("TAG", "taskId: ${taskId}")
                    // Устанавливаем уведомление
                    if (tempTask.reminder != null)
                        setNotification(requireContext(), taskId.toInt(), tempTask)
                    findNavController().popBackStack()
                }
            }
            else{
                updateTask()
                findNavController().popBackStack()
            }
        }

    }


    private fun initUpdate() = with (binding){
        if(args.task!=null){
            edTaskTitle.setText(args.task!!.taskTitle)
            edTaskDesc.setText(args.task?.taskDescription!!)

            taskInfo.date = args.task?.date
            taskInfo.createdDate = args.task?.createdDate
            taskInfo.reminder = args.task?.reminder
            if(taskInfo.date != null) {
                chipDatePicker.text = DateTimeManager.convertDateToString(taskInfo.date!!)
                chipDatePicker.isCloseIconVisible = true
            }
            if(taskInfo.reminder != null) {
                chipReminder.text = DateTimeManager.convertTimeToString(taskInfo.reminder!!)
                chipReminder.isCloseIconVisible = true
            }

            taskInfo.isImportant=args.task?.isImportant!!

            if(args.task!!.imagePath != null){
                chipImage.isCloseIconVisible = true
                taskInfo.imagePath = args.task!!.imagePath
                imgPick.setImageBitmap(BitmapFactory.decodeFile(taskInfo.imagePath))
            }

            taskInfo.listId=args.task?.listId!!
            isCategorySelected=true
            viewModel.getListById(taskInfo.listId!!).observe(viewLifecycleOwner){
                it.forEach {
                    chipSelectList.text = it.listTitle
                }
            }
        }

        if(args.listName!= null){
            chipSelectList.text = args.listName!!.listTitle
            taskInfo.listId = args.listName!!.listId
        }
    }


    private fun updateTask() = with(binding){
        val tempTask = args.task?.copy(
            taskTitle = edTaskTitle.text.toString(),
            taskDescription = edTaskDesc.text.toString(),
            listId = taskInfo.listId,
            date = taskInfo.date,
            reminder = taskInfo.reminder,
            isImportant = taskInfo.isImportant,
            imagePath = taskInfo.imagePath
        )
        lifecycleScope.launch {
            viewModel.updateTask(tempTask!!)
            if (tempTask.reminder != null)
                setNotification(requireContext(), tempTask.taskId!!, tempTask)
            else
                removeAlarm(tempTask.taskId!!, tempTask)
        }
    }


    private fun showDatePicker(){
        val datePicker = MaterialDatePicker.Builder.datePicker().build()
        datePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            taskInfo.date = calendar.time
            binding.chipDatePicker.text = DateTimeManager.convertDateToString(taskInfo.date!!)
            binding.chipDatePicker.isCloseIconVisible = true
        }
        datePicker.show(childFragmentManager,"TAG")
    }


    private fun setAlarm(){
        val datePicker = MaterialDatePicker.Builder.datePicker().build()
        val isSystem24Hour = is24HourFormat(requireContext())
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
            taskInfo.reminder = calendar.time
            binding.chipReminder.text = DateTimeManager.convertTimeToString(taskInfo.reminder!!)
            timePicker.show(childFragmentManager, "TAG")
        }

        timePicker.addOnPositiveButtonClickListener {
            val cal = Calendar.getInstance()
            cal.time=taskInfo.reminder!!
            cal.set(Calendar.HOUR_OF_DAY, timePicker.hour)
            cal.set(Calendar.MINUTE, timePicker.minute)
            taskInfo.reminder = cal.time
            binding.chipReminder.text = DateTimeManager.convertTimeToString(taskInfo.reminder!!)
            binding.chipReminder.isCloseIconVisible = true
        }
        datePicker.show(childFragmentManager,"TAG")
    }


    fun setNotification(context: Context, taskId: Int, task: Task) {
        Log.d("TAG", "taskId: ${taskId}")
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, TaskReceiver::class.java).apply {
            putExtra(Constants.TASK_ID, taskId)
            putExtra(Constants.TASK_TITLE, task.taskTitle)
            putExtra(Constants.TASK_DESCRIPTION, task.taskDescription)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            time = task.reminder!!
        }

// Устанавливаем время уведомления
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }


    private fun removeAlarm(taskId: Int, task: Task){
        Log.d("TAG", "removeAlarm")
        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, TaskReceiver::class.java).apply {
            putExtra(Constants.TASK_ID, taskId)
            putExtra(Constants.TASK_TITLE, task.taskTitle)
            putExtra(Constants.TASK_DESCRIPTION, task.taskDescription)
        }
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), taskId, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
    }


    private fun pickImageFromGallery(){
        if (PermissionsUtil.hasGalleryPermission(this@AddEditTaskFragment)) {
            val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            if (intent.resolveActivity(requireActivity().packageManager) != null){
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
        var filePath:String? = null
        val cursor = requireActivity().contentResolver.query(contentUri,null,null,null,null)
        if (cursor == null){
            filePath = contentUri.path
        }else{
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
                taskInfo.imagePath = getPathFromUri(fileUri)!!
                binding.chipImage.isCloseIconVisible = true
                binding.imgCardView.visibility = View.VISIBLE
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }


    companion object {
        @JvmStatic
        fun newInstance() = AddEditTaskFragment()
    }
}