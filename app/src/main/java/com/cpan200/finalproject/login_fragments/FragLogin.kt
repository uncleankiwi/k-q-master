package com.cpan200.finalproject.login_fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.cpan200.finalproject.R



/**
 * A simple [Fragment] subclass.
 */
class FragLogin : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val valBtnLoginRegister = view.findViewById<Button>(R.id.btnLogin_Register)
        valBtnLoginRegister?.setOnClickListener {
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.LoginContainer, FragOnboarding())
            transaction?.addToBackStack("FragLogin")
            transaction?.commit()
        }
        return view
    }


}
