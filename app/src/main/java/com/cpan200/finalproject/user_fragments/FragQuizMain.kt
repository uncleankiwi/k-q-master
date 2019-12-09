package com.cpan200.finalproject.user_fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
		valRcyQuizMain.adapter = QuestionListAdapter(context!!, App.currentQuiz!!)



		val valBtnQuizMainClose = view.findViewById<Button>(R.id.btnQuizMainClose)
		valBtnQuizMainClose.setOnClickListener { (context as AppCompatActivity).onBackPressed() }

		val valBtnQuizMainSubmitSave = view.findViewById<Button>(R.id.btnQuizMainSubmitSave)


		val valBtnQuizMainAdd = view.findViewById<Button>(R.id.btnQuizMainAdd)
		//could remove this later, since layout changes depend on quiz.finalized, not questionListViewMode
//		if (App.questionListViewMode == QuestionListAdapter.ViewMode.EDIT)
//			valBtnQuizMainAdd.isGone = false
//		else if (App.questionListViewMode == QuestionListAdapter.ViewMode.DO)
//			valBtnQuizMainAdd.isGone = true
		valBtnQuizMainAdd.setOnClickListener {
			//adds a blank question to current quiz
			//adds it to App.currentQuiz, not to QuizDB!
			//to add to quiz DB, use submit button
			App.addBlankQuestion()
			(valRcyQuizMain.adapter as QuestionListAdapter).refreshData()

		}

		val valTxtQuizMainTitle = view.findViewById<TextView>(R.id.txtQuizMainTitle)
		val valEditQuizMainTitle = view.findViewById<EditText>(R.id.editQuizMainTitle)

		//changing layouts depending on whether quiz opened is finalized or not
		if (App.currentQuiz != null){
			when (App.currentQuiz!!.finalized){
				true ->{
					//do mode
					valTxtQuizMainTitle.text = App.currentQuiz?.title
					valTxtQuizMainTitle.isGone = false
					valEditQuizMainTitle.isGone = true

					valBtnQuizMainAdd.isGone = true
					valBtnQuizMainSubmitSave.text = getString(R.string.Submit)
					valBtnQuizMainSubmitSave.setOnClickListener {
						//submit quiz, calc score
						//todo submit quiz, calc score
					}
				}
				false ->{
					//edit mode
					valEditQuizMainTitle.setText(App.currentQuiz?.title)
					valTxtQuizMainTitle.isGone = true
					valEditQuizMainTitle.isGone = false

					valBtnQuizMainAdd.isGone = false
					valBtnQuizMainSubmitSave.text = getString(R.string.Save)
					valBtnQuizMainSubmitSave.setOnClickListener {
						//save edits made to quiz and its questions
						//todo save edits made to quiz and its questions
					}
				}
			}
		}

        return view
    }


}
