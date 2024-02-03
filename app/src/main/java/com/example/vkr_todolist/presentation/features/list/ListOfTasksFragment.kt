package com.example.vkr_todolist.presentation.features.list

import android.os.Build
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
import com.example.vkr_todolist.app.App
import com.example.vkr_todolist.R
import com.example.vkr_todolist.presentation.features.task.TaskAdapter
import com.example.vkr_todolist.cache.room.model.ListItem
import com.example.vkr_todolist.cache.room.model.Task
import com.example.vkr_todolist.databinding.FragmentListOfTasksBinding
import com.example.vkr_todolist.presentation.dialogs.DeleteDialog
import com.example.vkr_todolist.presentation.main.MainViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class ListOfTasksFragment : Fragment(), TaskAdapter.TaskListener {
    private lateinit var binding: FragmentListOfTasksBinding
    private lateinit var adapter: TaskAdapter
    private var listItem: ListItem? = null
    private val viewModel: MainViewModel by activityViewModels{
        MainViewModel.MainViewModelFactory((context?.applicationContext as App).database)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListOfTasksBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabNewTask.setOnClickListener {
            val action =
                ListFragmentDirections.actionListFragmentToAddEditTaskFragment(null, listItem)
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

    private fun observer(){
        viewModel.getAllTasksFromList(listItem?.listId!!).observe(viewLifecycleOwner) {
            adapter?.submitList(it)
            binding.viewStub.run { visibility = if(it.isNullOrEmpty()) View.VISIBLE else View.GONE }
        }
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


    private fun initRecyclerView()=with(binding){
        rcViewTask.layoutManager = LinearLayoutManager(activity)
        adapter = TaskAdapter(this@ListOfTasksFragment)
        rcViewTask.adapter = adapter
    }


    private fun deleteTask(task: Task){
        DeleteDialog.showDeleteTask(context as AppCompatActivity, object : DeleteDialog.Listener{
            override fun onClick() {
                viewModel.deleteTask(task)
            }
        })
    }


    companion object {
        const val LIST_NAME = "list_name"
        @JvmStatic
        fun newInstance() = ListOfTasksFragment()
    }

    override fun onCLickItem(task: Task, state: Int) {
        when(state){
            TaskAdapter.CHECK_BOX -> {
                viewModel.updateTask(task)
            }
            TaskAdapter.EDIT -> {
                val action =
                    ListFragmentDirections.actionListFragmentToAddEditTaskFragment(task, null)
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
}