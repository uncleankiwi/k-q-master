package com.cpan200.finalproject.user_fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.cpan200.classes.App
import com.cpan200.classes.User
import com.cpan200.finalproject.R

/**
 * A simple [Fragment] subclass.
 */
class FragParticularsMain : Fragment() {

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		val view = inflater.inflate(R.layout.fragment_particulars_main, container, false)

		if (App.currentEditUser != null){
			val valTxtEditParticularsUsername = view.findViewById<TextView>(R.id.txtEditParticularsUsername)
			val valEditEditParticularsPassword = view.findViewById<EditText>(R.id.editEditParticularsPassword)
			val valRadEditParticularsSuperuser = view.findViewById<RadioButton>(R.id.radEditParticularsSuperuser)
			val valRadEditParticularsAdmin = view.findViewById<RadioButton>(R.id.radEditParticularsAdmin)
			val valRadEditParticularsStudent = view.findViewById<RadioButton>(R.id.radEditParticularsStudent)
			val valEditEditParticularsFirstname = view.findViewById<EditText>(R.id.editEditParticularsFirstname)
			val valEditEditParticularsLastname = view.findViewById<EditText>(R.id.editEditParticularsLastname)
			val valEditEditParticularsEmail = view.findViewById<EditText>(R.id.editEditParticularsEmail)

			//populate with currentEditUser's particulars
			valTxtEditParticularsUsername.text = App.currentEditUser!!.name
			valEditEditParticularsPassword.setText(App.currentEditUser!!.password)
			when (App.currentEditUser!!.status){
				User.UserStatus.SUPERUSER -> {
					valRadEditParticularsSuperuser.isChecked = true
					val valRadgrpEditParticularsStatus =view.findViewById<RadioGroup>(R.id.radgrpEditParticularsStatus)
					valRadgrpEditParticularsStatus.isEnabled = false
				}
				User.UserStatus.ADMIN -> valRadEditParticularsAdmin.isChecked = true
				User.UserStatus.STUDENT -> valRadEditParticularsStudent.isChecked = true
			}
			valRadEditParticularsSuperuser.isEnabled = false
			valEditEditParticularsFirstname.setText(App.currentEditUser!!.firstName)
			valEditEditParticularsLastname.setText(App.currentEditUser!!.lastName)
			valEditEditParticularsEmail.setText(App.currentEditUser!!.email)

			val valBtnEditParticularsSave = view.findViewById<Button>(R.id.btnEditParticularsSave)
			//save button listener
			valBtnEditParticularsSave.setOnClickListener {
				App.changeParticulars(context!!,
					App.currentEditUser!!.name!!,
					valEditEditParticularsPassword.text.toString(),
					valEditEditParticularsEmail.text.toString(),
					valEditEditParticularsFirstname.text.toString(),
					valEditEditParticularsLastname.text.toString())
			}
		}
		//cancel button listener
		val valBtnEditParticularsCancel = view.findViewById<Button>(R.id.btnEditParticularsCancel)
		valBtnEditParticularsCancel.setOnClickListener {
			(context as AppCompatActivity).onBackPressed()
		}

		return view
	}


}
