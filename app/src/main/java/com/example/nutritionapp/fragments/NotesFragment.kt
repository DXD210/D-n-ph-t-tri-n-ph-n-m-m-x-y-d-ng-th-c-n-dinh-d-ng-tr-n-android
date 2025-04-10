//package com.example.nutritionapp.fragments
//
//import android.app.AlertDialog
//import android.os.Bundle
//import android.text.Editable
//import android.text.TextWatcher
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.Observer
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.nutritionapp.App
//import com.example.nutritionapp.R
//import com.example.nutritionapp.adapters.NoteAdapter
//import com.example.nutritionapp.database.entities.Note
//import kotlinx.android.synthetic.main.dialog_note.view.*
//import kotlinx.android.synthetic.main.fragment_notes.*
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//class NotesFragment : Fragment(), NoteAdapter.OnNoteClickListener {
//
//    private val noteDao by lazy { App.getInstance().database.noteDao() }
//    private val userId by lazy { App.getInstance().preferenceManager.getUserId() }
//
//    private lateinit var noteAdapter: NoteAdapter
//    private var searchQuery = ""
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_notes, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        setupRecyclerView()
//        setupSearchView()
//        setupFabButton()
//
//        // Load initial data
//        loadNotes()
//    }
//
//    private fun setupRecyclerView() {
//        noteAdapter = NoteAdapter(requireContext(), this)
//        recyclerViewNotes.apply {
//            layoutManager = LinearLayoutManager(requireContext())
//            adapter = noteAdapter
//        }
//    }
//
//    private fun setupSearchView() {
//        editTextSearch.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//
//            override fun afterTextChanged(s: Editable?) {
//                searchQuery = s.toString().trim()
//                loadNotes()
//            }
//        })
//    }
//
//    private fun setupFabButton() {
//        fabAddNote.setOnClickListener {
//            showNoteDialog()
//        }
//    }
//
//    private fun loadNotes() {
//        progressBar.visibility = View.VISIBLE
//
//        if (searchQuery.isEmpty()) {
//            // Load all notes
//            noteDao.getNotesByUserId(userId).observe(viewLifecycleOwner, Observer { notes ->
//                updateNotesList(notes)
//            })
//        } else {
//            // Load filtered notes
//            noteDao.searchNotesByUserId(userId, searchQuery).observe(viewLifecycleOwner, Observer { notes ->
//                updateNotesList(notes)
//            })
//        }
//    }
//
//    private fun updateNotesList(notes: List<Note>) {
//        progressBar.visibility = View.GONE
//
//        if (notes.isEmpty()) {
//            textViewEmpty.visibility = View.VISIBLE
//            recyclerViewNotes.visibility = View.GONE
//        } else {
//            textViewEmpty.visibility = View.GONE
//            recyclerViewNotes.visibility = View.VISIBLE
//
//            noteAdapter.setNotes(notes)
//        }
//    }
//
//    private fun showNoteDialog(note: Note? = null) {
//        // Create dialog view
//        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_note, null)
//
//        // Set dialog title
//        val isEdit = note != null
//        dialogView.textViewDialogTitle.text = if (isEdit) "Edit Note" else "Add Note"
//
//        // Set note data if editing
//        if (isEdit) {
//            dialogView.editTextTitle.setText(note?.title)
//            dialogView.editTextContent.setText(note?.content)
//        }
//
//        // Create dialog
//        val dialog = AlertDialog.Builder(requireContext())
//            .setView(dialogView)
//            .setCancelable(true)
//            .create()
//
//        // Set button click listeners
//        dialogView.buttonCancel.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        dialogView.buttonSave.setOnClickListener {
//            val title = dialogView.editTextTitle.text.toString().trim()
//            val content = dialogView.editTextContent.text.toString().trim()
//
//            if (title.isEmpty()) {
//                dialogView.textInputLayoutTitle.error = "Title is required"
//                return@setOnClickListener
//            }
//
//            // Save note
//            saveNote(note?.id, title, content)
//
//            dialog.dismiss()
//        }
//
//        dialog.show()
//    }
//
//    private fun saveNote(noteId: Long?, title: String, content: String) {
//        CoroutineScope(Dispatchers.IO).launch {
//            val note = if (noteId != null) {
//                // Update existing note
//                Note(
//                    id = noteId,
//                    userId = userId,
//                    title = title,
//                    content = content
//                )
//            } else {
//                // Create new note
//                Note(
//                    userId = userId,
//                    title = title,
//                    content = content
//                )
//            }
//
//            if (noteId != null) {
//                noteDao.update(note)
//            } else {
//                noteDao.insert(note)
//            }
//
//            withContext(Dispatchers.Main) {
//                val actionText = if (noteId != null) "updated" else "created"
//                Toast.makeText(requireContext(), "Note $actionText successfully", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    override fun onEditClick(note: Note) {
//        showNoteDialog(note)
//    }
//
//    override fun onDeleteClick(note: Note) {
//        AlertDialog.Builder(requireContext())
//            .setTitle("Delete Note")
//            .setMessage("Are you sure you want to delete this note?")
//            .setPositiveButton("Delete") { _, _ ->
//                CoroutineScope(Dispatchers.IO).launch {
//                    noteDao.delete(note)
//
//                    withContext(Dispatchers.Main) {
//                        Toast.makeText(requireContext(), "Note deleted", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//            .setNegativeButton("Cancel", null)
//            .show()
//    }
//}

