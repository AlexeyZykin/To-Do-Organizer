package com.example.vkr_todolist.ui.task

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vkr_todolist.App
import com.example.vkr_todolist.R
import com.example.vkr_todolist.adapter.TaskAdapter
import com.example.vkr_todolist.data.model.Task
import com.example.vkr_todolist.databinding.FragmentImportantTasksBinding
import com.example.vkr_todolist.databinding.FragmentTaskBinding
import com.example.vkr_todolist.ui.dialogs.DeleteDialog
import com.example.vkr_todolist.ui.main.MainViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class ImportantTasksFragment : Fragment(), TaskAdapter.TaskListener {
    private lateinit var binding: FragmentImportantTasksBinding
    private lateinit var adapter: TaskAdapter

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as App).database)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImportantTasksBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        observer()
        initSwipe()
    }


    private fun observer(){
        viewModel.importantTasks.observe(viewLifecycleOwner){
            adapter.submitList(it)
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
                viewModel.deleteTask(task)
                Snackbar.make(binding.root, R.string.snackbar_deleted, Snackbar.LENGTH_LONG)
                    .apply {
                        setAction(R.string.undo) {
                            //viewModel.insertTask(task)
                            lifecycleScope.launch {
                                viewModel.insertTask(task)
                            }
                        }
                        show()
                    }
            }

        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        itemTouchHelper.attachToRecyclerView(binding.rcViewTask)
    }


    private fun deleteTask(task: Task){
        DeleteDialog.showDeleteTask(context as AppCompatActivity, object : DeleteDialog.Listener{
            override fun onClick() {
                viewModel.deleteTask(task)
            }
        })
    }


    override fun onCLickItem(task: Task, state: Int) {
        when(state){
            TaskAdapter.CHECK_BOX -> {
                viewModel.updateTask(task)
            }
            TaskAdapter.EDIT -> {
                val action = ImportantTasksFragmentDirections.actionImportantTasksFragmentToAddEditTaskFragment(task, null)
                findNavController().navigate(action)
                
            }
            TaskAdapter.STAR ->{
                viewModel.updateTask(task)
            }
            TaskAdapter.DELETE -> {
                deleteTask(task)
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = ImportantTasksFragment()
    }
}