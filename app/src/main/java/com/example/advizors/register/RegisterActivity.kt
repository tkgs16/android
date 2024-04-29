package com.example.advizors.register

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.RequiresExtension
import androidx.appcompat.app.AppCompatActivity
import com.example.advizors.MainActivity
import com.example.advizors.R
import com.example.advizors.login.LoginActivity
import com.example.advizors.models.user.User
import com.example.advizors.models.user.UserModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth

class RegisterActivity : AppCompatActivity() {

    private lateinit var imageSelectionCallBack: ActivityResultLauncher<Intent>
    private var selectedImageURI: Uri? = null
    private lateinit var firstNameInputLayout: TextInputLayout
    private lateinit var firstNameEditText: TextInputEditText
    private lateinit var lastNameInputLayout: TextInputLayout
    private lateinit var lastNameEditText: TextInputEditText
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordInputLayout: TextInputLayout
    private lateinit var confirmPasswordEditText: TextInputEditText
    private val auth = Firebase.auth

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        defineImageSelectionCallBack()
        openGallery()
        startLoginActivity()
        createNewUser()
    }

    private fun createNewUser() {
        firstNameInputLayout = findViewById(R.id.layoutFirstName)
        firstNameEditText = findViewById(R.id.editTextFirstName)
        lastNameInputLayout = findViewById(R.id.layoutLastName)
        lastNameEditText = findViewById(R.id.editTextLastName)
        emailInputLayout = findViewById(R.id.layoutEmailAddress)
        emailEditText = findViewById(R.id.editTextEmailAddress)
        passwordInputLayout = findViewById(R.id.layoutPassword)
        passwordEditText = findViewById(R.id.editTextPassword)
        confirmPasswordInputLayout = findViewById(R.id.layoutConfirmPassword)
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPassword)

        findViewById<Button>(R.id.SignUpButton).setOnClickListener {
            val firstName = firstNameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            val isValidUser =
                validateUser(firstName, lastName, email, password, confirmPassword)

            if (isValidUser) {
                auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                    val authenticatedUser = it.user!!

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setPhotoUri(selectedImageURI)
                        .setDisplayName("$firstName $lastName")
                        .build()

                    authenticatedUser.updateProfile(profileUpdates)
                    UserModel.instance.addUser(
                        User(authenticatedUser.uid, firstName, lastName),
                        selectedImageURI!!
                    ) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Register Successful",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        //TODO refactor main activity
                        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }.addOnFailureListener {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Register Failed, " + it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun startLoginActivity() {
        findViewById<TextView>(R.id.LogInTextView).setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
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
        email: String,
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
        val isEmailNotEmpty =
            validateField(email.isNotEmpty(), emailInputLayout, "Email cannot be empty")
        val isEmailValid = if (isEmailNotEmpty) validateField(
            Patterns.EMAIL_ADDRESS.matcher(email).matches(),
            emailInputLayout,
            "Invalid email format"
        ) else false

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

        val isConfirmPasswordNotEmpty = validateField(confirmPassword.isNotEmpty(), confirmPasswordInputLayout, "Confirm password cannot be empty")

        val isPasswordsMatch = if(isConfirmPasswordNotEmpty) validateField(password == confirmPassword, confirmPasswordInputLayout, "Passwords don't match") else false

        if (selectedImageURI == null) {
            Toast.makeText(
                this@RegisterActivity,
                "You must select Profile Image",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return isFirstNameValid && isLastNameValid && isEmailValid && isPasswordValid && isPasswordsMatch
    }

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    private fun openGallery() {
        findViewById<Button>(R.id.btnPickImage).setOnClickListener {
            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            imageSelectionCallBack.launch(intent)
        }
    }

    @SuppressLint("Recycle")
    private fun getImageSize(uri: Uri?): Long {
        val inputStream = contentResolver.openInputStream(uri!!)
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
                            this@RegisterActivity,
                            "Selected image is too large",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        selectedImageURI = imageUri
                        findViewById<ImageView>(R.id.profileImageView).setImageURI(imageUri)
                    }
                } else {
                    Toast.makeText(this@RegisterActivity, "No Image Selected", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Error processing result", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}