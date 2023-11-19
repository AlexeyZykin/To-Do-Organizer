package com.example.vkr_todolist.ui.dialogs

import android.content.Context
import com.example.vkr_todolist.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object DeleteDialog {

    fun showDialog(context: Context, listener: Listener) {
    MaterialAlertDialogBuilder(context)
        .setTitle(context.getString(R.string.delete_list))
        .setMessage(context.getString(R.string.delete_list_dialog))
        .setPositiveButton(context.getString(R.string.delete)) { _, _ ->
            listener.onClick()
        }
        .setNegativeButton(context.getString(R.string.cancel_dialog), null)
        .show()
   }

    fun showCompTasksDialog(context: Context, listener: Listener){
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.delete_completed_tasks))
            .setMessage(context.getString(R.string.delete_comp_tasks_dialog))
            .setPositiveButton(context.getString(R.string.delete)) { _, _ ->
                listener.onClick()
            }
            .setNegativeButton(context.getString(R.string.cancel_dialog), null)
            .show()
    }

    fun showDeleteEndEvents(context: Context, listener: Listener){
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.delete_end_events))
            .setMessage(context.getString(R.string.delete_end_events_dialog))
            .setPositiveButton(context.getString(R.string.delete)) { _, _ ->
                listener.onClick()
            }
            .setNegativeButton(context.getString(R.string.cancel_dialog), null)
            .show()
    }

    fun showDeleteTask(context: Context, listener: Listener){
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.delete_task))
            .setMessage(context.getString(R.string.delete_task_dialog))
            .setPositiveButton(context.getString(R.string.delete)) { _, _ ->
                listener.onClick()
            }
            .setNegativeButton(context.getString(R.string.cancel_dialog), null)
            .show()
    }


    fun showDeleteEvent(context: Context, listener: Listener){
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.delete_event))
            .setMessage(context.getString(R.string.delete_event_dialog))
            .setPositiveButton(context.getString(R.string.delete)) { _, _ ->
                listener.onClick()
            }
            .setNegativeButton(context.getString(R.string.cancel_dialog), null)
            .show()
    }


    fun showDeleteNote(context: Context, listener: Listener){
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.delete_note))
            .setMessage(context.getString(R.string.delete_note_dialog))
            .setPositiveButton(context.getString(R.string.delete)) { _, _ ->
                listener.onClick()
            }
            .setNegativeButton(context.getString(R.string.cancel_dialog), null)
            .show()
    }

    interface Listener{
        fun onClick()
    }
}