package com.example.advizors.models.user.edit

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.RequiresExtension
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.advizors.MainActivity
import com.example.advizors.R
import com.example.advizors.data.user.User
import com.example.advizors.data.user.UserModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.squareup.picasso.Picasso

class EditUserFragment : Fragment() {

    private val auth = Firebase.auth
    private lateinit var imageSelectionCallBack: ActivityResultLauncher<Intent>
    private var selectedImageURI: Uri? = null
    private lateinit var firstNameInputLayout: TextInputLayout
    private lateinit var firstNameEditText: TextInputEditText
    private lateinit var lastNameInputLayout: TextInputLayout
    private lateinit var lastNameEditText: TextInputEditText

    private lateinit var progressBar: ProgressBar

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_user, container, false)

        progressBar = view.findViewById(R.id.editProgressBar)

        firstNameInputLayout = view.findViewById(R.id.layoutEditFirstName)
        lastNameInputLayout = view.findViewById(R.id.layoutEditLastName)

        firstNameEditText = view.findViewById(R.id.editTextFirstName)
        lastNameEditText = view.findViewById(R.id.editTextLastName)

        return view
    }
    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar.visibility = View.INVISIBLE

        defineImageSelectionCallBack()
        populateUserInformation()
        openGallery()
        updateUser()
    }

    private fun populateUserInformation() {
        UserModel.instance.getCurrentUser().observe(viewLifecycleOwner) {
            if (it != null) {
                firstNameEditText.setText(it.firstName)
                lastNameEditText.setText(it.lastName)
                if (it.profileImage != null && it.profileImage!!.isNotEmpty()) {
                    Picasso.get().load(it.profileImage)
                        .into(requireView().findViewById<ImageView>(R.id.profileEditImageView))
                }
            }
        }
    }

    private fun updateUser() {
        requireView().findViewById<Button>(R.id.EditButton).setOnClickListener {
            val user = auth.currentUser
            val newFirstName = firstNameEditText.text.toString().trim()
            val newLastName = lastNameEditText.text.toString().trim()

            val isValidUser =
                validateUser(newFirstName, newLastName,)

            if (isValidUser) {
                if (user != null) {
                    auth.updateCurrentUser(user).addOnSuccessListener {
                        progressBar.visibility = View.VISIBLE

                        val profileUpdatesBuilder = UserProfileChangeRequest.Builder()
                            .setDisplayName("$newFirstName $newLastName")

                        if (selectedImageURI != null) {
                            profileUpdatesBuilder.setPhotoUri(selectedImageURI)
                        }

                        val profileUpdates = profileUpdatesBuilder.build()
                        user.updateProfile(profileUpdates)
                        UserModel.instance.addUser(
                            User(user.uid, newFirstName, newLastName),
                            selectedImageURI
                        ) {
                            Toast.makeText(
                                requireContext(),
                                "Edit Successful",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Edit Failed, " + it.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun validateField(isValid: Boolean, layout: TextInputLayout, error: String): Boolean {
        if (!isValid) {
            layout.error = error;
            return false
        }
        layout.error = null
        return true
    }

    private fun validateUser(
        firstName: String,
        lastName: String,
    ): Boolean {
        val isFirstNameValid = validateField(
            firstName.isNotEmpty(),
            firstNameInputLayout,
            "First name cannot be empty"
        )

        val isLastNameValid = validateField(
            lastName.isNotEmpty(),
            lastNameInputLayout,
            "Last name cannot be empty")

        return isFirstNameValid && isLastNameValid
    }

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    private fun openGallery() {
        requireView().findViewById<Button>(R.id.btnEditPickImage).setOnClickListener {
            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            imageSelectionCallBack.launch(intent)
        }
    }

    @SuppressLint("Recycle")
    private fun getImageSize(uri: Uri?): Long {
        val inputStream = requireContext().contentResolver.openInputStream(uri!!)
        return inputStream?.available()?.toLong() ?: 0
    }

    private fun defineImageSelectionCallBack() {
        imageSelectionCallBack = registerForActivityResult(
            StartActivityForResult()
        ) { result: ActivityResult ->
            try {
                val imageUri: Uri? = result.data?.data
                if (imageUri != null) {
                    val imageSize = getImageSize(imageUri)
                    val maxCanvasSize = 5 * 1024 * 1024 // 5MB
                    if (imageSize > maxCanvasSize) {
                        Toast.makeText(
                            requireContext(),
                            "Selected image is too large",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        selectedImageURI = imageUri
                        requireView().findViewById<ImageView>(R.id.profileEditImageView).setImageURI(imageUri)
                    }
                } else {
                    Toast.makeText(requireContext(), "No Image Selected", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error processing result", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}
