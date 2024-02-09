package com.example.vkr_todolist.presentation.features.list

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
import com.example.vkr_todolist.presentation.features.task.TaskAdapter
import com.example.vkr_todolist.databinding.FragmentListOfTasksBinding
import com.example.vkr_todolist.presentation.dialogs.DeleteDialog
import com.example.vkr_todolist.presentation.features.task.TaskViewModel
import com.example.vkr_todolist.presentation.model.TaskUi
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class ListOfTasksFragment : Fragment(), TaskAdapter.TaskListener {
    private lateinit var binding: FragmentListOfTasksBinding
    private lateinit var adapter: TaskAdapter
    private val viewModel by viewModel<TaskViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListOfTasksBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listId = arguments?.getInt(LIST_NAME)
        listId?.let { viewModel.getAllTasksByList(it) }
        subscribeObserver()
        initRecyclerView()
        initSwipe()
        binding.fabNewTask.setOnClickListener {
            val action =
                ListFragmentDirections.actionListFragmentToAddEditTaskFragment(null,
                    listId?.let { id -> intArrayOf(id) })
            findNavController().navigate(action)
        }
    }


    private fun subscribeObserver() {
        viewModel.tasks.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it)
            }
            binding.viewStub.run { visibility = if(it.isNullOrEmpty()) View.VISIBLE else View.GONE }
        }
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


    private fun initRecyclerView()=with(binding){
        rcViewTask.layoutManager = LinearLayoutManager(activity)
        adapter = TaskAdapter(this@ListOfTasksFragment)
        rcViewTask.adapter = adapter
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
                    ListFragmentDirections.actionListFragmentToAddEditTaskFragment(taskUi.id?.let {
                        intArrayOf(it)
                    }, null)
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

    companion object {
        const val LIST_NAME = "list_name"
    }
}