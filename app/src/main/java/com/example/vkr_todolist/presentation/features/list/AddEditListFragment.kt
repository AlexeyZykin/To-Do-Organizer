package com.example.vkr_todolist.presentation.features.list

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.vkr_todolist.app.App
import com.example.vkr_todolist.R
import com.example.vkr_todolist.data.source.local.model.ListItem
import com.example.vkr_todolist.databinding.FragmentAddEditListBinding
import com.example.vkr_todolist.presentation.main.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar

class AddEditListFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddEditListBinding
    private val args: AddEditListFragmentArgs by navArgs()
    private val viewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as App).database)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddEditListBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bSaveList.setOnClickListener {
            createNewList()
        }
        initUpdate()
    }

    private fun initUpdate() = with (binding){
        if(args.listItem!=null){
            edTitle.setText(args.listItem!!.listTitle)
        }
    }


    private fun updateListItem()=with(binding){
        val tempList = args.listItem?.copy(
            listTitle = edTitle.text.toString()
        )
        viewModel.updateListItem(tempList!!)
    }


    private fun createNewList()= with(binding){
        if(edTitle.text.isNullOrBlank())
            Snackbar.make(binding.root, R.string.snackbar_addTitle, Snackbar.LENGTH_SHORT).setAction("Action", null).show()
        else {
            if(args.listItem == null) {
                val tempList = ListItem(
                    null,
                    edTitle.text.toString(),
                    R.color.primary_color1,
                    0,
                    0,
                    ""
                )
                viewModel.insertListItem(tempList)
                findNavController().popBackStack()
            }
            else {
                updateListItem()
                findNavController().popBackStack()
                findNavController().popBackStack()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddEditListFragment()
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismiss()
    }

}