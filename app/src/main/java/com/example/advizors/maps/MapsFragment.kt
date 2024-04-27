package com.example.advizors.maps

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.advizors.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class MapsFragment : Fragment(), OnMapReadyCallback {
    private var viewModel: MapsViewModel? = null
    private var firestore: FirebaseFirestore? = null
    private  lateinit var view: View
    private var searchView: SearchView? = null
    private var mapMarkers: MutableList<Marker?> = ArrayList()

    override fun onMapReady(googleMap: GoogleMap) {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

//        // adding on query listener for our search view.
//        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String): Boolean {
//                displaySelectedMarkers(newText)
//                return false
//            }
//        })
//        googleMap.setOnMapLongClickListener { latLng: LatLng -> findNavController(view).navigate(MapsFragmentDirections.actionMapToFragmentAddNote(latLng.latitude.toFloat(), latLng.longitude.toFloat())) }
//        firestore?.collection("Notes")
//                ?.addSnapshotListener(EventListener { value, e ->
//                    if (e != null) {
//                        return@EventListener
//                    }
//                    for (doc in value!!) {
//                        addMarkerToMap(doc, googleMap)
//                    }
//                })
//        firestore?.collection("Notes")?.get()?.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                for (document in task.result) {
//                    addMarkerToMap(document, googleMap)
//                }
//            } else {
//                Log.d("Error", "Error getting documents: ", task.exception)
//            }
//        }
        val israel = LatLng(31.04, 34.8)
        //            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(israel))
    }

//    private fun createNoteMarkerBitmap(): Bitmap {
//        val height = 100
//        val width = 100
//        val bitmapdraw = resources.getDrawable(R.drawable.note, null) as BitmapDrawable
//        val b = bitmapdraw.bitmap
//        return Bitmap.createScaledBitmap(b, width, height, false)
//    }

//    private fun addMarkerToMap(document: QueryDocumentSnapshot, googleMap: GoogleMap) {
//        val note: Note = Note.create(document.getData())
//        if (note.isDeleted) return
//        val coordinate = LatLng(note.latitude, note.longitude)
//        val noteMarkerBitmap = createNoteMarkerBitmap()
//        val marker = googleMap.addMarker(MarkerOptions().position(coordinate)
//                .title(note.title))
//        marker!!.tag = document.id
//        marker.setIcon(BitmapDescriptorFactory.fromBitmap(noteMarkerBitmap))
//        googleMap.setOnMarkerClickListener { marker ->
//            val noteId = marker.tag.toString()
//            findNavController(view).navigate(MapsFragmentDirections.actionMapToNoteDetailsFragment(noteId))
//            true
//        }
//        mapMarkers.add(marker)
//    }

    fun displaySelectedMarkers(filterString: String) {
        for (mapMarker in mapMarkers) {
            mapMarker!!.isVisible = mapMarker.title!!.lowercase(Locale.getDefault())
                    .contains(filterString.lowercase(Locale.getDefault()))
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        view = inflater.inflate(R.layout.fragment_maps, container, false)
        searchView = view.findViewById(R.id.idSearchView)
        firestore = FirebaseFirestore.getInstance()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = getChildFragmentManager().findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this)[MapsViewModel::class.java]
    }
}