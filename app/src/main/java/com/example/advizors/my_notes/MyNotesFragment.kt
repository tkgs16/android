import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.advizors.models.note.Note
import com.example.advizors.my_notes.NoteViewModel
import com.example.advizors.R

class MyNotesFragment : Fragment(), NoteAdapter.OnNoteClickListener {

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var spinner: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_notes, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        spinner = view.findViewById(R.id.my_notes_spinner)
        spinner.visibility = View.VISIBLE
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        noteAdapter = NoteAdapter(emptyList(), this) // Initially empty, will be updated later
        recyclerView.adapter = noteAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

        // Observe notes for the current user
        noteViewModel.getMyNotes().observe(viewLifecycleOwner, Observer { notes ->
            notes?.let {
                updateNotesList(it)
                spinner.visibility = View.INVISIBLE
            }
        })
    }

    private fun updateNotesList(notes: List<Note>) {
        noteAdapter.updateNotes(notes)
    }

    override fun onNoteClicked(noteId: String) {
        // Handle navigation to another fragment with the noteId
        // Example: Navigate to a new fragment using Navigation Component
//        val action = MyNotesFragmentDirections.actionMyNotesFragmentToNoteDetailFragment(noteId)
//        findNavController().navigate(action)
        Log.d("BRO", noteId)
    }
}