import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.advizors.R
import com.example.advizors.data.note.Note
import com.example.advizors.models.my_notes.NoteViewModel

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
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        noteAdapter = NoteAdapter(emptyList(), this)
        recyclerView.adapter = noteAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]

         noteViewModel.getMyNotes().observe(viewLifecycleOwner) {
             it?.let { updateNotesList(it) }
         }
    }

    private fun updateNotesList(notes: List<Note>) { noteAdapter.updateNotes(notes) }

    override fun onNoteClicked(noteId: String) {
        findNavController().navigate(MyNotesFragmentDirections.actionMyNotesFragmentToViewNoteFragment(noteId))
    }
}
