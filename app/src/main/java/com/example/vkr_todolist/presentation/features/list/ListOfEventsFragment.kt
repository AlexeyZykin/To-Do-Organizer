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
import com.example.vkr_todolist.presentation.features.event.EventAdapter
import com.example.vkr_todolist.data.source.local.model.Event
import com.example.vkr_todolist.data.source.local.model.ListItem
import com.example.vkr_todolist.databinding.FragmentListOfEventsBinding
import com.example.vkr_todolist.presentation.features.dialogs.DeleteDialog
import com.example.vkr_todolist.presentation.main.MainViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class ListOfEventsFragment : Fragment(), EventAdapter.EventListener {
    private lateinit var binding: FragmentListOfEventsBinding
    private lateinit var adapter: EventAdapter

    private var listItem: ListItem? = null
    private val viewModel: MainViewModel by activityViewModels{
        MainViewModel.MainViewModelFactory((context?.applicationContext as App).database)
    }




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListOfEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabCreateEvent.setOnClickListener {
            val action =
                ListFragmentDirections.actionListFragmentToAddEditEventFragment(null, listItem)
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


    private fun init()=with(binding){
        val value = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(ListOfNotesFragment.LIST_NAME, ListItem::class.java)
        } else {
            arguments?.getSerializable(ListOfNotesFragment.LIST_NAME) as? ListItem
        }
        listItem=value
    }


    private fun observer(){
        viewModel.getAllEventsFromList(listItem?.listId!!).observe(viewLifecycleOwner) {
            adapter.submitList(it)
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
                    ListFragmentDirections.actionListFragmentToAddEditEventFragment(event, null)
                findNavController().navigate(action)
            }
            EventAdapter.DELETE -> {
                deleteEvent(event)
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = ListOfEventsFragment()
    }
}