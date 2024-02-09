package com.example.vkr_todolist.presentation.features.note.addEditNote

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Spannable
import android.text.style.StyleSpan
import android.view.*
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.example.vkr_todolist.NavGraphDirections
import com.example.vkr_todolist.R
import com.example.vkr_todolist.cache.room.model.NoteCache
import com.example.vkr_todolist.databinding.FragmentAddEditNoteBinding
import com.example.vkr_todolist.presentation.model.ListUi
import com.example.vkr_todolist.presentation.model.NoteUi
import com.example.vkr_todolist.presentation.utils.HtmlManager
import com.example.vkr_todolist.presentation.utils.PermissionsUtil
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*


class AddEditNoteFragment : Fragment() {
    private lateinit var binding: FragmentAddEditNoteBinding
    private lateinit var popupMenu: PopupMenu
    private val args: AddEditNoteFragmentArgs by navArgs()
    private val listUi = ListUi(null, "")
    private val noteUi = NoteUi(null, "", null, "", listUi, null)
    private var isCategorySelected = false
    private var pref: SharedPreferences? = null
    private val permissionLauncher =
        PermissionsUtil.registerForPermissionsResult(this@AddEditNoteFragment)
    private val viewModel by viewModel<AddEditNoteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.noteId?.let { viewModel.getNote(it.first()) }
        args.listId?.let { viewModel.getList(it.first()) }
        pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        viewModel.getAllLists()
        subscribeObservers()
        setupMenu()
        setTextSize()
        binding.fabCreateNote.setOnClickListener { createNewNote() }
        binding.chipSelectList.setOnClickListener { initFilterList() }
        binding.chipImage.setOnClickListener { pickImageFromGallery() }
        binding.chipImage.setOnCloseIconClickListener { closeIconImage() }
    }

    private fun closeIconImage() {
        noteUi.imagePath = null
        binding.imgCardView.visibility = View.GONE
        binding.imagePick.setImageBitmap(null)
        binding.chipImage.isCloseIconVisible = false
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.nav_note_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.id_bold -> {
                        setBoldForSelectedText()
                        return true
                    }

                    R.id.id_italic -> {
                        setItalicForSelectedText()
                        return true
                    }
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


    private fun initFilterList() {
        popupMenu = PopupMenu(context?.applicationContext, binding.chipSelectList)
        viewModel.lists.observe(viewLifecycleOwner) {
            it?.forEach { list ->
                popupMenu.menu.add(list.title)
                    .setIcon(R.drawable.ic_notes)
                    .setOnMenuItemClickListener { menuItem ->
                        noteUi.list = list
                        binding.chipSelectList.text = menuItem.title
                        isCategorySelected = true
                        true
                    }
            }
        }
        popupMenu.menu.add(Menu.NONE, R.id.addEditListFragment, Menu.NONE, R.string.add_new_list)
            .setIcon(R.drawable.ic_add)
            .setOnMenuItemClickListener {
                val action = NavGraphDirections.actionGlobalAddEditListFragment(null)
                findNavController().navigate(action)
                true
            }
        popupMenu.show()
    }


    private fun subscribeObservers() {
        viewModel.listItem.observe(viewLifecycleOwner) {
            it?.let {
                binding.chipSelectList.text = it.title
                noteUi.list = it
            }
        }
        viewModel.note.observe(viewLifecycleOwner) {
            it?.let { note ->
                binding.edTitle.setText(note.title)
                binding.edDesc.setText(note.description?.let { desc ->
                    HtmlManager.getFromHtml(desc).trim()
                })
                binding.chipSelectList.text = note.list.title
                isCategorySelected = true
                note.imagePath?.let { image ->
                    binding.chipImage.isCloseIconVisible = true
                    binding.imagePick.setImageBitmap(BitmapFactory.decodeFile(image))
                }
                noteUi.apply {
                    id = note.id
                    title = note.title
                    description = note.description
                    list = note.list
                    time = note.time
                    imagePath = note.imagePath
                }
            }
        }
    }


    private fun updateNote() = with(binding) {
        noteUi.title = binding.edTitle.text.toString()
        noteUi.description = HtmlManager.toHtml(binding.edDesc.text)
        viewModel.updateNote(noteUi)
    }

    private fun createNewNote() {
        when {
            binding.edTitle.text.isNullOrBlank() -> Snackbar.make(
                binding.root,
                R.string.snackbar_addTitle,
                Snackbar.LENGTH_SHORT
            )
                .setAction("Action", null).show()

            binding.edDesc.text.isNullOrBlank() -> Snackbar.make(
                binding.root,
                R.string.snackbar_addDesc,
                Snackbar.LENGTH_SHORT
            )
                .setAction("Action", null).show()

            binding.chipSelectList.text == getString(R.string.select_list) -> Snackbar.make(
                binding.root,
                R.string.snackbar_selectList,
                Snackbar.LENGTH_SHORT
            )
                .setAction("Action", null).show()

            else -> {
                if (args.noteId == null){
                    noteUi.title = binding.edTitle.text.toString()
                    noteUi.description = HtmlManager.toHtml(binding.edDesc.text)
                    noteUi.time = getCurrentTime()
                    viewModel.addNote(noteUi)
                }
                else updateNote()
                findNavController().popBackStack()
            }
        }
    }


    private fun getCurrentTime(): String {
        val formatter = SimpleDateFormat("dd MMM, yyyy - HH:mm", Locale.getDefault())
        return formatter.format(Calendar.getInstance().time)
    }


    private fun setBoldForSelectedText() = with(binding) {
        val startPos = edDesc.selectionStart
        val endPos = edDesc.selectionEnd

        val styles = edDesc.text.getSpans(startPos, endPos, StyleSpan::class.java)
        var boldStyle: StyleSpan? = null
        if (styles.isNotEmpty()) {
            edDesc.text.removeSpan(styles[0])
        } else {
            boldStyle = StyleSpan(Typeface.BOLD)
        }

        edDesc.text.setSpan(boldStyle, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        edDesc.text.trim()
        edDesc.setSelection(startPos)
    }


    private fun setItalicForSelectedText() = with(binding) {
        val startPos = edDesc.selectionStart
        val endPos = edDesc.selectionEnd

        val styles = edDesc.text.getSpans(startPos, endPos, StyleSpan::class.java)
        var boldStyle: StyleSpan? = null
        if (styles.isNotEmpty()) {
            edDesc.text.removeSpan(styles[0])
        } else {
            boldStyle = StyleSpan(Typeface.ITALIC)
        }

        edDesc.text.setSpan(boldStyle, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        edDesc.text.trim()
        edDesc.setSelection(startPos)
    }


    private fun setTextSize() = with(binding) {
        edTitle.setTextSize(pref?.getString("title_size_key", "16"))
        edDesc.setTextSize(pref?.getString("content_size_key", "16"))
    }

    private fun EditText.setTextSize(size: String?) {
        if (size != null) this.textSize = size.toFloat()
    }


    private fun pickImageFromGallery() {
        if (PermissionsUtil.hasGalleryPermission(this@AddEditNoteFragment)) {
            var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startForProfileImageResult.launch(intent)
            }
        } else {
            permissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }
    }

    private fun getPathFromUri(contentUri: Uri): String? {
        var filePath: String? = null
        val cursor = requireActivity().contentResolver.query(contentUri, null, null, null, null)
        if (cursor == null) {
            filePath = contentUri.path
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }


    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data
                noteUi.imagePath = fileUri?.let { getPathFromUri(it) }
                binding.imagePick.setImageURI(fileUri)
                binding.chipImage.isCloseIconVisible = true
                binding.imgCardView.visibility = View.VISIBLE
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
}