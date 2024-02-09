package com.example.vkr_todolist.presentation.features.list

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.vkr_todolist.R
import com.example.vkr_todolist.databinding.FragmentAddEditListBinding
import com.example.vkr_todolist.presentation.model.ListUi
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddEditListFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddEditListBinding
    private val args: AddEditListFragmentArgs by navArgs()
    private val viewModel by viewModel<ListViewModel>()

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
        args.listId?.let { viewModel.getListById(it.first()) }
        viewModel.listItem.observe(viewLifecycleOwner) { listItem ->
            binding.edTitle.setText(listItem.title)
        }
        binding.bSaveList.setOnClickListener { createNewList() }
    }

    private fun createNewList() {
        if (binding.edTitle.text.isNullOrBlank())
            Snackbar.make(binding.root, R.string.snackbar_addTitle, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()
        else {
            if (args.listId != null) {
                viewModel.updateList(
                    ListUi(args.listId?.first(), binding.edTitle.text.toString())
                )
                findNavController().popBackStack()
                findNavController().popBackStack()
            }
            else {
                viewModel.addList(
                    ListUi(null, binding.edTitle.text.toString())
                )
                findNavController().popBackStack()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismiss()
    }
}