package com.example.vkr_todolist.presentation.features.task

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vkr_todolist.R
import com.example.vkr_todolist.cache.room.model.TaskCache
import com.example.vkr_todolist.databinding.FragmentImportantTasksBinding
import com.example.vkr_todolist.presentation.dialogs.DeleteDialog
import com.example.vkr_todolist.presentation.model.TaskUi
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class ImportantTasksFragment : Fragment(), TaskAdapter.TaskListener {
    private lateinit var binding: FragmentImportantTasksBinding
    private lateinit var adapter: TaskAdapter
    private val viewModel by viewModel<TaskViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImportantTasksBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getImportantTasks()
        initRecyclerView()
        subscribeObserver()
        initSwipe()
    }


    private fun subscribeObserver(){
        viewModel.tasks.observe(viewLifecycleOwner){
            it?.let { adapter.submitList(it) }
            binding.emptyImpTasks.run { visibility = if(it.isNullOrEmpty()) View.VISIBLE else View.GONE }
        }
    }


    private fun initRecyclerView()=with(binding){
        rcViewTask.layoutManager = LinearLayoutManager(activity)
        adapter = TaskAdapter(this@ImportantTasksFragment)
        rcViewTask.adapter = adapter
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
                val task = adapter.currentList[pos]
                task.id?.let { viewModel.deleteTask(it) }
                Snackbar.make(binding.root, R.string.snackbar_deleted, Snackbar.LENGTH_LONG)
                    .apply {
                        setAction(R.string.undo) {
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


    private fun deleteTask(taskUi: TaskUi){
        DeleteDialog.showDeleteTask(context as AppCompatActivity, object : DeleteDialog.Listener{
            override fun onClick() {
                taskUi.id?.let { viewModel.deleteTask(it) }
            }
        })
    }


    override fun onCLickItem(taskUi: TaskUi, state: Int) {
        when(state){
            TaskAdapter.CHECK_BOX -> {
                viewModel.updateTask(taskUi)
            }
            TaskAdapter.EDIT -> {
                val action =
                    ImportantTasksFragmentDirections.actionImportantTasksFragmentToAddEditTaskFragment(
                        taskUi.id?.let { intArrayOf(it) },
                        null
                    )
                findNavController().navigate(action)
                
            }
            TaskAdapter.STAR ->{
                viewModel.updateTask(taskUi)
            }
            TaskAdapter.DELETE -> {
                deleteTask(taskUi)
            }
        }
    }
}