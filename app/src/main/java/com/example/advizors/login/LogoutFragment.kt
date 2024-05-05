import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.advizors.R
import com.example.advizors.models.user.UserModel
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

        UserModel.instance.logOff();

//        val i = Intent(getActivity(), LoginActivity::class.java)
//        startActivity(i)
//        (getActivity() as Activity?).overridePendingTransition(0, 0)

        return view
    }
}
