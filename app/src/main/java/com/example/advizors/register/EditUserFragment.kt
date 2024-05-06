package com.example.advizors.register

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
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresExtension
import androidx.fragment.app.Fragment
import com.example.advizors.MainActivity
import com.example.advizors.R
import com.example.advizors.models.user.User
import com.example.advizors.models.user.UserModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class EditUserFragment : Fragment() {

    private val auth = Firebase.auth
    private lateinit var imageSelectionCallBack: ActivityResultLauncher<Intent>
    private var selectedImageURI: Uri? = null
    private lateinit var firstNameInputLayout: TextInputLayout
    private lateinit var firstNameEditText: TextInputEditText
    private lateinit var lastNameInputLayout: TextInputLayout
    private lateinit var lastNameEditText: TextInputEditText
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordInputLayout: TextInputLayout
    private lateinit var confirmPasswordEditText: TextInputEditText

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_user, container, false)

        firstNameInputLayout = view.findViewById(R.id.layoutEditFirstName)
        lastNameInputLayout = view.findViewById(R.id.layoutEditLastName)
        passwordInputLayout = view.findViewById(R.id.layoutEditPassword)
        confirmPasswordInputLayout = view.findViewById(R.id.layoutEditConfirmPassword)

        firstNameEditText = view.findViewById(R.id.editTextFirstName)
        lastNameEditText = view.findViewById(R.id.editTextLastName)
        passwordEditText = view.findViewById(R.id.editTextPassword)
        confirmPasswordEditText = view.findViewById(R.id.editTextConfirmPassword)

        populateUserInformation()
        defineImageSelectionCallBack()
        openGallery()
        updateUser()

        return view
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
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            val isValidUser =
                validateUser(newFirstName, newLastName, password, confirmPassword)

            if (isValidUser) {
                if (user != null) {
                    auth.updateCurrentUser(user).addOnSuccessListener {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setPhotoUri(selectedImageURI)
                            .setDisplayName("$newFirstName $newLastName")
                            .build()

                        user.updateProfile(profileUpdates)
                        UserModel.instance.addUser(
                            User(user.uid, newFirstName, newLastName),
                            selectedImageURI!!
                        ) {
                            Toast.makeText(
                                requireContext(),
                                "Register Successful",
                                Toast.LENGTH_SHORT
                            ).show()
                            //TODO refactor main activity
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Register Failed, " + it.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun validateField(isValid: Boolean, layout: TextInputLayout, error: String): Boolean {
        if (!isValid) {
            layout.error = error
            return false
        }
        layout.error = null
        return true
    }

    private fun validateUser(
        firstName: String,
        lastName: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        val isFirstNameValid = validateField(
            firstName.isNotEmpty(),
            firstNameInputLayout,
            "First name cannot be empty"
        )

        val isLastNameValid =
            validateField(lastName.isNotEmpty(), lastNameInputLayout, "Last name cannot be empty")
        val isPasswordNotEmpty =
            validateField(password.isNotEmpty(), passwordInputLayout, "Password cannot be empty")
        val isPasswordNotShort = if (isPasswordNotEmpty) validateField(
            password.length >= 6,
            passwordInputLayout,
            "Password must be at least 6 characters"
        ) else false
        val isPasswordValid = if (isPasswordNotShort) validateField(
            password.any { it.isDigit() },
            passwordInputLayout,
            "Password must contain at least one digit"
        ) else false

        val isConfirmPasswordNotEmpty = validateField(
            confirmPassword.isNotEmpty(),
            confirmPasswordInputLayout,
            "Confirm password cannot be empty"
        )

        val isPasswordsMatch = if (isConfirmPasswordNotEmpty) validateField(
            password == confirmPassword,
            confirmPasswordInputLayout,
            "Passwords don't match"
        ) else false

        if (selectedImageURI == null) {
            Toast.makeText(
                requireContext(),
                "You must select Profile Image",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return isFirstNameValid && isLastNameValid && isPasswordValid && isPasswordsMatch
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
            ActivityResultContracts.StartActivityForResult()
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
