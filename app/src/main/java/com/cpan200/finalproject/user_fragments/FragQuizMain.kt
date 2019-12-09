package com.cpan200.finalproject.user_fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cpan200.classes.App
import com.cpan200.classes.QuestionListAdapter
import com.cpan200.finalproject.R
import kotlinx.android.synthetic.main.fragment_quiz_main.*


/**
 * A simple [Fragment] subclass.
 */
class FragQuizMain : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_quiz_main, container, false)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

		val valRcyQuizMain = view.findViewById<RecyclerView>(R.id.rcyQuizMain)
		valRcyQuizMain.layoutManager = layoutManager

		val valBtnQuizMainAdd = view.findViewById<Button>(R.id.btnQuizMainAdd)
		if (App.questionListViewMode == QuestionListAdapter.ViewMode.EDIT)
			valBtnQuizMainAdd.isGone = false
		else if (App.questionListViewMode == QuestionListAdapter.ViewMode.DO)
			valBtnQuizMainAdd.isGone = true

        return view
    }


}
