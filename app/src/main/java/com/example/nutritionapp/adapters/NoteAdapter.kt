//package com.example.nutritionapp.adapters
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.example.nutritionapp.R
//import com.example.nutritionapp.database.entities.Note
//import com.example.nutritionapp.utils.DateUtils
//import kotlinx.android.synthetic.main.item_note.view.*
//
//class NoteAdapter(
//    private val context: Context,
//    private val listener: OnNoteClickListener
//) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
//
//    private val notes = mutableListOf<Note>()
//
//    interface OnNoteClickListener {
//        fun onEditClick(note: Note)
//        fun onDeleteClick(note: Note)
//    }
//
//    fun setNotes(newNotes: List<Note>) {
//        notes.clear()
//        notes.addAll(newNotes)
//        notifyDataSetChanged()
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
//        val view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false)
//        return NoteViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
//        holder.bind(notes[position])
//    }
//
//    override fun getItemCount(): Int = notes.size
//
//    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        fun bind(note: Note) {
//            // Set note title
//            itemView.textViewNoteTitle.text = note.title
//
//            // Set note content
//            itemView.textViewNoteContent.text = note.content ?: ""
//
//            // Set note date
//            itemView.textViewNoteDate.text = DateUtils.formatDate(note.updatedAt)
//
//            // Set edit button click listener
//            itemView.imageViewEdit.setOnClickListener {
//                listener.onEditClick(note)
//            }
//
//            // Set delete button click listener
//            itemView.imageViewDelete.setOnClickListener {
//                listener.onDeleteClick(note)
//            }
//        }
//    }
//}

package com.example.nutritionapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nutritionapp.database.entities.Note
import com.example.nutritionapp.databinding.ItemNoteBinding
import com.example.nutritionapp.utils.DateUtils

class NoteAdapter(
    private val context: Context,
    private val listener: OnNoteClickListener
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private val notes = mutableListOf<Note>()

    interface OnNoteClickListener {
        fun onEditClick(note: Note)
        fun onDeleteClick(note: Note)
    }

    fun setNotes(newNotes: List<Note>) {
        notes.clear()
        notes.addAll(newNotes)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    override fun getItemCount(): Int = notes.size

    inner class NoteViewHolder(private val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note) {
            // Set note title
            binding.textViewNoteTitle.text = note.title

            // Set note content
            binding.textViewNoteContent.text = note.content ?: ""

            // Set note date
            binding.textViewNoteDate.text = DateUtils.formatDate(note.updatedAt)

            // Set edit button click listener
            binding.imageViewEdit.setOnClickListener {
                listener.onEditClick(note)
            }

            // Set delete button click listener
            binding.imageViewDelete.setOnClickListener {
                listener.onDeleteClick(note)
            }
        }
    }
}