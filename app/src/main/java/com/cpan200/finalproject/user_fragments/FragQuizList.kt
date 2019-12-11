package com.cpan200.finalproject.user_fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cpan200.classes.App
import com.cpan200.classes.QuizListAdapter
import com.cpan200.finalproject.R

/**
 * A simple [Fragment] subclass.
 */
class FragQuizList : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_quiz_list, container, false)

        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
		val valRcyQuizList = view.findViewById<RecyclerView>(R.id.rcyQuizList)
		valRcyQuizList.layoutManager = layoutManager

		val isAdmin = (App.quizListViewMode == QuizListAdapter.ViewMode.ADMIN)
		val quizzes = if (!isAdmin)
			App.getFinalizedQuizList(context!!)
		else
			App.getQuizList(context!!)

		valRcyQuizList.adapter = QuizListAdapter(context!!, quizzes)

		val valBtnQuizListAdd = view.findViewById<Button>(R.id.btnQuizListAdd)
		valBtnQuizListAdd.isGone = !isAdmin

		valBtnQuizListAdd.setOnClickListener {
			App.addQuiz(context!!)
			(valRcyQuizList.adapter as QuizListAdapter).refreshData()
		}

        return view
    }


}
