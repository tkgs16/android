import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.advizors.data.note.Note
import com.example.advizors.models.my_notes.NoteViewModel
import com.example.advizors.R
import androidx.navigation.fragment.findNavController

class MyNotesFragment : Fragment(), NoteAdapter.OnNoteClickListener {

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_notes, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        val emptyListTextView: TextView = view.findViewById(R.id.textViewEmptyList)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        noteAdapter = NoteAdapter(emptyList(), this)
        recyclerView.adapter = noteAdapter

        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

        noteViewModel.getMyNotes().observe(viewLifecycleOwner, Observer { notes ->
            if (notes.isNotEmpty()) {
                recyclerView.visibility = View.VISIBLE
                emptyListTextView.visibility = View.GONE
                updateNotesList(notes)
            } else {
                recyclerView.visibility = View.GONE
                emptyListTextView.visibility = View.VISIBLE
            }
        })

        return view
    }

    private fun updateNotesList(notes: List<Note>) {
        noteAdapter.updateNotes(notes)
    }

    override fun onNoteClicked(noteId: String) {
        findNavController().navigate(MyNotesFragmentDirections.actionMyNotesFragmentToViewNoteFragment(noteId))
    }
}
