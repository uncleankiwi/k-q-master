package com.cpan200.finalproject.user_fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
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

		//try to recall what fragment was in AdminSubContainer
		when (App.fragAdminMainViewMode){
			ViewMode.Quizzes ->{
				fragmentManager!!.beginTransaction()
					.replace(R.id.AdminSubContainer, FragQuizList(), "FragQuizList").commit()
			}
			ViewMode.Students ->{
				fragmentManager!!.beginTransaction()
					.replace(R.id.AdminSubContainer, FragStudentList(), "FragStudentList").commit()
			}
		}

		val valBtnAdminMainQuizzes = view.findViewById<Button>(R.id.btnAdminMainQuizzes)
		valBtnAdminMainQuizzes.setOnClickListener {
			App.fragAdminMainViewMode = ViewMode.Quizzes
			fragmentManager!!.beginTransaction()
					.replace(R.id.AdminSubContainer, FragQuizList(), "FragQuizList").commit()
		}

		val valBtnAdminMainStudents = view.findViewById<Button>(R.id.btnAdminMainStudents)
		valBtnAdminMainStudents.setOnClickListener {
			App.fragAdminMainViewMode = ViewMode.Students
			fragmentManager!!.beginTransaction()
					.replace(R.id.AdminSubContainer, FragStudentList(), "FragStudentList").commit()
		}

		return view
	}

	enum class ViewMode {
		Quizzes,
		Students
	}


}
