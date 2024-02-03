package com.example.vkr_todolist.presentation.features.task

import android.graphics.Paint
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.vkr_todolist.R
import com.example.vkr_todolist.cache.room.model.Task
import com.example.vkr_todolist.databinding.ItemTaskBinding
import com.example.vkr_todolist.presentation.utils.DateTimeManager

class TaskAdapter(private val listener: TaskListener) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(
    DiffCallback()
){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    class TaskViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val binding = ItemTaskBinding.bind(view)

        fun bind(task: Task, listener: TaskListener) = with(binding){
            tvTaskTitle.text = task.taskTitle
            tvTaskTitle.maxLines = 2
            tvTaskTitle.ellipsize = TextUtils.TruncateAt.END

            if (task.date!=null)
                tvTaskDate.text = DateTimeManager.convertDateToString(task.date!!)
            setColorDate(task)
            tvTaskDate.visibility = infoVisibility(task, DATE)
            imDecs.visibility = infoVisibility(task, DESC)
            imNotification.visibility = infoVisibility(task, REMINDER)
            imAttachment.visibility = infoVisibility(task, IMAGE)

            taskCardView.setOnClickListener {
                listener.onCLickItem(task, EDIT)
            }

            chBox.isChecked=task.taskChecked
            setPaintFlagAndColor(binding)
            chBox.setOnClickListener {
                listener.onCLickItem(task.copy(taskChecked = chBox.isChecked), CHECK_BOX)

            }

            val isStared = task.isImportant
            setPaintStar(isStared)
            layoutImportant.setOnClickListener {
                listener.onCLickItem(task.copy(isImportant = !isStared), STAR)
            }

            root.setOnLongClickListener {
                listener.onCLickItem(task, DELETE)
                true
            }
        }

        private fun setColorDate(task: Task) = with(binding) {
            if (task.date != null) {
                if (DateTimeManager.isDatePassed(task.date!!)) {
                    tvTaskDate.setTextColor(ContextCompat.getColor(binding.root.context, R.color.red))
                } else {
                    tvTaskDate.setTextColor(ContextCompat.getColor(binding.root.context, R.color.hint_text))
                }
            }
        }

        private fun infoVisibility(task: Task, state: Int): Int{
            return when (state) {
                DESC -> {
                    return if (task.taskDescription.isNullOrEmpty()) View.GONE
                    else View.VISIBLE
                }
                REMINDER -> {
                    return if (task.reminder == null) View.GONE
                    else View.VISIBLE
                }
                DATE -> {
                    return if (task.date == null) View.GONE
                    else View.VISIBLE
                }
                IMAGE -> {
                    return if (task.imagePath == null) View.GONE
                    else View.VISIBLE
                }
                else -> {0}
            }
        }

        private fun setPaintStar(state: Boolean){
            binding.apply {
                when(state){
                    true ->  imStar.setImageResource(R.drawable.ic_star)
                    false -> imStar.setImageResource(R.drawable.ic_star_empty)
                }
            }
        }

        private fun setPaintFlagAndColor(binding: ItemTaskBinding){
            binding.apply {
                if(chBox.isChecked){
                    tvTaskTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvTaskTitle.setTextColor(ContextCompat.getColor(binding.root.context, R.color.hint_light))
                } else {
                    tvTaskTitle.paintFlags = Paint.ANTI_ALIAS_FLAG
                    val typedValue = TypedValue()
                    val theme = binding.root.context.theme
                    theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
                    tvTaskTitle.setTextColor(typedValue.data)
                }
            }

        }

        companion object{
            fun create(parent: ViewGroup): TaskViewHolder {
                return TaskViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(
                            R.layout.item_task,
                            parent, false
                        )
                )
            }
        }
    }


    class DiffCallback: DiffUtil.ItemCallback<Task>(){
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.taskId == newItem.taskId
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem==newItem
        }
    }


    interface TaskListener{
        fun onCLickItem(task: Task, state: Int)
    }

    companion object{
        const val CHECK_BOX = 0
        const val EDIT = 1
        const val STAR = 2
        const val DESC = 3
        const val REMINDER = 4
        const val DATE = 5
        const val IMAGE = 6
        const val DELETE = 7
    }
}