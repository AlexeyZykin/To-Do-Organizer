package com.example.vkr_todolist.presentation.features.event

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vkr_todolist.app.App
import com.example.vkr_todolist.R
import com.example.vkr_todolist.cache.room.model.Event
import com.example.vkr_todolist.databinding.FragmentEventBinding
import com.example.vkr_todolist.presentation.dialogs.DeleteDialog
import com.example.vkr_todolist.presentation.main.MainViewModel
import com.example.vkr_todolist.presentation.utils.DateTimeManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class EventFragment : Fragment(), EventAdapter.EventListener {
    private lateinit var binding: FragmentEventBinding
    private lateinit var adapter: EventAdapter


    private val viewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as App).database)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabCreateEvent.setOnClickListener {
            val action = EventFragmentDirections.actionEventFragmentToAddEditEventFragment(
                event = null,
                listName = null
            )
            findNavController().navigate(action)
        }

        initRecyclerView()
        initSwipe()
        observer()
        setupMenu()
    }


    private fun observer(){
        viewModel.allEvents.observe(viewLifecycleOwner){
            Log.d("TAG", "observer: $it")
            adapter.submitList(it)
            binding.emptyViewEvent.run { visibility = if(it.isNullOrEmpty()) View.VISIBLE else View.GONE }
        }
    }


    private fun initRecyclerView()=with(binding){
        rcViewEvent.layoutManager = LinearLayoutManager(activity)
        adapter = EventAdapter(this@EventFragment, requireContext())
        rcViewEvent.adapter = adapter
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
                val event = adapter.currentList[pos]
                viewModel.deleteEvent(event)
                Snackbar.make(binding.root, R.string.snackbar_deleted, Snackbar.LENGTH_LONG)
                    .apply {
                        setAction(R.string.undo) {
                            lifecycleScope.launch {
                                viewModel.insertEvent(event)
                            }
                        }
                        show()
                    }
            }

        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        itemTouchHelper.attachToRecyclerView(binding.rcViewEvent)
    }


    private fun setupMenu(){
        (requireActivity() as MenuHost).addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.events_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId){
                    R.id.searchFragment-> {
                        val action =
                            EventFragmentDirections.actionEventFragmentToSearchFragment(EVENT)
                        findNavController().navigate(action)
                        return true }
                    R.id.deleteEndedEvents-> {
                        deleteEndedEvents()
                        return true }
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


    private fun deleteEndedEvents(){
        DeleteDialog.showDeleteEndEvents(context as AppCompatActivity, object : DeleteDialog.Listener{
            override fun onClick() {
                viewModel.deleteEndedEvents(DateTimeManager.getCurrentDateTime())
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


    override fun onCLickItem(event: Event, state: Int) {
        when(state){
            EventAdapter.EDIT -> {
                val action =
                    EventFragmentDirections.actionEventFragmentToAddEditEventFragment(event, null)
                findNavController().navigate(action)
            }
            EventAdapter.DELETE -> {
                deleteEvent(event)
            }
        }
    }


    companion object {
        const val EVENT = "event_type"
        @JvmStatic
        fun newInstance() = EventFragment()
    }
}