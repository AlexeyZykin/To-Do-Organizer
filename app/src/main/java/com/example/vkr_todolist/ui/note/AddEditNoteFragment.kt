package com.example.vkr_todolist.ui.note

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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.example.vkr_todolist.App
import com.example.vkr_todolist.NavGraphDirections
import com.example.vkr_todolist.R
import com.example.vkr_todolist.data.model.ListItem
import com.example.vkr_todolist.data.model.Note
import com.example.vkr_todolist.databinding.FragmentAddEditNoteBinding
import com.example.vkr_todolist.ui.main.MainViewModel
import com.example.vkr_todolist.utils.HtmlManager
import com.example.vkr_todolist.utils.PermissionsUtil
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.text.SimpleDateFormat
import java.util.*


class AddEditNoteFragment : Fragment() {
    private lateinit var binding: FragmentAddEditNoteBinding
    private lateinit var popupMenu: PopupMenu
    private val args: AddEditNoteFragmentArgs by navArgs()
    private var noteInfo = Note(null, "", "", "", null, null)
    private var isCategorySelected = false
    private var pref: SharedPreferences? = null
    private val permissionLauncher = PermissionsUtil.registerForPermissionsResult(this@AddEditNoteFragment)

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as App).database)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabCreateNote.setOnClickListener {
            createNewNote()
        }
        binding.chipSelectList.setOnClickListener {
            initFilterList()
        }
        binding.chipImage.setOnClickListener {
            pickImageFromGallery()
        }
        binding.chipImage.setOnCloseIconClickListener {
            noteInfo.imagePath = null
            binding.imgCardView.visibility = View.GONE
            binding.imagePick.setImageBitmap(null)
            binding.chipImage.isCloseIconVisible=false
        }
        pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        initUpdate()
        setupMenu()
        setTextSize()
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


    private fun initFilterList() = with(binding) {
        popupMenu= PopupMenu(context?.applicationContext, chipSelectList)
        viewModel.allListItem.value?.forEach{listItem->
            popupMenu.menu.add(listItem.listTitle)
                .setIcon(R.drawable.ic_notes)
                .setOnMenuItemClickListener {menuItem->
                chipSelectList.text=menuItem.title
                noteInfo.listId = listItem.listId
                isCategorySelected = true
                true
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


    private fun initUpdate() = with (binding){
        if(args.note!=null){
            edTitle.setText(args.note!!.noteTitle)
            edDesc.setText(HtmlManager.getFromHtml(args.note?.noteDescription!!).trim())
            noteInfo.listId=args.note?.listId!!
            isCategorySelected=true
            if(args.note!!.imagePath != null){
                binding.chipImage.isCloseIconVisible = true
                noteInfo.imagePath = args.note!!.imagePath
                imagePick.setImageBitmap(BitmapFactory.decodeFile(noteInfo.imagePath))
            }
            viewModel.getListById(noteInfo.listId!!).observe(viewLifecycleOwner){
                it.forEach {
                    chipSelectList.text = it.listTitle
                }
            }
        }
        if(args.listName!= null){
            chipSelectList.text = args.listName!!.listTitle
            noteInfo.listId = args.listName!!.listId
        }
    }


    private fun updateNote() = with(binding){
       val tempNote = args.note?.copy(
            noteTitle = edTitle.text.toString(),
            noteDescription = HtmlManager.toHtml(edDesc.text),
            listId = noteInfo.listId,
            imagePath = noteInfo.imagePath
        )
        viewModel.updateNote(tempNote!!)
    }


    private fun createNewNote()=with(binding) {
        if(edTitle.text.isNullOrBlank())
            Snackbar.make(binding.root, R.string.snackbar_addTitle, Snackbar.LENGTH_SHORT).setAction("Action", null).show()
        else if(edDesc.text.isNullOrBlank())
            Snackbar.make(binding.root, R.string.snackbar_addDesc, Snackbar.LENGTH_SHORT).setAction("Action", null).show()
        else if(noteInfo.listId == null)
            Snackbar.make(binding.root, R.string.snackbar_selectList, Snackbar.LENGTH_SHORT).setAction("Action", null).show()
        else {
            if(args.note == null) {
                val tempNote = Note(
                    null,
                    binding.edTitle.text.toString(),
                    HtmlManager.toHtml(binding.edDesc.text),
                    getCurrentTime(),
                    noteInfo.listId,
                    noteInfo.imagePath
                )
                viewModel.insertNote(tempNote)
            }
            else{
                updateNote()
            }
            findNavController().popBackStack()
        }
    }


    private fun getCurrentTime() : String{
        val formatter=SimpleDateFormat("dd MMM, yyyy - HH:mm", Locale.getDefault())
        return formatter.format(Calendar.getInstance().time)
    }


    private fun setBoldForSelectedText() = with(binding){
        val startPos = edDesc.selectionStart
        val endPos = edDesc.selectionEnd

        val styles = edDesc.text.getSpans(startPos, endPos, StyleSpan::class.java)
        var boldStyle: StyleSpan? = null
        if(styles.isNotEmpty()){
            edDesc.text.removeSpan(styles[0])
        } else {
            boldStyle = StyleSpan(Typeface.BOLD)
        }

        edDesc.text.setSpan(boldStyle, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        edDesc.text.trim()
        edDesc.setSelection(startPos)
    }


    private fun setItalicForSelectedText() = with(binding){
        val startPos = edDesc.selectionStart
        val endPos = edDesc.selectionEnd

        val styles = edDesc.text.getSpans(startPos, endPos, StyleSpan::class.java)
        var boldStyle: StyleSpan? = null
        if(styles.isNotEmpty()){
            edDesc.text.removeSpan(styles[0])
        } else {
            boldStyle = StyleSpan(Typeface.ITALIC)
        }

        edDesc.text.setSpan(boldStyle, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        edDesc.text.trim()
        edDesc.setSelection(startPos)
    }


    private fun setTextSize() = with(binding){
        edTitle.setTextSize(pref?.getString("title_size_key","16"))
        edDesc.setTextSize(pref?.getString("content_size_key","16"))
    }

    private fun EditText.setTextSize(size: String?){
        if(size != null) this.textSize = size.toFloat()
    }


    private fun pickImageFromGallery(){
        if (PermissionsUtil.hasGalleryPermission(this@AddEditNoteFragment)) {
            var intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            if (intent.resolveActivity(requireActivity().packageManager) != null){
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
        var filePath:String? = null
        val cursor = requireActivity().contentResolver.query(contentUri,null,null,null,null)
        if (cursor == null){
            filePath = contentUri.path
        }else{
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
                val fileUri = data?.data!!

               // mProfileUri = fileUri
                binding.imagePick.setImageURI(fileUri)
                noteInfo.imagePath = getPathFromUri(fileUri)!!
                binding.chipImage.isCloseIconVisible = true
                binding.imgCardView.visibility = View.VISIBLE
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = AddEditNoteFragment()
    }
}