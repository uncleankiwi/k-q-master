package com.cpan200.finalproject.login_fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.cpan200.classes.App
import com.cpan200.finalproject.R
import kotlinx.android.synthetic.main.fragment_onboarding.*

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
			activity?.onBackPressed()
		}

		val valBtnRegRegister = view.findViewById<Button>(R.id.btnReg_Register)
		valBtnRegRegister.setOnClickListener {
			App.createUser(activity!!, editReg_Username.text.toString(), editReg_Password.text.toString(),
				editReg_Email.text.toString(),
				editReg_Firstname.text.toString(),
				editReg_Lastname.text.toString())





		}
		return view
	}


}
