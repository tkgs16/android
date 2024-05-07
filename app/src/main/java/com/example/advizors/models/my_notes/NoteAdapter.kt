import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.advizors.data.note.Note
import com.example.advizors.R
import com.squareup.picasso.Picasso

class NoteAdapter(private var notes: List<Note>, private val listener: OnNoteClickListener) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

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

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val textViewContent: TextView = itemView.findViewById(R.id.textViewContent)
        private val imageViewNote: ImageView = itemView.findViewById(R.id.imageViewNote)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val noteId = notes[position].id
                listener.onNoteClicked(noteId)
            }
        }

        fun bind(note: Note) {
            textViewContent.text = note.content

            note.imageUrl?.let { imageUrl ->
                imageViewNote.visibility = View.VISIBLE
                Picasso.get()
                    .load(imageUrl)
                    .fit()
                    .centerCrop()
                    .into(imageViewNote)
            } ?: run {
                imageViewNote.visibility = View.GONE
            }
        }
    }

    interface OnNoteClickListener {
        fun onNoteClicked(noteId: String)
    }
}
