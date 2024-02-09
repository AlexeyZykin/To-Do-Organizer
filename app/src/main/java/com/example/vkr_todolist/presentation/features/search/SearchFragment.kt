package com.example.vkr_todolist.presentation.features.search

import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.vkr_todolist.R
import com.example.vkr_todolist.presentation.features.event.EventAdapter
import com.example.vkr_todolist.presentation.features.note.NoteAdapter
import com.example.vkr_todolist.presentation.features.task.TaskAdapter
import com.example.vkr_todolist.databinding.FragmentSearchBinding
import com.example.vkr_todolist.presentation.dialogs.DeleteDialog
import com.example.vkr_todolist.presentation.features.event.EventFragment
import com.example.vkr_todolist.presentation.features.event.EventViewModel
import com.example.vkr_todolist.presentation.features.note.NoteFragment
import com.example.vkr_todolist.presentation.features.note.NoteViewModel
import com.example.vkr_todolist.presentation.features.task.TaskFragment
import com.example.vkr_todolist.presentation.features.task.TaskViewModel
import com.example.vkr_todolist.presentation.model.EventUi
import com.example.vkr_todolist.presentation.model.NoteUi
import com.example.vkr_todolist.presentation.model.TaskUi
import com.example.vkr_todolist.presentation.utils.HideKeyboard
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment(), TaskAdapter.TaskListener, NoteAdapter.NoteListener,
    EventAdapter.EventListener {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapterNote: NoteAdapter
    private lateinit var adapterTask: TaskAdapter
    private lateinit var adapterEvent: EventAdapter
    private lateinit var defPref: SharedPreferences
    private val args: SearchFragmentArgs by navArgs()
    private val taskViewModel by viewModel<TaskViewModel>()
    private val noteViewModel by viewModel<NoteViewModel>()
    private val eventViewModel by viewModel<EventViewModel>()


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

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_view, menu)
                val search = menu.findItem(R.id.searchView)
                val searchView = search.actionView as SearchView
                searchView.queryHint = getString(R.string.search)
                searchView.onActionViewExpanded()
                binding.emptySearch.run { visibility = View.VISIBLE }
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        subscribeObservers(newText)
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


    private fun subscribeObservers(newText: String?) {
        when (args.typeName) {
            TaskFragment.TASK -> {
                taskViewModel.searchTask(newText!!)
                taskViewModel.tasks.observe(viewLifecycleOwner) {
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
                noteViewModel.searchNote(newText!!)
                noteViewModel.notes.observe(viewLifecycleOwner) {
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
                eventViewModel.searchEvent(newText!!)
                eventViewModel.events.observe(viewLifecycleOwner) {
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


    private fun deleteTask(taskUi: TaskUi) {
        DeleteDialog.showDeleteTask(context as AppCompatActivity, object : DeleteDialog.Listener {
            override fun onClick() {
                taskUi.id?.let { taskViewModel.deleteTask(it) }
            }
        })
    }


    private fun deleteEvent(eventUi: EventUi) {
        DeleteDialog.showDeleteEvent(context as AppCompatActivity, object : DeleteDialog.Listener {
            override fun onClick() {
                eventUi.id?.let { eventViewModel.deleteEvent(it) }
            }
        })
    }


    private fun deleteNote(noteUi: NoteUi) {
        DeleteDialog.showDeleteNote(context as AppCompatActivity, object : DeleteDialog.Listener {
            override fun onClick() {
                noteUi.id?.let { noteViewModel.deleteNote(it) }
            }
        })
    }


    override fun onCLickItem(taskUi: TaskUi, state: Int) {
        when (state) {
            TaskAdapter.CHECK_BOX -> {
                taskViewModel.updateTask(taskUi)
            }

            TaskAdapter.EDIT -> {
                val action =
                    SearchFragmentDirections.actionSearchFragmentToAddEditTaskFragment(
                        taskUi.id?.let { intArrayOf(it) },
                        null
                    )
                findNavController().navigate(action)
            }

            TaskAdapter.STAR -> {
                taskViewModel.updateTask(taskUi)
            }

            TaskAdapter.DELETE -> {
                deleteTask(taskUi)
            }
        }
    }


    override fun onCLickItem(noteUi: NoteUi, state: Int) {
        when (state) {
            NoteAdapter.EDIT -> {
                val action =
                    SearchFragmentDirections.actionSearchFragmentToAddEditNoteFragment(
                        noteUi.id?.let { intArrayOf(it) },
                        null
                    )
                findNavController().navigate(action)
            }

            NoteAdapter.DELETE -> {
                deleteNote(noteUi)
            }
        }
    }

    override fun onCLickItem(eventUi: EventUi, state: Int) {
        when (state) {
            EventAdapter.EDIT -> {
                val action =
                    SearchFragmentDirections.actionSearchFragmentToAddEditEventFragment(
                        eventUi.id?.let { intArrayOf(it) },
                        null
                    )
                findNavController().navigate(action)
            }

            EventAdapter.DELETE -> {
                deleteEvent(eventUi)
            }
        }
    }
}