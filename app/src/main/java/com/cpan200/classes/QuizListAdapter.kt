package com.cpan200.classes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cpan200.classes.QuizListAdapter.*
import com.cpan200.finalproject.R
import kotlinx.android.synthetic.main.panel_quiz.view.*

class QuizListAdapter(
	val context: Context,
	private val quizzes: MutableList<Quiz>,
	val viewMode: ViewMode
) : RecyclerView.Adapter<QuizPanelViewHolder>() {
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizPanelViewHolder {
		val view = LayoutInflater.from(context).inflate(R.layout.panel_quiz, parent, false)
		return QuizPanelViewHolder(view)

	}

	override fun getItemCount(): Int {
		return quizzes.size
	}

	override fun onBindViewHolder(holder: QuizPanelViewHolder, position: Int) {
		val quiz = quizzes[position]
		holder.setData(quiz, position, viewMode)
	}

	inner class QuizPanelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var currentQuiz: Quiz? = null
		var currentPosition: Int = 0

		fun setData(quiz: Quiz, pos: Int, viewMode: ViewMode){
			this.currentQuiz = quiz
			this.currentPosition = pos

			itemView.txtPanelQuizTitle.text = this.currentQuiz!!.title



		}

		init {
			//onclicklistener

		}
	}

	enum class ViewMode {
		ADMIN,
		STUDENT
	}
}