package com.example.advizors

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.advizors.models.note.Note
import com.example.advizors.models.note.NoteModel
import com.example.advizors.models.note.SerializableLatLng
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.auth.UserInfo
import com.google.firebase.auth.auth
import com.squareup.picasso.Picasso

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "noteId"

/**
 * A simple [Fragment] subclass.
 * Use the [ViewNoteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewNoteFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var noteId: String? = null
    private lateinit var editBtn: Button
    private lateinit var deleteBtn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var contentTv: TextView
    private lateinit var imageView: ImageView
    private val auth = Firebase.auth
    private lateinit var detailedNote: Note


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            noteId = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_note, container, false)
        editBtn = view.findViewById(R.id.details_edit_btn)
        deleteBtn = view.findViewById(R.id.details_delete_btn)
        progressBar = view.findViewById(R.id.details_progress_bar)

        contentTv = view.findViewById(R.id.details_content_tv)
        imageView = view.findViewById(R.id.details_image)
        editBtn.visibility = View.INVISIBLE
        deleteBtn.visibility = View.INVISIBLE
        detailedNote = noteId?.let { findNoteById(it) }!!
        contentTv.text = detailedNote.content
        if(detailedNote.userId == auth.currentUser?.uid  ) {
            editBtn.visibility = View.VISIBLE
            deleteBtn.visibility = View.VISIBLE
        }
        progressBar.visibility = View.INVISIBLE

        if (detailedNote.imageUrl != null && detailedNote.imageUrl!!.isNotEmpty()) {
            Picasso.get().load(detailedNote.imageUrl).into(imageView)
        }


        editBtn.setOnClickListener { v: View? ->
            //TODO I need you to navigate to AddNoteFragment
            val action = ViewNoteFragmentDirections.actionViewNoteFragmentToAddNoteFragment(0F,0F,detailedNote)
            Navigation.findNavController(v!!).navigate((action)
            )
        }
        deleteBtn.setOnClickListener { v: View? ->
            progressBar.visibility = View.VISIBLE
            NoteModel.instance.deleteNote(detailedNote) {
                findNavController().popBackStack()
            }
        }

        return view
    }

    private fun findNoteById(noteId: String): Note? {
        return NoteModel.instance.getAllNotes().value?.find { it.id == noteId }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ViewNoteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ViewNoteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}