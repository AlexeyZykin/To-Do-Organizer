package com.example.vkr_todolist.presentation.features.list

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.vkr_todolist.R
import com.example.vkr_todolist.presentation.features.note.NoteAdapter
import com.example.vkr_todolist.databinding.FragmentListOfNotesBinding
import com.example.vkr_todolist.presentation.dialogs.DeleteDialog
import com.example.vkr_todolist.presentation.features.note.NoteViewModel
import com.example.vkr_todolist.presentation.model.NoteUi
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListOfNotesFragment : Fragment(), NoteAdapter.NoteListener {
    private lateinit var binding: FragmentListOfNotesBinding
    private lateinit var adapter: NoteAdapter
    private lateinit var defPref: SharedPreferences
    private val viewModel by viewModel<NoteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListOfNotesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listId = arguments?.getInt(LIST_NAME)
        listId?.let { viewModel.getNotesByList(listId) }
        subscribeObserver()
        initRecyclerView()
        initSwipe()
        binding.fabNewNote.setOnClickListener {
            val action =
                ListFragmentDirections.actionListFragmentToAddEditNoteFragment(null,
                    listId?.let { id -> intArrayOf(id) })
            findNavController().navigate(action)
        }
    }

    private fun subscribeObserver() {
        viewModel.notes.observe(viewLifecycleOwner) {
            it?.let { adapter.submitList(it) }
            binding.emptyViewNote.run {
                visibility = if (it.isNullOrEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun initSwipe() {
        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                val note = adapter.currentList[pos]
                note.id?.let { viewModel.deleteNote(it) }
                Snackbar.make(binding.root, R.string.snackbar_deleted, Snackbar.LENGTH_LONG)
                    .apply {
                        setAction(R.string.undo) {
                            viewModel.addNote(note)
                        }
                        show()
                    }
            }

        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        itemTouchHelper.attachToRecyclerView(binding.rcViewNote)
    }


    private fun initRecyclerView() = with(binding) {
        defPref = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        rcViewNote.layoutManager = getLayoutManager()
        adapter = NoteAdapter(this@ListOfNotesFragment, defPref)
        rcViewNote.adapter = adapter
    }

    private fun getLayoutManager(): RecyclerView.LayoutManager {
        return if (defPref.getString("note_style_key", "Linear") == "Linear") {
            LinearLayoutManager(activity)
        } else {
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }

    private fun deleteNote(noteUi: NoteUi) {
        DeleteDialog.showDeleteNote(context as AppCompatActivity, object : DeleteDialog.Listener {
            override fun onClick() {
                noteUi.id?.let { viewModel.deleteNote(it) }
            }
        })
    }

    override fun onCLickItem(noteUi: NoteUi, state: Int) {
        when (state) {
            NoteAdapter.EDIT -> {
                val action =
                    ListFragmentDirections.actionListFragmentToAddEditNoteFragment(noteUi.id?.let {
                        intArrayOf(it)
                    }, null)
                findNavController().navigate(action)
            }

            NoteAdapter.DELETE -> {
                deleteNote(noteUi)
            }
        }
    }

    companion object {
        const val LIST_NAME = "list_name"
    }
}