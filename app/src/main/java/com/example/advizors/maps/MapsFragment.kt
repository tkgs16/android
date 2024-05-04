package com.example.advizors.maps

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class MapsFragment : Fragment(), OnMapReadyCallback {
    private var viewModel: MapsViewModel? = null
    private lateinit var view: View
    private var searchView: SearchView? = null
    private var mapMarkers: MutableList<Marker?> = ArrayList()
    private lateinit var users: MutableList<User>
    private val allMarkersMap: HashMap<Marker, String> = HashMap()


    fun displaySelectedMarkers(filterString: String) {
        val userId = users.firstOrNull { it.firstName.lowercase() == filterString.lowercase() }?.id
        for (mapMarker in mapMarkers) {
            if (userId == null) mapMarker!!.isVisible = true
            else mapMarker!!.isVisible = allMarkersMap[mapMarker] == userId
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_maps, container, false)
        searchView = view.findViewById(R.id.idSearchView)
        UserModel.instance.getAllUsers().observe(viewLifecycleOwner) {
            users = it
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            getChildFragmentManager().findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this)[MapsViewModel::class.java]
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // adding on query listener for our search view.
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                displaySelectedMarkers(newText)
                return false
            }
        })
//        var note = Note("","","", )
        googleMap.setOnMapLongClickListener { latLng: LatLng ->
            Navigation.findNavController(view).navigate(
                MapsFragmentDirections.actionMapsFragmentToAddNoteFragment(
                    latLng.latitude.toFloat(),
                    latLng.longitude.toFloat(),
                    null
                )
            )
        }

        val notes = NoteModel.instance.getAllNotes()
        notes.observe(viewLifecycleOwner) { notesList ->
            notesList.forEach { addMarkerToMap(it, googleMap) }
        }


        val israel = LatLng(31.04, 34.8)
        //            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(israel))
    }

    private fun addMarkerToMap(note: Note, googleMap: GoogleMap) {
        if (note.isDeleted) return
        val coordinate = LatLng(note.position.latitude, note.position.longitude)
//        val noteMarkerBitmap = createNoteMarkerBitmap()
        val marker = googleMap.addMarker(MarkerOptions().position(coordinate))
        if (marker != null) {
            allMarkersMap[marker] = note.userId
        }
        marker!!.tag = note.id
//        marker.setIcon(BitmapDescriptorFactory.fromBitmap(noteMarkerBitmap))
        googleMap.setOnMarkerClickListener {
            val noteId = it.tag.toString()
            Navigation.findNavController(view)
                .navigate(MapsFragmentDirections.actionMapsFragmentToViewNoteFragment(noteId))
            true
        }
        mapMarkers.add(marker)
    }


}