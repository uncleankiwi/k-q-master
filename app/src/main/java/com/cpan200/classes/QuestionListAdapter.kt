package com.cpan200.classes

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.cpan200.finalproject.R
import kotlinx.android.synthetic.main.panel_question.view.*

class QuestionListAdapter(
		val context: Context,
		private var quiz: Quiz
		//private val viewMode: ViewMode
) : RecyclerView.Adapter<QuestionListAdapter.QuestionPanelViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionPanelViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.panel_question, parent, false)
        return QuestionPanelViewHolder(view)
    }

    override fun getItemCount(): Int {
        return quiz.questionList?.count() ?: 0
    }

    override fun onBindViewHolder(holder: QuestionPanelViewHolder, position: Int) {
        quiz.questionList = quiz.questionList ?: mutableListOf()
        val question = quiz.questionList!![position]
        holder.setData(question, position) //viewMode
    }

	fun refreshData(){
		//this gets currentQuiz global variable, NOT from db!
		quiz = App.currentQuiz!!
		notifyDataSetChanged()
	}

    inner class QuestionPanelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		private var currentQuestion: Question? = null
		var currentPosition: Int = 0
		var ansPopulated: Boolean = false
		fun setData(question: Question, pos: Int){
			//viewMode: ViewMode
			this.currentQuestion = question
			this.currentPosition = pos

			val isAdmin = App.currentUser!!.status == User.UserStatus.SUPERUSER || App.currentUser!!.status == User.UserStatus.ADMIN
			val isEditMode = isAdmin && !quiz.finalized!!
			itemView.txtQuestionPanelQuestion.text = this.currentQuestion!!.question

			//hiding it since it's a static 5 options for now
			itemView.btnQuestionPanelAddAns.isGone = true

			if (!ansPopulated){
				if (isEditMode){
					//edit mode
					//populate question title
					itemView.editQuestionPanelQuestion.setText(this.currentQuestion!!.question)
					itemView.txtQuestionPanelQuestion.isGone = true
					itemView.editQuestionPanelQuestion.isGone = false

					//fill options with answer options
					for (i in 0 until (quiz.maxOptions ?: 0)){
						//create maxOptions number of options
						val radAns = RadioButton(context)
						radAns.text = ""
						radAns.layoutParams = LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.WRAP_CONTENT,
								LinearLayout.LayoutParams.WRAP_CONTENT).also { it.setMargins(0, 23, 0, 20) }

						//check correct answer
						if (i == this.currentQuestion!!.correctAnswer) radAns.isChecked = true
						itemView.radGrpQuestionPanelAns.addView(radAns, i)

						//create editTexts and fill them in if the answer exists
						val editAns = EditText(context)
						editAns.hint = context.getString(R.string.spacer)
						//also create a listener to save new answers
						editAns.addTextChangedListener(object : TextWatcher {
							override fun afterTextChanged(p0: Editable?) {}
							override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
							override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
								App.currentEditingQuiz!!.questionList!![currentPosition].answers!![i] = p0.toString()
							}
						})

						if (this.currentQuestion != null){
							if (this.currentQuestion!!.answers != null){
								editAns.setText(this.currentQuestion!!.answers!![i])
							}
						}
						//editAns.layoutParams = LinearLayout.LayoutParams(
//								LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

						itemView.QuestionPanelAnsContainer.addView(editAns, i)
					}

					//radio button listener
					itemView.radGrpQuestionPanelAns.setOnCheckedChangeListener { _, i ->
						App.currentEditingQuiz!!.questionList!![currentPosition].correctAnswer = i
					}

				}
				else {
					//do mode
					//populate question title
					itemView.txtQuestionPanelQuestion.text = this.currentQuestion!!.question
					itemView.txtQuestionPanelQuestion.isGone = false
					itemView.editQuestionPanelQuestion.isGone = true

					//create options only if they aren't null/blank. fill options
					for (i in 0 until (quiz.maxOptions ?: 0)){
						var currAns: String? = null
						if (this.currentQuestion != null){
							if (this.currentQuestion!!.answers != null){
								currAns = this.currentQuestion!!.answers!![i]
							}
						}
						if (currAns != null && currAns != ""){
							val radAns = RadioButton(context)
							radAns.text = currAns
							itemView.radGrpQuestionPanelAns.addView(radAns, i)
						}

					}

					//radio button listener
					itemView.radGrpQuestionPanelAns.setOnCheckedChangeListener { _, i ->
						App.currentQuizAttempt[currentPosition] = i
					}

				}
				ansPopulated = true
			}

			if (quiz.finalized!!){
				//do mode
				itemView.txtQuestionPanelQuestion.text = currentQuestion!!.question
				itemView.txtQuestionPanelQuestion.isGone = false
				itemView.editQuestionPanelQuestion.isGone = true
				itemView.llQuestionPanelEditControls.isGone = true
				itemView.imgQuestionPanel.isGone = this.currentQuestion!!.image == null
			} else {
				//edit mode
				itemView.editQuestionPanelQuestion.setText(currentQuestion!!.question)
				itemView.txtQuestionPanelQuestion.isGone = true
				itemView.editQuestionPanelQuestion.isGone = false
				itemView.llQuestionPanelEditControls.isGone = false
			}

		}
    }

    enum class ViewMode {
        EDIT,
        DO
    }
}