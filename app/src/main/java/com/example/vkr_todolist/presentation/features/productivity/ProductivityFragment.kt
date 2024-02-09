package com.example.vkr_todolist.presentation.features.productivity

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import com.example.vkr_todolist.R
import com.example.vkr_todolist.databinding.FragmentProductivityBinding
import com.example.vkr_todolist.presentation.utils.Constants
import com.example.vkr_todolist.presentation.utils.DateTimeManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt


class ProductivityFragment : Fragment() {
    private lateinit var binding: FragmentProductivityBinding
    private lateinit var popupMenu: PopupMenu
    private val viewModel by viewModel<ProductivityViewModel>()
    private val sharedPref by lazy {
        requireActivity().getSharedPreferences(
            "TimeRange",
            Context.MODE_PRIVATE
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductivityBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSelectedFilter()
        observer()
        binding.selectTimeRange.setOnClickListener { initFilterList() }
    }


    private fun initFilterList() = with(binding) {
        popupMenu = PopupMenu(requireContext().applicationContext, selectTimeRange)
        popupMenu.menuInflater.inflate(R.menu.time_range_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.weekRange -> {
                    binding.nameMonth.visibility = View.INVISIBLE
                    saveSelectedFilter(Constants.WEEK)
                    selectTimeRange.text = getString(R.string.week)
                    viewModel.setSelectedTimeRange(Constants.WEEK)
                    true
                }

                R.id.monthRange -> {
                    binding.nameMonth.visibility = View.VISIBLE
                    binding.nameMonth.text = DateTimeManager.getNameMonth()
                    saveSelectedFilter(Constants.MONTH)
                    selectTimeRange.text = getString(R.string.month)
                    viewModel.setSelectedTimeRange(Constants.MONTH)
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


    private fun initSelectedFilter() {
        val selectedItem = sharedPref.getInt("selected_item", Constants.WEEK)
        when (selectedItem) {
            Constants.WEEK -> {
                binding.nameMonth.visibility = View.INVISIBLE
                binding.selectTimeRange.text = getString(R.string.week)
                viewModel.setSelectedTimeRange(Constants.WEEK)
            }

            Constants.MONTH -> {
                binding.nameMonth.visibility = View.VISIBLE
                binding.nameMonth.text = DateTimeManager.getNameMonth()
                binding.selectTimeRange.text = getString(R.string.month)
                viewModel.setSelectedTimeRange(Constants.MONTH)
            }
        }
    }

    private fun observer() {
        viewModel.createdTasksCount.observe(viewLifecycleOwner) { createdCount ->
            viewModel.completedTasksCount.observe(viewLifecycleOwner) { completedCount ->
                binding.countCreated.text = createdCount.toString()
                binding.countCompleted.text = completedCount.toString()
                val productivity = completedCount.toFloat() / createdCount * 100
                if (productivity.isNaN())
                    binding.productivityRate.text = 0.toString() + "%"
                else {
                    val rounded = productivity.roundToInt()
                    binding.productivityRate.text = rounded.toString() + "%"
                }
                binding.progressBar.progress = productivity.toInt()
            }
        }
    }
}