package com.example.vkr_todolist.presentation.features.event
import android.content.Context
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.vkr_todolist.R
import com.example.vkr_todolist.cache.room.model.Event
import com.example.vkr_todolist.databinding.ItemEventsBinding
import com.example.vkr_todolist.presentation.utils.DateTimeManager


class EventAdapter(private val listener: EventListener, private val context: Context): ListAdapter<Event, EventAdapter.EventViewHolder>(
    DiffCallback()
){



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventViewHolder {
        return EventViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position), listener, context)
    }

    class EventViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var countDownTimer: CountDownTimer? = null
       private val binding = ItemEventsBinding.bind(view)
        fun bind(event: Event, listener: EventListener, context: Context)= with(binding)  {
            tvEventTitle.text = event.eventTitle
            tvListName.text = event.listName

            if(event.date!! > DateTimeManager.getCurrentDate())
                countDownTimer(event, context)
            else
                countTime.text = context.getString(R.string.countdown_finished)

            eventCardView.setOnClickListener {
                listener.onCLickItem(event, EDIT)
            }

            root.setOnLongClickListener {
                listener.onCLickItem(event, DELETE)
                true
            }
        }


        private fun countDownTimer(event: Event, context: Context){
            countDownTimer?.cancel()
            countDownTimer = object : CountDownTimer(
                event.date!!.time - System.currentTimeMillis(),
                1000
            ) {
                override fun onTick(millisUntilFinished: Long) {
                    binding.countTime.text = getTimeString(millisUntilFinished)
                }

                override fun onFinish() {
                    binding.countTime.text = context.getString(R.string.countdown_finished)
                }
            }
            (countDownTimer as CountDownTimer).start()
        }


        private fun getTimeString(millis: Long): String {
            val seconds = millis / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            return when {
                days > 0 -> "${days}д ${hours % 24}ч"
                hours > 0  -> "${hours}ч ${minutes % 60}мин"
                else -> "${minutes % 60}мин ${seconds % 60}сек"
            }
        }


        companion object {
            fun create(parent: ViewGroup): EventViewHolder {
                return EventViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(
                            R.layout.item_events,
                            parent, false
                        )
                )
            }
        }
    }

    interface EventListener {
        fun onCLickItem(event: Event, state: Int)
    }


    class DiffCallback: DiffUtil.ItemCallback<Event>(){
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.eventId == newItem.eventId
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem==newItem
        }
    }

    companion object{
        const val EDIT = 1
        const val DELETE = 7
    }
}