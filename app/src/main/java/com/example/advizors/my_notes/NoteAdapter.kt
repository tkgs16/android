package com.example.advizors.my_notes

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.advizors.models.note.Note
import com.example.advizors.R

class NoteAdapter(private var notes: List<Note>) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.bind(note)
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    fun updateNotes(newNotes: List<Note>) {
        notes = newNotes
        notifyDataSetChanged()
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewContent: TextView = itemView.findViewById(R.id.textViewContent)
        private val imageViewNote: ImageView = itemView.findViewById(R.id.imageViewNote)

        fun bind(note: Note) {
            textViewContent.text = note.content

            // Check if note has image URL, if yes, load it using Glide
            note.imageUrl?.let { imageUrl ->
                imageViewNote.visibility = View.VISIBLE
                Glide.with(itemView.context)
                    .load(Uri.parse(imageUrl))
                    .centerCrop()
                    .into(imageViewNote)
            } ?: run {
                imageViewNote.visibility = View.GONE
            }
        }
    }
}
