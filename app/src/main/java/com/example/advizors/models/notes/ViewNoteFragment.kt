package com.example.advizors.models.notes

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.advizors.GeoApiManager
import com.example.advizors.Location
import com.example.advizors.LocationResponse
import com.example.advizors.R
import com.example.advizors.data.note.Note
import com.example.advizors.data.note.NoteModel
import com.example.advizors.data.user.User
import com.example.advizors.data.user.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

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
    private lateinit var countryTv: TextView
    private lateinit var streetTv: TextView
    private lateinit var cityTv: TextView
    private lateinit var userNameTv: TextView
    private lateinit var imageView: ImageView
    private val auth = Firebase.auth
    private lateinit var detailedNote: Note
    private lateinit var detailedUser: User
    private val args: ViewNoteFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_note, container, false)
        editBtn = view.findViewById(R.id.details_edit_btn)
        deleteBtn = view.findViewById(R.id.details_delete_btn)
        progressBar = view.findViewById(R.id.details_progress_bar)
        userNameTv = view.findViewById(R.id.user_name_tv)
        contentTv = view.findViewById(R.id.details_content_tv)
        countryTv = view.findViewById(R.id.country_tv)
        streetTv = view.findViewById(R.id.street_tv)
        cityTv = view.findViewById(R.id.city_tv)
        imageView = view.findViewById(R.id.details_image)
        progressBar.visibility = View.VISIBLE
        editBtn.visibility = View.INVISIBLE
        deleteBtn.visibility = View.INVISIBLE

        NoteModel.instance.getAllNotes().observe(viewLifecycleOwner) { it ->
            detailedNote = it.find { it.id == args.noteId }!!
            getGeoApiInfo(
                detailedNote.position.longitude,
                detailedNote.position.latitude
            )
            if (detailedNote.imageUrl != null && detailedNote.imageUrl!!.isNotEmpty()) {
                Picasso.get().load(detailedNote.imageUrl)
                    .into(imageView, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            progressBar.visibility = View.INVISIBLE
                            contentTv.text = "Content : ${detailedNote.content}"
                            UserModel.instance.getAllUsers().observe(viewLifecycleOwner) { it ->
                                detailedUser = it.find { it.id == detailedNote.userId }!!
                                userNameTv.text =
                                    "Note by: ${detailedUser?.firstName} ${detailedUser?.lastName}"
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
            val action =
                ViewNoteFragmentDirections.actionViewNoteFragmentToAddNoteFragment(
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
                    var fm: FragmentManager = requireActivity().supportFragmentManager
                    val fragmentTransaction = fm.beginTransaction()
                    for (i in 0 until fm.backStackEntryCount) {
                        fm.popBackStack();
                    }
                    findNavController().navigate(ViewNoteFragmentDirections.actionViewNoteFragmentToNavDeleteUi())
                }
            }
        }

        return view
    }

    private fun getGeoApiInfo(longitude: Double, latitude: Double) {
        val call = GeoApiManager().getLocation(longitude, latitude)
        call.enqueue(object : Callback<LocationResponse> {
            override fun onResponse(
                call: Call<LocationResponse>,
                response: Response<LocationResponse>
            ) {
                if (response.isSuccessful) {
                    val res = response.body()
                    if (res != null) {
                        val location = res.features[0].properties
                        if (location.country != null) {
                            countryTv.text = "Country : ${location.country}"
                        }
                        if (location.city != null) {
                            cityTv.text = "City : ${location.city}"
                        }
                        if (location.street != null) {
                            streetTv.text = "Street : ${location.street}"
                        }
                    }
                }
            }

            override fun onFailure(call: Call<LocationResponse>, t: Throwable) {
                // Handle failure
                Log.d("Error", "unable to fetch additional info about post location")
            }
        })
    }


}