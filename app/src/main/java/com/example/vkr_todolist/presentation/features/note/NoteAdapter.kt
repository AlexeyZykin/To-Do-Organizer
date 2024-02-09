package com.example.vkr_todolist.presentation.features.note

import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.example.vkr_todolist.R
import com.example.vkr_todolist.cache.room.model.NoteCache
import com.example.vkr_todolist.databinding.ItemNoteLinearBinding
import com.example.vkr_todolist.presentation.model.NoteUi
import com.example.vkr_todolist.presentation.utils.HtmlManager

class NoteAdapter(private val listener: NoteListener, private val defPref: SharedPreferences) :
    ListAdapter<NoteUi, NoteAdapter.NoteViewHolder>(
        DiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position), listener, defPref)
    }

    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemNoteLinearBinding.bind(view)
        fun bind(noteUi: NoteUi, listener: NoteListener, defPref: SharedPreferences) =
            with(binding) {
                tvNoteTitle.text = noteUi.title
                tvNoteTitle.maxLines = 2
                tvNoteTitle.ellipsize = TextUtils.TruncateAt.END

                tvNoteDescription.text = noteUi.description?.let { HtmlManager.getFromHtml(it).trim() }
                tvNoteDescription.maxLines = 5
                tvNoteDescription.ellipsize = TextUtils.TruncateAt.END

                tvNoteTime.text = noteUi.time

                initImage(noteUi, defPref)

                noteLinear.setOnClickListener {
                    listener.onCLickItem(noteUi, EDIT)
                }

                root.setOnLongClickListener {
                    listener.onCLickItem(noteUi, DELETE)
                    true
                }
            }

        private fun initImage(noteUi: NoteUi, defPref: SharedPreferences) = with(binding) {
            if (defPref.getString("note_style_key", "Linear") == "Linear") {
                if (noteUi.imagePath != null) {
                    centralDivider.visibility = View.VISIBLE
                    imAttachment.visibility = View.VISIBLE
                    imgNote.setImageBitmap(BitmapFactory.decodeFile(noteUi.imagePath))
                    imgNote.visibility = View.VISIBLE
                } else {
                    centralDivider.visibility = View.GONE
                    imAttachment.visibility = View.GONE
                    imgNote.visibility = View.GONE
                }
            } else {
                centralDivider.visibility = View.GONE
                imgNote.visibility = View.GONE
                if (noteUi.imagePath != null) {
                    imAttachment.visibility = View.VISIBLE
                } else {
                    imAttachment.visibility = View.GONE
                }
            }
        }


        companion object {
            fun create(parent: ViewGroup): NoteViewHolder {
                return NoteViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(
                            R.layout.item_note_linear,
                            parent, false
                        )
                )
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<NoteUi>() {
        override fun areItemsTheSame(oldItem: NoteUi, newItem: NoteUi): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NoteUi, newItem: NoteUi): Boolean {
            return oldItem == newItem
        }
    }


    interface NoteListener {
        fun onCLickItem(noteUi: NoteUi, state: Int)
    }

    companion object {
        const val EDIT = 1
        const val DELETE = 7
    }
}