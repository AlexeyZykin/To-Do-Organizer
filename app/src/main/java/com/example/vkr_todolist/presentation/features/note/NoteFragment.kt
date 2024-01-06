package com.example.vkr_todolist.presentation.features.note

import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.*
import com.example.vkr_todolist.app.App
import com.example.vkr_todolist.R
import com.example.vkr_todolist.data.source.local.model.Note
import com.example.vkr_todolist.databinding.FragmentNoteBinding
import com.example.vkr_todolist.presentation.features.dialogs.DeleteDialog
import com.example.vkr_todolist.presentation.main.MainViewModel
import com.google.android.material.snackbar.Snackbar


class NoteFragment : Fragment(), NoteAdapter.NoteListener {
    private lateinit var binding: FragmentNoteBinding
    private lateinit var adapter: NoteAdapter
    private lateinit var defPref: SharedPreferences

    private val mainViewModel: MainViewModel by activityViewModels{
        MainViewModel.MainViewModelFactory((context?.applicationContext as App).database)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        observer()
        binding.fabNewNote.setOnClickListener {
            val action = NoteFragmentDirections.actionNoteFragmentToAddEditNoteFragment(null, null)
            it.findNavController().navigate(action)
        }
        initSwipe()
        setupMenu()
    }


    private fun setupMenu(){
        (requireActivity() as MenuHost).addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.notes_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId){
                    R.id.searchFragment-> {
                        val action = NoteFragmentDirections.actionNoteFragmentToSearchFragment(NOTE)
                        findNavController().navigate(action)
                        return true }
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
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
                mainViewModel.deleteNote(note)
                Snackbar.make(binding.root, R.string.snackbar_deleted, Snackbar.LENGTH_LONG)
                    .apply {
                        setAction(R.string.undo) {
                            mainViewModel.insertNote(note)
                        }
                        show()
                    }
            }

        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        itemTouchHelper.attachToRecyclerView(binding.rcViewNote)
    }


    private fun observer(){
        mainViewModel.allNotes.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.emptyViewNote.run { visibility = if(it.isNullOrEmpty()) View.VISIBLE else View.GONE }
        }
    }


    private fun initRecyclerView()=with(binding){
        defPref = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        rcViewNote.layoutManager= getLayoutManager()
        adapter = NoteAdapter(this@NoteFragment, defPref)
        rcViewNote.adapter=adapter
    }


    private fun getLayoutManager(): RecyclerView.LayoutManager{
        return if(defPref.getString("note_style_key", "Linear") == "Linear"){
            LinearLayoutManager(activity)
        } else {
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }


    private fun deleteNote(note: Note){
        DeleteDialog.showDeleteNote(context as AppCompatActivity, object : DeleteDialog.Listener{
            override fun onClick() {
                mainViewModel.deleteNote(note)
            }
        })
    }


    override fun onCLickItem(note: Note, state: Int) {
        when(state){
            NoteAdapter.EDIT -> {
                val action =
                    NoteFragmentDirections.actionNoteFragmentToAddEditNoteFragment(note, null)
                findNavController().navigate(action)
            }
            NoteAdapter.DELETE -> {
                deleteNote(note)
            }
        }
    }


    companion object {
        const val NOTE = "note_type"
        @JvmStatic
        fun newInstance() = NoteFragment()
    }
}