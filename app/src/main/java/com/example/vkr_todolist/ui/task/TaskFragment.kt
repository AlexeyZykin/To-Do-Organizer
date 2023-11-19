package com.example.vkr_todolist.ui.task

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AlertDialogLayout
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vkr_todolist.App
import com.example.vkr_todolist.NavGraphDirections
import com.example.vkr_todolist.R
import com.example.vkr_todolist.adapter.TaskAdapter
import com.example.vkr_todolist.data.model.Task
import com.example.vkr_todolist.databinding.FragmentTaskBinding
import com.example.vkr_todolist.ui.dialogs.DeleteDialog
import com.example.vkr_todolist.ui.main.MainViewModel
import com.example.vkr_todolist.utils.Constants
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.*


class TaskFragment : Fragment(), TaskAdapter.TaskListener {
    private lateinit var binding: FragmentTaskBinding
    private lateinit var adapter: TaskAdapter
    private lateinit var popupMenu: PopupMenu

    private val sharedPref by lazy { requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE) }

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as App).database)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentTaskBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabNewTask.setOnClickListener {
            val action = TaskFragmentDirections.actionTaskFragmentToAddEditTaskFragment(null, null)
            it.findNavController().navigate(action)
        }
        binding.chipFilterTasks.setOnClickListener {
            initFilterList()
        }
        initRecyclerView()
        initSwipe()
        observer()
        setupMenu()
        initSelectedFilter()
    }

    private fun setupMenu(){
        (requireActivity() as MenuHost).addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.tasks_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId){
                    R.id.searchFragment-> {
                        val action = TaskFragmentDirections.actionTaskFragmentToSearchFragment(TASK)
                        findNavController().navigate(action)
                        return true }
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


    private fun initRecyclerView()=with(binding){
        rcViewTask.layoutManager = LinearLayoutManager(activity)
        adapter = TaskAdapter(this@TaskFragment)
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

    private fun initSelectedFilter(){
        val selectedItem = sharedPref.getInt("selected_item", Constants.TASKS_ALL)
        Log.d("TAG", "Selected item: $selectedItem")
        when(selectedItem){
            Constants.TASKS_ALL ->{
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

    private fun observer(){
        viewModel.getTasks().observe(viewLifecycleOwner){
            Log.d("TAG", "observer: $it")
                    adapter.submitList(it)
                    binding.viewStub.run { visibility = if(it.isNullOrEmpty()) View.VISIBLE else View.GONE }
                }
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
                val action = TaskFragmentDirections.actionTaskFragmentToAddEditTaskFragment(task, null)
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
        const val TASK = "task_type"

        @JvmStatic
        fun newInstance() = TaskFragment()
    }
}