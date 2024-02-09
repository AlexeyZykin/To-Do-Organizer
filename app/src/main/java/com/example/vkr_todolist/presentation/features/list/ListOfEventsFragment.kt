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
import com.example.vkr_todolist.presentation.features.event.EventAdapter
import com.example.vkr_todolist.databinding.FragmentListOfEventsBinding
import com.example.vkr_todolist.presentation.dialogs.DeleteDialog
import com.example.vkr_todolist.presentation.features.event.EventViewModel
import com.example.vkr_todolist.presentation.model.EventUi
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class ListOfEventsFragment : Fragment(), EventAdapter.EventListener {
    private lateinit var binding: FragmentListOfEventsBinding
    private lateinit var adapter: EventAdapter
    private val viewModel by viewModel<EventViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListOfEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listId = arguments?.getInt(LIST_NAME)
        listId?.let { viewModel.getAllEventsFromList(listId) }
        subscribeObserver()
        initRecyclerView()
        initSwipe()
        binding.fabCreateEvent.setOnClickListener {
            val action =
                ListFragmentDirections.actionListFragmentToAddEditEventFragment(null,
                    listId?.let { id -> intArrayOf(id) })
            findNavController().navigate(action)
        }
    }

    private fun subscribeObserver() {
        viewModel.events.observe(viewLifecycleOwner) {
            it?.let { adapter.submitList(it) }
            binding.emptyViewEvent.run { visibility = if(it.isNullOrEmpty()) View.VISIBLE else View.GONE }
        }
    }

    private fun initRecyclerView()=with(binding){
        rcViewEvent.layoutManager = LinearLayoutManager(activity)
        adapter = EventAdapter(this@ListOfEventsFragment, requireContext())
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
                event.id?.let { viewModel.deleteEvent(it) }
                Snackbar.make(binding.root, R.string.snackbar_deleted, Snackbar.LENGTH_LONG)
                    .apply {
                        setAction(R.string.undo) {
                            lifecycleScope.launch {
                                viewModel.addEvent(event)
                            }
                        }
                        show()
                    }
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        itemTouchHelper.attachToRecyclerView(binding.rcViewEvent)
    }

    private fun deleteEventLongClick(eventUi: EventUi){
        DeleteDialog.showDeleteEvent(context as AppCompatActivity, object : DeleteDialog.Listener{
            override fun onClick() {
                eventUi.id?.let { viewModel.deleteEvent(it) }
            }
        })
    }

    override fun onCLickItem(eventUi: EventUi, state: Int) {
        when(state){
            EventAdapter.EDIT -> {
                val action =
                    ListFragmentDirections.actionListFragmentToAddEditEventFragment(eventUi.id?.let {
                        intArrayOf(it)
                    }, null)
                findNavController().navigate(action)
            }
            EventAdapter.DELETE -> {
                deleteEventLongClick(eventUi)
            }
        }
    }

    companion object {
        const val LIST_NAME = "list_name"
    }
}