package com.example.nutritionapp.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nutritionapp.App
import com.example.nutritionapp.R
import com.example.nutritionapp.adapters.NoteAdapter
import com.example.nutritionapp.database.entities.Note
import com.example.nutritionapp.databinding.FragmentNotesBinding
import com.example.nutritionapp.databinding.DialogNoteBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotesFragment : Fragment(), NoteAdapter.OnNoteClickListener {

    private lateinit var binding: FragmentNotesBinding
    private val noteDao by lazy { App.getInstance().database.noteDao() }
    private val userId by lazy { App.getInstance().preferenceManager.getUserId() }

    private lateinit var noteAdapter: NoteAdapter
    private var searchQuery = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchView()
        setupFabButton()

        // Load initial data
        loadNotes()
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter(requireContext(), this)
        binding.recyclerViewNotes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = noteAdapter
        }
    }

    private fun setupSearchView() {
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                searchQuery = s.toString().trim()
                loadNotes()
            }
        })
    }

    private fun setupFabButton() {
        binding.fabAddNote.setOnClickListener {
            showNoteDialog()
        }
    }

    private fun loadNotes() {
        binding.progressBar.visibility = View.VISIBLE

        if (searchQuery.isEmpty()) {
            // Load all notes
            noteDao.getNotesByUserId(userId).observe(viewLifecycleOwner, Observer { notes ->
                updateNotesList(notes)
            })
        } else {
            // Load filtered notes
            noteDao.searchNotesByUserId(userId, searchQuery).observe(viewLifecycleOwner, Observer { notes ->
                updateNotesList(notes)
            })
        }
    }

    private fun updateNotesList(notes: List<Note>) {
        binding.progressBar.visibility = View.GONE

        if (notes.isEmpty()) {
            binding.textViewEmpty.visibility = View.VISIBLE
            binding.recyclerViewNotes.visibility = View.GONE
        } else {
            binding.textViewEmpty.visibility = View.GONE
            binding.recyclerViewNotes.visibility = View.VISIBLE

            noteAdapter.setNotes(notes)
        }
    }

    private fun showNoteDialog(note: Note? = null) {
        // Create dialog view
        val dialogBinding = DialogNoteBinding.inflate(LayoutInflater.from(requireContext()))

        // Set dialog title
        val isEdit = note != null
        dialogBinding.textViewDialogTitle.text = if (isEdit) "Edit Note" else "Add Note"

        // Set note data if editing
        if (isEdit) {
            dialogBinding.editTextTitle.setText(note?.title)
            dialogBinding.editTextContent.setText(note?.content)
        }

        // Create dialog
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()

        // Set button click listeners
        dialogBinding.buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.buttonSave.setOnClickListener {
            val title = dialogBinding.editTextTitle.text.toString().trim()
            val content = dialogBinding.editTextContent.text.toString().trim()

            if (title.isEmpty()) {
                dialogBinding.textInputLayoutTitle.error = "Title is required"
                return@setOnClickListener
            }

            // Save note
            saveNote(note?.id, title, content)

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun saveNote(noteId: Long?, title: String, content: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val note = if (noteId != null) {
                // Update existing note
                Note(
                    id = noteId,
                    userId = userId,
                    title = title,
                    content = content
                )
            } else {
                // Create new note
                Note(
                    userId = userId,
                    title = title,
                    content = content
                )
            }

            if (noteId != null) {
                noteDao.update(note)
            } else {
                noteDao.insert(note)
            }

            withContext(Dispatchers.Main) {
                val actionText = if (noteId != null) "updated" else "created"
                Toast.makeText(requireContext(), "Note $actionText successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditClick(note: Note) {
        showNoteDialog(note)
    }

    override fun onDeleteClick(note: Note) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Delete") { _, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    noteDao.delete(note)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Note deleted", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}