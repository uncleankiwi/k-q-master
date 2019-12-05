package com.cpan200.finalproject.login_fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.cpan200.finalproject.R

/**
 * A simple [Fragment] subclass.
 */
class FragOnboarding : Fragment() {

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val view = inflater.inflate(R.layout.fragment_onboarding, container, false)

		val valBtnRegCancel = view.findViewById<Button>(R.id.btnReg_Cancel)
		valBtnRegCancel.setOnClickListener {
			//show login fragment whether it exists already or not
			val transaction = fragmentManager!!.beginTransaction()
			var fragOnboarding: Fragment? = fragmentManager!!.findFragmentByTag("FragOnboarding")
			if (fragOnboarding == null) {
				fragOnboarding = FragOnboarding()
				Log.i("test123", "init frag onboarding")
			}

			transaction.replace(R.id.LoginContainer, FragLogin(), "FragLogin")
			transaction.addToBackStack("FragLogin")
			transaction.commit()
			Log.i("test123", fragmentManager!!.fragments.count().toString())
		}
		return view
	}


}
