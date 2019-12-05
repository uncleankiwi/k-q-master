package com.cpan200.finalproject.login_fragments


import android.os.Bundle
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
			activity?.onBackPressed()
		}
		return view
	}


}
