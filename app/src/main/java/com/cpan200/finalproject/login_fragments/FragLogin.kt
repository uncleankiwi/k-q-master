package com.cpan200.finalproject.login_fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.cpan200.finalproject.R
import kotlinx.android.synthetic.main.activity_login.*

/**
 * A simple [Fragment] subclass.
 */
class FragLogin : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(com.cpan200.finalproject.R.layout.fragment_login, container, false)

        //test
        val transaction = activity!!.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.LoginContainer, FragOnboarding(), "FragOnboarding")
        transaction.addToBackStack("FragLogin").commit()

        val valBtnLoginRegister = view.findViewById<Button>(com.cpan200.finalproject.R.id.btnLogin_Register)
        valBtnLoginRegister.setOnClickListener {
            //            val fragment = activity!!.supportFragmentManager.findFragmentByTag("FragLogin")
//            if (fragment != null) {
//                Log.i("test123", "asdasdasd")
//                activity!!.supportFragmentManager.beginTransaction().remove(fragment).commit()
        //}
          //  activity!!.supportFragmentManager.popBackStack()



        }
        return view
    }




}
