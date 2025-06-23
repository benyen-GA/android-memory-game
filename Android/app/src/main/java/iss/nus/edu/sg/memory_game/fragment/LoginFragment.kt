package iss.nus.edu.sg.memory_game.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import iss.nus.edu.sg.memory_game.R
import iss.nus.edu.sg.memory_game.dao.TempUserDAO

class LoginFragment : Fragment() {
    private var isPaidUser: Boolean? = null


    companion object {
        fun newInstance() = LoginFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    //login check here to immediately redirect to fetch fragment once checked that user is not logged in
        val loginPrefs = requireActivity().getSharedPreferences("auth", Context.MODE_PRIVATE)
        val isLoggedIn = loginPrefs.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            NavHostFragment.findNavController(this).navigate(R.id.action_login_to_fetch)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
                return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginButton = view.findViewById<Button>(R.id.loginButton)
        val username = view.findViewById<EditText>(R.id.username)
        val password = view.findViewById<EditText>(R.id.password)
        val loginPrefs = requireActivity().getSharedPreferences("auth", Context.MODE_PRIVATE)


        loginButton?.setOnClickListener {
            val user = TempUserDAO.authenticate(username.text.toString(), password.text.toString())
            if (user != null) {
                // getSharedPreferences key = "auth", boolean flag isLoggedIn to true if user credentials are valid with existing user
                with (loginPrefs.edit()) {
                    putBoolean("isLoggedIn", true)
                    putBoolean("isPaidUser", user.isPaidUser)

                    //storing username as well, if user is required as unique id in later fragments
                    //to call for APIs
                    putString("Username", user.username)
                    apply()
                }
                view.findNavController().navigate(R.id.action_login_to_fetch)

            } else {
                Toast.makeText(context, "Invalid login credentials", Toast.LENGTH_SHORT).show()
            }


        }
    }
}
