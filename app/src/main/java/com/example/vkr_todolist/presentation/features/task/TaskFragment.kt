package com.example.vkr_todolist.presentation.features.task

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vkr_todolist.R
import com.example.vkr_todolist.databinding.FragmentTaskBinding
import com.example.vkr_todolist.presentation.dialogs.DeleteDialog
import com.example.vkr_todolist.presentation.model.TaskUi
import com.example.vkr_todolist.presentation.utils.Constants
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel


class TaskFragment : Fragment(), TaskAdapter.TaskListener {
    private lateinit var binding: FragmentTaskBinding
    private lateinit var adapter: TaskAdapter
    private lateinit var popupMenu: PopupMenu
    private val viewModel by viewModel<TaskViewModel>()
    private val sharedPref by lazy {
        requireActivity().getSharedPreferences(
            "MyPrefs",
            Context.MODE_PRIVATE
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initSwipe()
        subscribeObserver()
        setupMenu()
        initSelectedFilter()
        binding.fabNewTask.setOnClickListener {
            val action = TaskFragmentDirections.actionTaskFragmentToAddEditTaskFragment(null, null)
            it.findNavController().navigate(action)
        }
        binding.chipFilterTasks.setOnClickListener { initFilterList() }
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.tasks_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.searchFragment -> {
                        val action = TaskFragmentDirections.actionTaskFragmentToSearchFragment(TASK)
                        findNavController().navigate(action)
                        return true
                    }
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun initRecyclerView() {
        binding.rcViewTask.layoutManager = LinearLayoutManager(activity)
        adapter = TaskAdapter(this@TaskFragment)
        binding.rcViewTask.adapter = adapter
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
                val task = adapter.currentList[pos]
                task.id?.let { viewModel.deleteTask(it) }
                Snackbar.make(binding.root, R.string.snackbar_deleted, Snackbar.LENGTH_LONG)
                    .apply {
                        setAction(R.string.undo) {
                            viewModel.addTask(task)
                        }
                        show()
                    }
            }

        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        itemTouchHelper.attachToRecyclerView(binding.rcViewTask)
    }


    private fun initFilterList() = with(binding) {
        popupMenu = PopupMenu(requireContext().applicationContext, chipFilterTasks)
        popupMenu.menuInflater.inflate(R.menu.filter_tasks_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.allTasks -> {
                    saveSelectedFilter(Constants.TASKS_ALL)
                    viewModel.setSelectedFilter(Constants.TASKS_ALL)
                    chipFilterTasks.text = getString(R.string.all_tasks)
                    true
                }

                R.id.todayTasks -> {
                    saveSelectedFilter(Constants.TASKS_TODAY)
                    viewModel.setSelectedFilter(Constants.TASKS_TODAY)
                    chipFilterTasks.text = getString(R.string.today_tasks)
                    true
                }

                R.id.tomorrowTasks -> {
                    saveSelectedFilter(Constants.TASKS_TOMORROW)
                    viewModel.setSelectedFilter(Constants.TASKS_TOMORROW)
                    chipFilterTasks.text = getString(R.string.tomorrow_tasks)
                    true
                }

                R.id.laterTasks -> {
                    saveSelectedFilter(Constants.TASKS_LATER)
                    viewModel.setSelectedFilter(Constants.TASKS_LATER)
                    chipFilterTasks.text = getString(R.string.later_tasks)
                    true
                }

                R.id.noDatesTasks -> {
                    saveSelectedFilter(Constants.TASKS_NO_DATES)
                    viewModel.setSelectedFilter(Constants.TASKS_NO_DATES)
                    chipFilterTasks.text = getString(R.string.no_dates_tasks)
                    true
                }

                else -> true
            }
        }
        popupMenu.show()
    }

    private fun saveSelectedFilter(selectedItem: Int) {
        sharedPref.edit().putInt("selected_item", selectedItem).apply()
    }

    private fun initSelectedFilter() {
        val selectedItem = sharedPref.getInt("selected_item", Constants.TASKS_ALL)
        when (selectedItem) {
            Constants.TASKS_ALL -> {
                binding.chipFilterTasks.text = getString(R.string.all_tasks)
                viewModel.setSelectedFilter(Constants.TASKS_ALL)
            }

            Constants.TASKS_TODAY -> {
                binding.chipFilterTasks.text = getString(R.string.today_tasks)
                viewModel.setSelectedFilter(Constants.TASKS_TODAY)
            }

            Constants.TASKS_TOMORROW -> {
                binding.chipFilterTasks.text = getString(R.string.tomorrow_tasks)
                viewModel.setSelectedFilter(Constants.TASKS_TOMORROW)
            }

            Constants.TASKS_LATER -> {
                binding.chipFilterTasks.text = getString(R.string.later_tasks)
                viewModel.setSelectedFilter(Constants.TASKS_LATER)
            }

            Constants.TASKS_NO_DATES -> {
                binding.chipFilterTasks.text = getString(R.string.no_dates_tasks)
                viewModel.setSelectedFilter(Constants.TASKS_NO_DATES)
            }
        }
    }

    private fun subscribeObserver() {
        viewModel.tasksFiltered.observe(viewLifecycleOwner) {
            it?.let { adapter.submitList(it) }
            binding.viewStub.run {
                visibility = if (it.isNullOrEmpty()) View.VISIBLE else View.GONE
            }
        }
    }


    private fun deleteTask(taskUi: TaskUi) {
        DeleteDialog.showDeleteTask(context as AppCompatActivity, object : DeleteDialog.Listener {
            override fun onClick() {
                taskUi.id?.let { viewModel.deleteTask(it) }
            }
        })
    }


    override fun onCLickItem(taskUi: TaskUi, state: Int) {
        when (state) {
            TaskAdapter.CHECK_BOX -> {
                viewModel.updateTask(taskUi)
            }

            TaskAdapter.EDIT -> {
                val action =
                    TaskFragmentDirections.actionTaskFragmentToAddEditTaskFragment(taskUi.id?.let {
                        intArrayOf(it)
                    }, null)
                findNavController().navigate(action)
            }

            TaskAdapter.STAR -> {
                viewModel.updateTask(taskUi)
            }

            TaskAdapter.DELETE -> {
                deleteTask(taskUi)
            }
        }
    }


    companion object {
        const val TASK = "task_type"
    }
}