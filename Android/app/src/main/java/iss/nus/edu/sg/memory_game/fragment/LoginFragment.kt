package iss.nus.edu.sg.memory_game.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import iss.nus.edu.sg.memory_game.R

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel (can be initialized here later if needed)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
                return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Lewis- Magic Code for TEST button to navigate to the Fetch Fragment Screen.
        val testButton = view.findViewById<Button>(R.id.testButton)
        testButton?.setOnClickListener {
            // Lewis - go to FetchFragment when TEST is clicked
            view.findNavController().navigate(R.id.action_login_to_fetch)
        }
    }
}
