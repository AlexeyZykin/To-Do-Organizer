package com.example.vkr_todolist.presentation.features.task

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vkr_todolist.R
import com.example.vkr_todolist.databinding.FragmentCompletedTasksBinding
import com.example.vkr_todolist.presentation.dialogs.DeleteDialog
import com.example.vkr_todolist.presentation.model.TaskUi
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CompletedTasksFragment : Fragment(), TaskAdapter.TaskListener {
    private lateinit var binding: FragmentCompletedTasksBinding
    private lateinit var adapter: TaskAdapter
    private val viewModel by viewModel<TaskViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCompletedTasksBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getCompletedTasks()
        initRecyclerView()
        subscribeObserver()
        initSwipe()
        setupMenu()
    }


    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.completed_tasks_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.deleteAllCompTasks -> {
                        deleteCompletedTasks()
                        return true
                    }
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }


    private fun deleteCompletedTasks() {
        DeleteDialog.showCompTasksDialog(
            context as AppCompatActivity,
            object : DeleteDialog.Listener {
                override fun onClick() {
                    viewModel.deleteCompletedTasks()
                }
            })
    }

    private fun subscribeObserver() {
        viewModel.tasks.observe(viewLifecycleOwner) {
            it?.let { adapter.submitList(it) }
            binding.emptyCompTasks.run {
                visibility = if (it.isNullOrEmpty()) View.VISIBLE else View.GONE
            }
        }
    }


    private fun initRecyclerView() = with(binding) {
        rcViewTask.layoutManager = LinearLayoutManager(activity)
        adapter = TaskAdapter(this@CompletedTasksFragment)
        rcViewTask.adapter = adapter
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
                            //viewModel.insertTask(task)
                            lifecycleScope.launch {
                                viewModel.addTask(task)
                            }
                        }
                        show()
                    }
            }

        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        itemTouchHelper.attachToRecyclerView(binding.rcViewTask)
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
                    CompletedTasksFragmentDirections.actionCompletedTasksFragmentToAddEditTaskFragment(
                        taskUi.id?.let { intArrayOf(it) },
                        null
                    )
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
}