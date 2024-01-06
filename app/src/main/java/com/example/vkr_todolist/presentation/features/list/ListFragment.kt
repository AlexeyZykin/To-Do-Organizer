package com.example.vkr_todolist.presentation.features.list

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.vkr_todolist.app.App
import com.example.vkr_todolist.R
import com.example.vkr_todolist.databinding.FragmentListBinding
import com.example.vkr_todolist.presentation.features.dialogs.DeleteDialog
import com.example.vkr_todolist.presentation.main.MainActivity
import com.example.vkr_todolist.presentation.main.MainViewModel
import com.google.android.material.tabs.TabLayoutMediator

class ListFragment : Fragment() {
    private lateinit var binding: FragmentListBinding
    private lateinit var adapter: ViewPagerAdapter
    private val args: ListFragmentArgs by navArgs()
    private val viewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as App).database)
    }
    private var label: String = ""
    private var isBottomSheetOpen: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (savedInstanceState != null) {
            label = savedInstanceState.getString("label", "")
        }
        adapter = ViewPagerAdapter(requireActivity(), args.listItem)
        binding = FragmentListBinding.inflate(inflater, container, false)
        Log.d("TAG", "onCreateView")
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()
        initViewPager()
        initListFrag()
        Log.d("TAG", "onViewCreated")
    }

    override fun onResume() {
        super.onResume()
        Log.d("TAG", "onResume")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("label", label)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            Log.d("TAG", "onViewStateRestored")
            label = savedInstanceState.getString("label", "")
            (activity as MainActivity).supportActionBar?.title = label
        }
    }


    private fun setupMenu(){
        (requireActivity() as MenuHost).addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.nav_list_name, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId){
                    R.id.editList-> {
                        updateListItem()
                        return true }
                    R.id.deleteList -> {
                        deleteListItem()
                        return true
                    }
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


    private fun initViewPager()= with(binding){
        viewPager.adapter = adapter
        adapter.fragmentsViewPager.forEach {fragment ->
            fragment.arguments =Bundle().apply {
                putSerializable("LIST_NAME", args.listItem)
            }
        }
        TabLayoutMediator(tabLayout,viewPager){tab,position->
            when(position){
                0->{
                    tab.setIcon(R.drawable.ic_task)
                    tab.text=getString(R.string.it_tasks)
                }
                1->{
                    tab.setIcon(R.drawable.ic_note)
                    tab.text=getString(R.string.it_notes)
                }
                2->{
                    tab.setIcon(R.drawable.ic_event)
                    tab.text=getString(R.string.it_event)
                }
            }
        }.attach()
    }


    private fun updateListItem(){
        val dialog = AddEditListFragment()
        
        val action = ListFragmentDirections.actionListFragmentToAddEditListFragment(args.listItem)
        findNavController().navigate(action)
    }


    private fun deleteListItem(){
        DeleteDialog.showDialog(context as AppCompatActivity, object : DeleteDialog.Listener{
            override fun onClick() {
                viewModel.deleteList(args.listItem.listId!!)
                findNavController().popBackStack()
            }
        })
    }


    private fun initListFrag() = with(binding){
            label = args.listItem.listTitle
            (activity as MainActivity).supportActionBar?.title = label
    }


    companion object {
        const val LIST_NAME = "list_name"
        @JvmStatic
        fun newInstance() = ListFragment()
    }

}