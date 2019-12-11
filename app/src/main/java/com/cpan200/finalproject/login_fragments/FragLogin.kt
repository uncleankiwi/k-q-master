package com.cpan200.finalproject.login_fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.cpan200.classes.App
import com.cpan200.finalproject.R
import kotlinx.android.synthetic.main.fragment_login.*

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
		valBtnLoginRegister.setOnClickListener {

			//show onboarding fragment whether it exists already or not
			val transaction = fragmentManager!!.beginTransaction()
			var fragOnboarding: Fragment? = fragmentManager!!.findFragmentByTag("FragOnboarding")
			if (fragOnboarding == null) {
				fragOnboarding = FragOnboarding()
			}
			transaction.replace(R.id.LoginContainer, fragOnboarding, "FragOnboarding")
			transaction.addToBackStack(null)
			transaction.commit()

		}

		val valBtnLoginLogin = view.findViewById<Button>(R.id.btnLogin_Login)
		valBtnLoginLogin.setOnClickListener {
			App.login(activity!!, editLogin_Username.text.toString(), editLogin_Password.text.toString())
		}

		return view
	}




}
