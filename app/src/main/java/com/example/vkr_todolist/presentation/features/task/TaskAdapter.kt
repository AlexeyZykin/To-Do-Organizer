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
import com.example.vkr_todolist.cache.room.model.TaskCache
import com.example.vkr_todolist.databinding.ItemTaskBinding
import com.example.vkr_todolist.presentation.model.TaskUi
import com.example.vkr_todolist.presentation.utils.DateTimeManager

class TaskAdapter(private val listener: TaskListener) :
    ListAdapter<TaskUi, TaskAdapter.TaskViewHolder>(
        DiffCallback()
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemTaskBinding.bind(view)

        fun bind(taskUi: TaskUi, listener: TaskListener) = with(binding) {
            tvTaskTitle.text = taskUi.title
            tvTaskTitle.maxLines = 2
            tvTaskTitle.ellipsize = TextUtils.TruncateAt.END

            taskUi.date?.let {
                tvTaskDate.text = DateTimeManager.convertDateToString(taskUi.date!!)
            }
            setColorDate(taskUi)
            tvTaskDate.visibility = infoVisibility(taskUi, DATE)
            imDecs.visibility = infoVisibility(taskUi, DESC)
            imNotification.visibility = infoVisibility(taskUi, REMINDER)
            imAttachment.visibility = infoVisibility(taskUi, IMAGE)

            taskCardView.setOnClickListener {
                listener.onCLickItem(taskUi, EDIT)
            }

            chBox.isChecked = taskUi.checked
            setPaintFlagAndColor(binding)
            chBox.setOnClickListener {
                listener.onCLickItem(taskUi.copy(checked = chBox.isChecked), CHECK_BOX)

            }

            val isStared = taskUi.isImportant
            setPaintStar(isStared)
            layoutImportant.setOnClickListener {
                listener.onCLickItem(taskUi.copy(isImportant = !isStared), STAR)
            }

            root.setOnLongClickListener {
                listener.onCLickItem(taskUi, DELETE)
                true
            }
        }

        private fun setColorDate(taskUi: TaskUi) = with(binding) {
            if (taskUi.date != null) {
                if (DateTimeManager.isDatePassed(taskUi.date!!)) {
                    tvTaskDate.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.red
                        )
                    )
                } else {
                    tvTaskDate.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.hint_text
                        )
                    )
                }
            }
        }

        private fun infoVisibility(taskUi: TaskUi, state: Int): Int {
            return when (state) {
                DESC -> {
                    return if (taskUi.description.isNullOrEmpty()) View.GONE
                    else View.VISIBLE
                }

                REMINDER -> {
                    return if (taskUi.reminder == null) View.GONE
                    else View.VISIBLE
                }

                DATE -> {
                    return if (taskUi.date == null) View.GONE
                    else View.VISIBLE
                }

                IMAGE -> {
                    return if (taskUi.imagePath == null) View.GONE
                    else View.VISIBLE
                }

                else -> {
                    0
                }
            }
        }

        private fun setPaintStar(state: Boolean) {
            binding.apply {
                when (state) {
                    true -> imStar.setImageResource(R.drawable.ic_star)
                    false -> imStar.setImageResource(R.drawable.ic_star_empty)
                }
            }
        }

        private fun setPaintFlagAndColor(binding: ItemTaskBinding) {
            binding.apply {
                if (chBox.isChecked) {
                    tvTaskTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvTaskTitle.setTextColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.hint_light
                        )
                    )
                } else {
                    tvTaskTitle.paintFlags = Paint.ANTI_ALIAS_FLAG
                    val typedValue = TypedValue()
                    val theme = binding.root.context.theme
                    theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
                    tvTaskTitle.setTextColor(typedValue.data)
                }
            }

        }

        companion object {
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


    class DiffCallback : DiffUtil.ItemCallback<TaskUi>() {
        override fun areItemsTheSame(oldItem: TaskUi, newItem: TaskUi): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TaskUi, newItem: TaskUi): Boolean {
            return oldItem == newItem
        }
    }


    interface TaskListener {
        fun onCLickItem(taskUi: TaskUi, state: Int)
    }

    companion object {
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