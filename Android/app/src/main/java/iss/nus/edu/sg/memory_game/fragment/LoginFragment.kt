package iss.nus.edu.sg.memory_game.fragment

import android.content.Context
import android.content.SharedPreferences
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
import iss.nus.edu.sg.memory_game.apis.RetrofitClient
import iss.nus.edu.sg.memory_game.dao.LoginRequest
import iss.nus.edu.sg.memory_game.dao.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {
    private var loginPrefs: SharedPreferences? = null

    companion object {
        fun newInstance() = LoginFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // login check here to immediately redirect to fetch fragment once checked that user is not logged in
        loginPrefs = requireActivity().getSharedPreferences("auth", Context.MODE_PRIVATE)
        val isLoggedIn = loginPrefs?.getBoolean("isLoggedIn", false)
        if (isLoggedIn == true) {
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
        loginPrefs = requireActivity().getSharedPreferences("auth", Context.MODE_PRIVATE)

        loginButton?.setOnClickListener {
            //setting login values into my loginrequest DTO
            val loginRequest = LoginRequest(username.text.toString(), password.text.toString())
            retrofitAuthenticate(loginRequest)
        }
    }

    private fun retrofitAuthenticate(loginRequest: LoginRequest) {
        RetrofitClient.instance.login(loginRequest)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse?>,
                    response: Response<LoginResponse?>
                ) {
                    //if response returned is a 200 OK
                    if (response.isSuccessful) {
                        val user = response.body()
                        if (user != null) {
                            // getSharedPreferences key = "auth", boolean flag isLoggedIn to true if user credentials are valid with existing user
                            with(loginPrefs!!.edit()) {
                                putBoolean("isLoggedIn", true)
                                putBoolean("isPaidUser", user.isPaidUser)

                                // storing username as well, if user is required as unique id in later fragments
                                // to call for APIs
                                putString("Username", user.username)
                                putString("UserID", user.id)
                                apply()
                            }
                        }
                        view!!.findNavController().navigate(R.id.action_login_to_fetch)
                    } else if (response.code() == 401) {
                        //  check for 401 Unauthorized
                        Toast.makeText(requireContext(), "Invalid credentials", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        //  other response errors
                        Toast.makeText(
                            requireContext(),
                            "Login failed: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    t.printStackTrace()
                    var message = when {
                        t.message?.contains("Unable to resolve host") == true -> {
                            "No internet connection"
                        }

                        t.message?.contains("timeout") == true -> {
                            "Request timeout"
                        }

                        else -> {
                            "Network: Unknown error"
                        }

                    }
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }

            })
            }
}
