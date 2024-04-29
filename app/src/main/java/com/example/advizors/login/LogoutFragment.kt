import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.advizors.R
import com.google.firebase.auth.FirebaseAuth


class LogoutFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?  {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_logout, container, false)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        view.setOnClickListener {
            signOut()
        }

        return view
    }

    private fun signOut() {
        auth.signOut()
        // Redirect the user to the login screen or any other appropriate action
        // For example, you can navigate to another fragment or activity
    }
}
