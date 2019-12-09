package com.cpan200.finalproject.user_fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import com.cpan200.classes.App
import com.cpan200.classes.QuizListAdapter
import com.cpan200.finalproject.R
import kotlinx.android.synthetic.main.fragment_quiz_list.*
import kotlinx.android.synthetic.main.fragment_quiz_main.*

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
        rcyQuizList.layoutManager = layoutManager
        rcyQuizList.adapter = QuizListAdapter(context!!, App.getQuizList(context!!), App.quizListViewMode)

		if (App.quizListViewMode == QuizListAdapter.ViewMode.ADMIN)
			btnQuizMainAdd.isGone = false
		else if (App.questionListViewMode == QuizListAdapter.ViewMode.STUDENT)
			btnQuizMainAdd.isGone = true

        return view
    }


}
