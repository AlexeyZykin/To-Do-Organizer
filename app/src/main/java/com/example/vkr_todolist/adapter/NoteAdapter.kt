package com.example.vkr_todolist.adapter

import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.example.vkr_todolist.R
import com.example.vkr_todolist.data.model.Note
import com.example.vkr_todolist.databinding.FragmentNoteBinding
import com.example.vkr_todolist.databinding.ItemNoteLinearBinding
import com.example.vkr_todolist.utils.HtmlManager

class NoteAdapter(private val listener: NoteListener, private val defPref: SharedPreferences): ListAdapter<Note, NoteAdapter.NoteViewHolder>(DiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position), listener, defPref)
    }

    class NoteViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = ItemNoteLinearBinding.bind(view)
        fun bind(note: Note, listener: NoteListener, defPref: SharedPreferences) = with(binding) {
            tvNoteTitle.text = note.noteTitle
            tvNoteTitle.maxLines = 2
            tvNoteTitle.ellipsize = TextUtils.TruncateAt.END

            tvNoteDescription.text = HtmlManager.getFromHtml(note.noteDescription!!).trim()
            tvNoteDescription.maxLines = 5
            tvNoteDescription.ellipsize = TextUtils.TruncateAt.END

            tvNoteTime.text = note.time

            initImage(note, defPref)

            noteLinear.setOnClickListener {
                listener.onCLickItem(note, EDIT)
           }

            root.setOnLongClickListener {
                listener.onCLickItem(note, DELETE)
                true
            }
        }

        private fun initImage(note: Note, defPref: SharedPreferences)=with(binding){
            if(defPref.getString("note_style_key", "Linear") == "Linear"){
                if (note.imagePath != null){
                    centralDivider.visibility = View.VISIBLE
                    imAttachment.visibility=View.VISIBLE
                    imgNote.setImageBitmap(BitmapFactory.decodeFile(note.imagePath))
                    imgNote.visibility = View.VISIBLE
                }else{
                    centralDivider.visibility = View.GONE
                    imAttachment.visibility=View.GONE
                    imgNote.visibility = View.GONE
                }
            }
            else {
                centralDivider.visibility = View.GONE
                imgNote.visibility = View.GONE
                if (note.imagePath != null){
                    imAttachment.visibility=View.VISIBLE
                }else{
                    imAttachment.visibility=View.GONE
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

    class DiffCallback:DiffUtil.ItemCallback<Note>(){
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.noteId == newItem.noteId
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem==newItem
        }
    }


    interface NoteListener {
        fun onCLickItem(note: Note, state: Int)
    }

    companion object{
        const val EDIT = 1
        const val DELETE = 7
    }
}