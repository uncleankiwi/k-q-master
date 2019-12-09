package com.cpan200.classes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cpan200.classes.QuizListAdapter.*
import com.cpan200.finalproject.R

class QuizListAdapter(
	val context: Context,
	val quizzes: MutableList<Quiz>
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
		//holder.setData
	}

	inner class QuizPanelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


		init {
			//onclicklistener

		}
	}
}