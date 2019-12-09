package com.cpan200.classes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cpan200.finalproject.R
import kotlinx.android.synthetic.main.panel_question.view.*

class QuestionListAdapter(
		val context: Context,
		private val quiz: Quiz,
		val viewMode: ViewMode
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
        holder.setData(question, position, viewMode)
    }

    inner class QuestionPanelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var currentQuestion: Question? = null
		var currentPosition: Int = 0
		fun setData(question: Question, pos: Int, viewMode: ViewMode){
			this.currentQuestion = question
			this.currentPosition = pos

			itemView.txtQuestionPanelQuestion.text = this.currentQuestion!!.question


		}
    }

    enum class ViewMode {
        EDIT,
        DO
    }
}