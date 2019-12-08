package com.cpan200.finalproject.user_fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentManager
import com.cpan200.classes.App
import com.cpan200.finalproject.R

/**
 * A simple [Fragment] subclass.
 */
class FragAdminMain : Fragment() {

	override fun onCreateView(
			inflater: LayoutInflater, container: ViewGroup?,
			savedInstanceState: Bundle?
	): View? {
		val view = inflater.inflate(R.layout.fragment_admin_main, container, false)
		fragmentManager!!.beginTransaction()
				.replace(R.id.AdminSubContainer, FragQuizList(), "FragQuizList").commit()

		val valBtnAdminMainQuizzes = view.findViewById<Button>(R.id.btnAdminMainQuizzes)
		valBtnAdminMainQuizzes.setOnClickListener {
			fragmentManager!!.beginTransaction()
					.replace(R.id.AdminSubContainer, FragQuizList(), "FragQuizList").commit()
		}

		val valBtnAdminMainStudents = view.findViewById<Button>(R.id.btnAdminMainStudents)
		valBtnAdminMainStudents.setOnClickListener {
			fragmentManager!!.beginTransaction()
					.replace(R.id.AdminSubContainer, FragStudentList(), "FragStudentList").commit()
		}

				return view
	}


}
