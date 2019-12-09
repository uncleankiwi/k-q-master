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
import com.cpan200.classes.Question
import com.cpan200.classes.QuestionListAdapter
import com.cpan200.classes.Quiz
import com.cpan200.finalproject.R


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
					App.currentEditingQuiz = Quiz()
					App.currentEditingQuiz!!.questionList = mutableListOf()
					valEditQuizMainTitle.setText(App.currentQuiz?.title)
					valTxtQuizMainTitle.isGone = true
					valEditQuizMainTitle.isGone = false

					valBtnQuizMainAdd.isGone = false
					valBtnQuizMainSubmitSave.text = getString(R.string.Save)
					valBtnQuizMainSubmitSave.setOnClickListener {

						//save edits made to quiz and its questions
						var fail = false

						if (valEditQuizMainTitle.text.toString() == ""){
							App.showToast(context!!, "Please enter a quiz title.")
							fail = true
						} else if (App.currentEditingQuiz!!.questionList!!.count() == 0) {
							App.showToast(context!!, "Please create at least 1 question.")
							fail = true
						} else {
							for (question in App.currentEditingQuiz!!.questionList!!){

								//make sure every question has at least 1 answer
								if (question.answers != null){
									fail = true
									break
								} else if (question.answers!!.count() == 0){
									fail = true
									break
								} else if (question.correctAnswer == null){
									//make sure every question has 1 correct answer
									fail = true
									break
								} else if (question.correctAnswer!! >= question.answers!!.count()){
									//make sure that correct answer matches index of one of the answers
									fail = true
									break
								}
							}
						}

						if (!fail) {
							App.currentEditingQuiz!!.title = valEditQuizMainTitle.text.toString()
							App.editQuiz(context!!, App.currentQuiz!!.id!!, App.currentEditingQuiz!!)
						}

					}
				}
			}
		}

        return view
    }


}
