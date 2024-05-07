package com.example.advizors.maps

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.advizors.R
import com.example.advizors.ViewNoteFragmentArgs
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


class MapsFragment : Fragment(), OnMapReadyCallback {
    private var viewModel: MapsViewModel? = null
    private lateinit var view: View
    private var searchView: SearchView? = null
    private var mapMarkers: MutableList<Marker?> = ArrayList()
    private lateinit var users: MutableList<User>
    private val allMarkersMap: HashMap<Marker, String> = HashMap()



//    private lateinit var  currentMap :GoogleMap


    fun displaySelectedMarkers(filterString: String) {
        val userId = users.firstOrNull { it.firstName.lowercase() == filterString.lowercase() }?.id
        mapMarkers.forEach {
            it!!.isVisible = if (userId == null) true else allMarkersMap[it] == userId
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        view = inflater.inflate(R.layout.fragment_maps, container, false)
        searchView = view.findViewById(R.id.idSearchView)
        UserModel.instance.getAllUsers().observe(viewLifecycleOwner) { users = it }
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
//        currentMap = googleMap
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                displaySelectedMarkers(newText)
                return false
            }
        })
        googleMap.setOnMapLongClickListener { latLng: LatLng ->
            Navigation.findNavController(view).navigate(
                MapsFragmentDirections.actionMapsFragmentToAddNoteFragment(
                    latLng.latitude.toFloat(), latLng.longitude.toFloat(), null
                )
            )
        }

        val notes = NoteModel.instance.getAllNotes()
        notes.observe(viewLifecycleOwner) { notesList ->
            notesList.forEach { addMarkerToMap(it, googleMap) }
        }


        val israel = LatLng(31.04, 34.8)
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(israel))
    }

    private fun addMarkerToMap(note: Note, googleMap: GoogleMap) {
        if (note.isDeleted) return
        val coordinate = LatLng(note.position.latitude, note.position.longitude)
        val marker = googleMap.addMarker(MarkerOptions().position(coordinate))
        if (marker != null)
        {
            allMarkersMap[marker] = note.userId
        }
        marker!!.tag = note.id
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(createNoteMarkerBitmap()))
        googleMap.setOnMarkerClickListener {
            val noteId = it.tag.toString()
            Navigation.findNavController(view)
                .navigate(MapsFragmentDirections.actionMapsFragmentToViewNoteFragment(noteId))
            true
        }
        mapMarkers.add(marker)

    }

    private fun createNoteMarkerBitmap(): Bitmap {
        val height = 115
        val width = 115
        val bitmapdraw = resources.getDrawable(R.drawable.note, null) as BitmapDrawable
        val b = bitmapdraw.bitmap
        return Bitmap.createScaledBitmap(b, width, height, false)
    }

    override fun onDestroyView() {
//        allMarkersMap.forEach{(key,value)->
//            key.remove()
//        }
        for (i in mapMarkers.indices)
        {
            if(mapMarkers[i] != null){
                mapMarkers[i]?.remove()

            }
        }
//        currentMap.clear()
        super.onDestroyView()
    }
}