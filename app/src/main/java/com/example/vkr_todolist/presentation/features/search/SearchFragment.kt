package com.example.vkr_todolist.presentation.features.search

import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.vkr_todolist.app.App
import com.example.vkr_todolist.R
import com.example.vkr_todolist.presentation.features.event.EventAdapter
import com.example.vkr_todolist.presentation.features.note.NoteAdapter
import com.example.vkr_todolist.presentation.features.task.TaskAdapter
import com.example.vkr_todolist.cache.room.model.Event
import com.example.vkr_todolist.cache.room.model.Note
import com.example.vkr_todolist.cache.room.model.Task
import com.example.vkr_todolist.databinding.FragmentSearchBinding
import com.example.vkr_todolist.presentation.dialogs.DeleteDialog
import com.example.vkr_todolist.presentation.features.event.EventFragment
import com.example.vkr_todolist.presentation.main.MainViewModel
import com.example.vkr_todolist.presentation.features.note.NoteFragment
import com.example.vkr_todolist.presentation.features.task.TaskFragment
import com.example.vkr_todolist.presentation.utils.HideKeyboard


class SearchFragment : Fragment(), TaskAdapter.TaskListener, NoteAdapter.NoteListener, EventAdapter.EventListener {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapterNote: NoteAdapter
    private lateinit var adapterTask: TaskAdapter
    private lateinit var adapterEvent: EventAdapter
    private lateinit var defPref: SharedPreferences
    private val args: SearchFragmentArgs by navArgs()

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as App).database)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        setupMenu()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        HideKeyboard.hideKeyboard(requireActivity())
    }

    private fun setupMenu(){
        (requireActivity() as MenuHost).addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_view, menu)
                val search = menu.findItem(R.id.searchView)
                val searchView = search.actionView as SearchView
                searchView.queryHint = getString(R.string.search)
                searchView.onActionViewExpanded()

                binding.emptySearch.run {
                    visibility = View.VISIBLE
                }

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }
                    override fun onQueryTextChange(newText: String?): Boolean {
                        observer(newText)
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


    private fun observer(newText: String?) {
        when (args.typeName) {
            TaskFragment.TASK -> {
                viewModel.searchTask(newText!!).observe(viewLifecycleOwner) {
                    if (newText.isBlank()) {
                        adapterTask.submitList(emptyList())
                        binding.emptySearch.run {
                            visibility = View.VISIBLE
                        }
                    } else {
                        adapterTask.submitList(it)
                        binding.emptySearch.run {
                            visibility = if (it.isNullOrEmpty()) View.VISIBLE else View.GONE
                        }
                    }
                }
            }
            NoteFragment.NOTE -> {
                viewModel.searchNote(newText!!).observe(viewLifecycleOwner) {
                    if (newText.isBlank()) {
                        adapterNote.submitList(emptyList())
                        binding.emptySearch.run {
                            visibility = View.VISIBLE
                        }
                    } else {
                        adapterNote.submitList(it)
                        binding.emptySearch.run {
                            visibility = if (it.isNullOrEmpty()) View.VISIBLE else View.GONE
                        }
                    }
                }
            }
            EventFragment.EVENT -> {
                viewModel.searchEvent(newText!!).observe(viewLifecycleOwner) {
                    if (newText.isBlank()) {
                        adapterEvent.submitList(emptyList())
                        binding.emptySearch.run {
                            visibility = View.VISIBLE
                        }
                    } else {
                        adapterEvent.submitList(it)
                        binding.emptySearch.run {
                            visibility = if (it.isNullOrEmpty()) View.VISIBLE else View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun initRecyclerView() = with(binding) {
        when (args.typeName) {
            TaskFragment.TASK -> {
                rcView.layoutManager = LinearLayoutManager(activity)
                adapterTask = TaskAdapter(this@SearchFragment)
                rcView.adapter = adapterTask
            }
            NoteFragment.NOTE -> {
                defPref = PreferenceManager.getDefaultSharedPreferences(requireActivity())
                rcView.layoutManager = getLayoutManager()
                adapterNote = NoteAdapter(this@SearchFragment, defPref)
                rcView.adapter = adapterNote
            }
            EventFragment.EVENT -> {
                rcView.layoutManager = LinearLayoutManager(activity)
                adapterEvent = EventAdapter(this@SearchFragment, requireContext())
                rcView.adapter = adapterEvent
            }
        }
    }


    private fun getLayoutManager(): RecyclerView.LayoutManager {
        return if (defPref.getString("note_style_key", "Linear") == "Linear") {
            LinearLayoutManager(activity)
        } else {
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }


    private fun deleteTask(task: Task){
        DeleteDialog.showDeleteTask(context as AppCompatActivity, object : DeleteDialog.Listener{
            override fun onClick() {
                viewModel.deleteTask(task)
            }
        })
    }


    private fun deleteEvent(event: Event){
        DeleteDialog.showDeleteEvent(context as AppCompatActivity, object : DeleteDialog.Listener{
            override fun onClick() {
                viewModel.deleteEvent(event)
            }
        })
    }


    private fun deleteNote(note: Note){
        DeleteDialog.showDeleteNote(context as AppCompatActivity, object : DeleteDialog.Listener{
            override fun onClick() {
                viewModel.deleteNote(note)
            }
        })
    }


    override fun onCLickItem(task: Task, state: Int) {
        when (state) {
            TaskAdapter.CHECK_BOX -> {
                viewModel.updateTask(task)
            }
            TaskAdapter.EDIT -> {
                val action =
                    SearchFragmentDirections.actionSearchFragmentToAddEditTaskFragment(task, null)
                findNavController().navigate(action)
            }
            TaskAdapter.STAR -> {
                viewModel.updateTask(task)
            }
            TaskAdapter.DELETE -> {
                deleteTask(task)
            }
        }
    }


    override fun onCLickItem(note: Note, state: Int) {
        when(state){
            NoteAdapter.EDIT -> {
                val action =
                    SearchFragmentDirections.actionSearchFragmentToAddEditNoteFragment(note, null)
                findNavController().navigate(action)
            }
            NoteAdapter.DELETE -> {
                deleteNote(note)
            }
        }
    }

    override fun onCLickItem(event: Event, state: Int) {
        when(state){
            EventAdapter.EDIT -> {
                val action =
                    SearchFragmentDirections.actionSearchFragmentToAddEditEventFragment(event, null)
                findNavController().navigate(action)
            }
            EventAdapter.DELETE -> {
                deleteEvent(event)
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = SearchFragment()
    }
}