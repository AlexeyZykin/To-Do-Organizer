package com.example.vkr_todolist.ui.list

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.vkr_todolist.App
import com.example.vkr_todolist.R
import com.example.vkr_todolist.adapter.NoteAdapter
import com.example.vkr_todolist.data.model.ListItem
import com.example.vkr_todolist.data.model.Note
import com.example.vkr_todolist.databinding.FragmentListOfNotesBinding
import com.example.vkr_todolist.ui.dialogs.DeleteDialog
import com.example.vkr_todolist.ui.main.MainActivity
import com.example.vkr_todolist.ui.main.MainViewModel
import com.example.vkr_todolist.ui.note.NoteFragmentDirections
import com.google.android.material.snackbar.Snackbar

class ListOfNotesFragment : Fragment(), NoteAdapter.NoteListener {
    private lateinit var binding: FragmentListOfNotesBinding
    private lateinit var adapter: NoteAdapter
    private lateinit var defPref: SharedPreferences
    private val args: ListOfNotesFragmentArgs by navArgs()
    private var listItem: ListItem? = null
    private val viewModel: MainViewModel by activityViewModels{
        MainViewModel.MainViewModelFactory((context?.applicationContext as App).database)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListOfNotesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabNewNote.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToAddEditNoteFragment(null, listItem)
            findNavController().navigate(action)
        }
        init()
        initRecyclerView()
        observer()
        initSwipe()
    }

    override fun onResume() {
        super.onResume()
//        (activity as MainActivity).supportActionBar?.title = ListFragment.labelName
//        Log.d("TAG", "resume")
    }


    private fun init()=with(binding){
                val value = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(LIST_NAME, ListItem::class.java)
        } else {
            arguments?.getSerializable(LIST_NAME) as? ListItem
        }
        listItem=value
    }


    private fun initSwipe(){
        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
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
                viewModel.deleteNote(note)
                Snackbar.make(binding.root, R.string.snackbar_deleted, Snackbar.LENGTH_LONG)
                    .apply {
                        setAction(R.string.undo) {
                            viewModel.insertNote(note)
                        }
                        show()
                    }
            }

        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        itemTouchHelper.attachToRecyclerView(binding.rcViewNote)
    }


    private fun observer(){
        viewModel.getAllNotesFromList(listItem?.listId!!).observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.emptyViewNote.run { visibility = if(it.isNullOrEmpty()) View.VISIBLE else View.GONE }
        }
    }


    private fun initRecyclerView()=with(binding){
        defPref = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        rcViewNote.layoutManager= getLayoutManager()
        adapter = NoteAdapter(this@ListOfNotesFragment, defPref)
        rcViewNote.adapter=adapter
    }


    private fun getLayoutManager(): RecyclerView.LayoutManager{
        return if(defPref.getString("note_style_key", "Linear") == "Linear"){
            LinearLayoutManager(activity)
        } else {
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }


    companion object {
        const val LIST_NAME = "list_name"
        @JvmStatic
        fun newInstance() = ListOfNotesFragment()
    }


    private fun deleteNote(note: Note){
        DeleteDialog.showDeleteNote(context as AppCompatActivity, object : DeleteDialog.Listener{
            override fun onClick() {
                viewModel.deleteNote(note)
            }
        })
    }


    override fun onCLickItem(note: Note, state: Int) {
        when(state){
            NoteAdapter.EDIT -> {
                val action = ListFragmentDirections.actionListFragmentToAddEditNoteFragment(note, null)
                findNavController().navigate(action)
            }
            NoteAdapter.DELETE -> {
                deleteNote(note)
            }
        }
    }
}