package com.cpan200.classes

import android.content.Context
import android.graphics.BitmapFactory
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.cpan200.finalproject.R
import kotlinx.android.synthetic.main.panel_question.view.*

class QuestionListAdapter(
		val context: Context,
		private var quiz: Quiz
) : RecyclerView.Adapter<QuestionListAdapter.QuestionPanelViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionPanelViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.panel_question, parent, false)
        return QuestionPanelViewHolder(view)
    }

    override fun getItemCount(): Int {
        return quiz.questionList.count()
    }

    override fun onBindViewHolder(holder: QuestionPanelViewHolder, position: Int) {
        quiz.questionList = quiz.questionList
        val question = quiz.questionList[position]
        holder.setData(question, position) //viewMode
    }

	fun refreshData(){
		//this gets currentQuiz global variable, NOT from db!
		quiz = App.currentQuiz
		notifyDataSetChanged()
	}

    inner class QuestionPanelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		private var currentQuestion: Question? = null
		var currentPosition: Int = 0
		private var ansPopulated: Boolean = false
		fun setData(question: Question, pos: Int){
			//viewMode: ViewMode
			this.currentQuestion = question
			this.currentPosition = pos

			val isAdmin = App.currentUser!!.status == User.UserStatus.SUPERUSER || App.currentUser!!.status == User.UserStatus.ADMIN
			val isEditMode = isAdmin && !quiz.finalized
			itemView.txtQuestionPanelQuestion.text = this.currentQuestion!!.question

			//hiding it since it's a static 5 options for now
			itemView.btnQuestionPanelAddAns.isGone = true

			//if currentQuiz image is not null, display image
			if (App.currentQuiz.questionList[currentPosition].image != null){
				val imageArray = App.currentQuiz.questionList[currentPosition].image!!
				val imageBmp = BitmapFactory.decodeByteArray(App.currentQuiz.questionList[currentPosition].image, 0, imageArray.size)
				itemView.imgQuestionPanel.setImageBitmap(imageBmp)
			}
			else {
				//clear imageView image
				itemView.imgQuestionPanel.setImageDrawable(null)
			}

			if (!ansPopulated){
				if (isEditMode){
					//edit mode
					//populate question title
					itemView.editQuestionPanelQuestion.setText(this.currentQuestion!!.question)
					itemView.txtQuestionPanelQuestion.isGone = true
					itemView.editQuestionPanelQuestion.isGone = false

					//question listener
					itemView.editQuestionPanelQuestion.addTextChangedListener(object : TextWatcher {
						override fun afterTextChanged(p0: Editable?) {}
						override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
						override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
							App.currentQuiz.questionList[currentPosition].question = p0.toString()
						}
					})

					//fill options with answer options
					for (i in 0 until (quiz.maxOptions)){
						//create maxOptions number of options
						val radAns = RadioEx(context)
						radAns.text = ""
						radAns.layoutParams = LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.WRAP_CONTENT,
								LinearLayout.LayoutParams.WRAP_CONTENT).also { it.setMargins(0, 23, 0, 20) }
						radAns.answerID = i //holds the index since RadioGroup's index consists of lies and empty promises

						//set delete button listener
						itemView.btnQuestionPanelDelete.setOnClickListener {
							App.currentQuiz.questionList.removeAt(currentPosition)
							refreshData()
						}

						//set radioEx listener
						radAns.setOnCheckedChangeListener { _, isChecked ->
							if (isChecked){
								App.currentQuiz.questionList[currentPosition].correctAnswer = radAns.answerID
							}
						}

						//check correct answer
						itemView.radGrpQuestionPanelAns.addView(radAns, i)
						if (i == this.currentQuestion!!.correctAnswer) radAns.isChecked = true

						//create editTexts and fill them in if the answer exists
						val editAns = EditText(context)
						editAns.hint = context.getString(R.string.spacer)
						//also create a listener to save new answers
						editAns.addTextChangedListener(object : TextWatcher {
							override fun afterTextChanged(p0: Editable?) {}
							override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
							override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
								if (i < App.currentQuiz.questionList[currentPosition].answers.count()){
									App.currentQuiz.questionList[currentPosition].answers[i] = p0.toString()
								}
							}
						})

						if (this.currentQuestion != null){
							if (i < this.currentQuestion!!.answers.count())
								editAns.setText(this.currentQuestion!!.answers[i])
						}
						itemView.QuestionPanelAnsContainer.addView(editAns, i)
					}

					//btnQuestionPanelSetImage listener
					itemView.btnQuestionPanelSetImage.setOnClickListener {
						//add image to currentQuiz then refresh
						App.openImageUriAndSave(context, currentPosition, this@QuestionListAdapter)		//todo
					}

					//imgQuestionPanel listener - removes image from currentQuiz if there's one
					itemView.imgQuestionPanel.setOnClickListener {
						//remove image from currentQuiz question, then refresh
						App.currentQuiz.questionList[currentPosition].image = null
						refreshData()
					}


				}
				else {
					//do mode
					//populate question title
					itemView.txtQuestionPanelQuestion.text = this.currentQuestion!!.question
					itemView.txtQuestionPanelQuestion.isGone = false
					itemView.editQuestionPanelQuestion.isGone = true

					//create options only if they aren't null/blank. fill options
					for (i in 0 until (quiz.maxOptions)){
						var currAns: String? = null
						if (this.currentQuestion != null){
							currAns = this.currentQuestion!!.answers[i]
						}
						if (currAns != null && currAns != ""){
							val radAns = RadioEx(context)
							radAns.text = currAns
							radAns.answerID = i

							itemView.radGrpQuestionPanelAns.addView(radAns)

							//also checking them if they're in the current currentQuizAttempt
							//so that answer selections survive rotation
							if (App.currentQuizAttempt[currentPosition] >= 0){
								if (i == App.currentQuizAttempt[currentPosition]) radAns.isChecked = true
							}
						}
					}

					itemView.radGrpQuestionPanelAns.setOnCheckedChangeListener { _, radioID ->
						val checkedRadio = (context as AppCompatActivity).findViewById<RadioEx>(radioID)
						if (checkedRadio.isChecked) {
							if (checkedRadio.answerID == null){
								//user didn't answer this question
							}
							else {
								App.currentQuizAttempt[currentPosition] = checkedRadio.answerID!!
							}
						}

					}

				}
				ansPopulated = true
			}

			if (quiz.finalized){
				//do mode
				itemView.txtQuestionPanelQuestion.text = currentQuestion!!.question
				itemView.txtQuestionPanelQuestion.isGone = false
				itemView.editQuestionPanelQuestion.isGone = true
				itemView.llQuestionPanelEditControls.isGone = true
				//itemView.imgQuestionPanel.isGone = this.currentQuestion!!.image == null
			} else {
				//edit mode
				itemView.editQuestionPanelQuestion.setText(currentQuestion!!.question)
				itemView.txtQuestionPanelQuestion.isGone = true
				itemView.editQuestionPanelQuestion.isGone = false
				itemView.llQuestionPanelEditControls.isGone = false
			}

		}
    }
}