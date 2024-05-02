package com.example.advizors


import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.advizors.models.note.Note
import com.example.advizors.models.note.NoteModel
import com.example.advizors.models.note.SerializableLatLng
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.example.advizors.AddNoteFragmentArgs

import com.squareup.picasso.Picasso
import java.util.Locale
import java.util.UUID

class AddNoteFragment : Fragment() {
    private lateinit var view: View
    private lateinit var noteContent: TextInputEditText
    private lateinit var spinner: ProgressBar
    private lateinit var attachPictureButton: ImageButton
    private lateinit var submitButton: MaterialButton
    private lateinit var deviceLocation: Location
    private lateinit var countryCode: String
    private var attachedPicture: Uri = Uri.EMPTY
    private var imageView: ImageView? = null
    private val auth = Firebase.auth
    private var hasImageChanged = false

    private val args: AddNoteFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(
            R.layout.fragment_add_note, container, false
        )

        getDeviceLocation()
        initViews(view)
        handleSubmitButton()
        handleAttachProductPicture()

//        requireActivity().findViewById<FloatingActionButton>(R.id.fab).isVisible = false;

        return view
    }

    private fun getDeviceLocation() {
        val geocoder = context?.let { Geocoder(it, Locale.getDefault()) }
        val addresses =
            geocoder?.getFromLocation(args.latitude.toDouble(), args.longitude.toDouble(), 1)
        if (addresses?.size!! > 0) {
            countryCode = addresses[0].countryCode
        } else {
            findNavController().popBackStack()
        }
    }

    private fun initViews(view: View) {
        spinner = view.findViewById(R.id.create_note_progress)
        noteContent = view.findViewById(R.id.note_content)
        attachPictureButton = view.findViewById(R.id.upload_picture_button)
        imageView = view.findViewById(R.id.selected_image)
        submitButton = view.findViewById(R.id.note_submit)

        if (args.note != null) {
            noteContent.setText(args.note!!.content)
            NoteModel.instance.getNoteImage(args.note!!.id) {
                attachedPicture = it
                Picasso.get().load(it).into(imageView)
                spinner.visibility = GONE
            }
        } else {
            spinner.visibility = GONE
        }


        spinner.visibility = GONE

    }

    private fun handleSubmitButton() {
        submitButton.setOnClickListener {
            createNewNote()
        }
    }

    private fun showDialogResponse(message: String) {
        val rootView: View = requireView()
        val snackBar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT)
        val snackBarView: View = snackBar.view
        snackBarView.setBackgroundColor(resources.getColor(R.color.black))
        val textView: TextView =
            snackBarView.findViewById(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(resources.getColor(R.color.white))
        snackBar.show()
    }

    private fun createNewNote() {
        val descriptionValue = noteContent.text.toString()

        if (descriptionValue.isEmpty()) {
            showDialogResponse("Please enter a description")
            return
        }
        if (attachedPicture == Uri.EMPTY) {
            showDialogResponse("Please select a picture")
            return
        }
        val noteId: String = if (args.note != null) {
            args.note!!.id
        } else {
            UUID.randomUUID().toString()
        }


        val newNote = auth.currentUser?.let {
            Note(
                noteId,
                noteContent.text.toString(),
                it.uid,
                SerializableLatLng(args.latitude.toDouble(), args.longitude.toDouble()),
            )
        }

        spinner.visibility = VISIBLE
        if (newNote != null) {
            if (args.note != null) {
                NoteModel.instance.updateNote(newNote) {
                    if (hasImageChanged) {
                        NoteModel.instance.updateNoteImage(newNote.id, attachedPicture) {
                            findNavController().popBackStack()
                        }
                    } else {
                        findNavController().popBackStack()
                    }
                }
            } else {
                NoteModel.instance.addNote(newNote, attachedPicture) {
                    findNavController().popBackStack()
                }
            }
        }
    }

    private val pickImageContract =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            try {
                uri?.let {
                    attachedPicture = it
                    Picasso.get().load(it).into(imageView)
                    hasImageChanged = true
                }
            } catch (e: Exception) {
                Log.d("CreateNote", "${e.message}")
            }
        }

    private fun handleAttachProductPicture() {
        attachPictureButton.setOnClickListener {
            pickImageContract.launch("image/*")
        }
    }

//    override fun onDestroyView(){
//        super.onDestroyView()
//        requireActivity().findViewById<FloatingActionButton>(R.id.fab).isVisible = true;
//    }
}
