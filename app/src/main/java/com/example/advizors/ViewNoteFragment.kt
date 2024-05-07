package com.example.advizors.maps

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.example.advizors.R
import com.example.advizors.models.note.Note
import com.example.advizors.models.note.NoteModel
import com.example.advizors.models.user.User
import com.example.advizors.models.user.UserModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


/**
 * A simple [Fragment] subclass.
 * Use the [ViewNoteFragment] factory method to
 * create an instance of this fragment.
 */
class ViewNoteFragment : Fragment() {
    private lateinit var editBtn: Button
    private lateinit var deleteBtn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var contentTv: TextView
    private lateinit var userNameTv: TextView
    private lateinit var imageView: ImageView
    private val auth = Firebase.auth
    private lateinit var detailedNote: Note
    private lateinit var detailedUser : User
    private val args: ViewNoteFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_note, container, false)
        editBtn = view.findViewById(R.id.details_edit_btn)
        deleteBtn = view.findViewById(R.id.details_delete_btn)
        progressBar = view.findViewById(R.id.details_progress_bar)
        userNameTv = view.findViewById(R.id.user_name_tv)
        contentTv = view.findViewById(R.id.details_content_tv)
        imageView = view.findViewById(R.id.details_image)
        progressBar.visibility = View.VISIBLE
        editBtn.visibility = View.INVISIBLE
        deleteBtn.visibility = View.INVISIBLE
        NoteModel.instance.getAllNotes().observe(viewLifecycleOwner) { it ->
            detailedNote = it.find { it.id == args.noteId }!!
            if (detailedNote.imageUrl != null && detailedNote.imageUrl!!.isNotEmpty()) {
                Picasso.get().load(detailedNote.imageUrl)
                    .into(imageView, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            progressBar.visibility = View.INVISIBLE
                            contentTv.text = "Content : ${detailedNote.content}"

                            UserModel.instance.getAllUsers().observe(viewLifecycleOwner){it->
                                detailedUser = it.find { it.id == detailedNote.userId }!!
                                userNameTv.text = "Note by: ${detailedUser?.firstName} ${detailedUser?.lastName}"
                            }
                            if (detailedNote.userId == auth.currentUser?.uid) {
                                editBtn.visibility = View.VISIBLE
                                deleteBtn.visibility = View.VISIBLE
                            }
                        }

                        override fun onError(e: Exception?) {
                            Toast.makeText(
                                activity,
                                "Failed to load image",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    })
            }

        }


        editBtn.setOnClickListener { v: View? ->
            val action = ViewNoteFragmentDirections.actionViewNoteFragmentToAddNoteFragment(
                detailedNote.position.latitude.toFloat(),
                detailedNote.position.longitude.toFloat(),
                detailedNote
            )
            Navigation.findNavController(v!!).navigate(
                (action)
            )
        }
        deleteBtn.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            detailedNote.let {
                NoteModel.instance.deleteNote(it) {
//                    findNavController().popBackStack()
                    var fm:FragmentManager = requireActivity().supportFragmentManager
                    val fragmentTransaction = fm.beginTransaction()
                    for(i in 0 until fm.backStackEntryCount) {
                    fm.popBackStack();
                }
                    findNavController().navigate(ViewNoteFragmentDirections.actionViewNoteFragmentToNavDeleteUi())
                }
            }
        }

        return view
    }

}