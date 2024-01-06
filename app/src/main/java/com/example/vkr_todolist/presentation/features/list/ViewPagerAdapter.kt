package com.example.vkr_todolist.presentation.features.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.vkr_todolist.data.source.local.model.ListItem

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