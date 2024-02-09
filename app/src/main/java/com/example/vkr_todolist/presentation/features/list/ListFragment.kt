package com.example.vkr_todolist.presentation.features.list

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.vkr_todolist.R
import com.example.vkr_todolist.databinding.FragmentListBinding
import com.example.vkr_todolist.presentation.dialogs.DeleteDialog
import com.example.vkr_todolist.presentation.main.MainActivity
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListFragment : Fragment() {
    private lateinit var binding: FragmentListBinding
    private lateinit var adapter: ViewPagerAdapter
    private val args: ListFragmentArgs by navArgs()
    private val viewModel by viewModel<ListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        adapter = ViewPagerAdapter(requireActivity(), args.listId)
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getListById(args.listId)
        viewModel.listItem.observe(viewLifecycleOwner) {
            (activity as MainActivity).supportActionBar?.title = it.title
        }
        setupMenu()
        initViewPager()
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
            fragment.arguments = Bundle().apply {
                putSerializable(LIST_NAME, args.listId)
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
        val action = ListFragmentDirections.actionListFragmentToAddEditListFragment(intArrayOf(args.listId))
        findNavController().navigate(action)
    }

    private fun deleteListItem(){
        DeleteDialog.showDialog(context as AppCompatActivity, object : DeleteDialog.Listener{
            override fun onClick() {
                viewModel.deleteList(args.listId)
                findNavController().popBackStack()
            }
        })
    }

    companion object {
        const val LIST_NAME = "list_name"
    }

}