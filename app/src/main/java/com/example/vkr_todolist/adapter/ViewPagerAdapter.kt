package com.example.vkr_todolist.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.vkr_todolist.data.model.ListItem
import com.example.vkr_todolist.ui.list.ListOfEventsFragment
import com.example.vkr_todolist.ui.list.ListOfNotesFragment
import com.example.vkr_todolist.ui.list.ListOfTasksFragment

class ViewPagerAdapter(fa: FragmentActivity, private val listItem: ListItem): FragmentStateAdapter(fa) {
    val fragmentsViewPager = listOf(ListOfTasksFragment(), ListOfNotesFragment(), ListOfEventsFragment())

    override fun getItemCount() = fragmentsViewPager.size

    override fun createFragment(position: Int): Fragment {
        val fragment = fragmentsViewPager[position]
        fragment.arguments = Bundle().apply{
            putSerializable(LIST_NAME, listItem)
        }
        return fragment
    }
    companion object{
        private const val LIST_NAME = "list_name"
    }
}