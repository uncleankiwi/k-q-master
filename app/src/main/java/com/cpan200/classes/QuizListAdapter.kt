package com.cpan200.classes

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.cpan200.classes.QuizListAdapter.*
import com.cpan200.finalproject.R
import com.cpan200.finalproject.user_fragments.FragQuizMain
import kotlinx.android.synthetic.main.panel_quiz.view.*

class QuizListAdapter(
		val context: Context,
		private var quizzes: MutableList<Quiz>,
		private val viewMode: ViewMode
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

	fun refreshData(){
		quizzes = App.getQuizList(context)
		notifyDataSetChanged()
	}

	inner class QuizPanelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		private var currentQuiz: Quiz? = null
		private var currentPosition: Int = 0

		fun setData(quiz: Quiz, pos: Int, viewMode: ViewMode){
			this.currentQuiz = quiz
			this.currentPosition = pos

			itemView.txtPanelQuizTitle.text = this.currentQuiz!!.title

			itemView.txtPanelQuizStats.text = "Finalized: ${this.currentQuiz!!.finalized}" +
					"\nNumber of questions: ${this.currentQuiz!!.questions}"

			itemView.btnPanelQuizEdit.isEnabled = !this.currentQuiz!!.finalized!!
			itemView.btnPanelQuizPublish.isEnabled = !this.currentQuiz!!.finalized!!
			itemView.btnPanelQuizScores.isEnabled = this.currentQuiz!!.finalized!!

		}

		init {
			itemView.btnPanelQuizDelete.setOnClickListener {
				App.showToast(context, "Deleted ${currentQuiz!!.title}")
				App.deleteQuiz(context, this.currentQuiz!!.id!!)
				refreshData()
			}

			itemView.btnPanelQuizPublish.setOnClickListener {
				App.publishQuiz(context, this.currentQuiz!!.id!!)
				refreshData()
			}

			itemView.setOnClickListener {
				//bring up this quiz if finalized
				if (this.currentQuiz!!.finalized!!){
					App.currentQuiz = App.getQuiz(context, this.currentQuiz!!.id!!)
					(context as AppCompatActivity).supportFragmentManager.beginTransaction()
							.replace(R.id.AdminContainer, FragQuizMain(), "FragQuizMain")
							.addToBackStack(null)
							.commit()
				}
			}

			itemView.btnPanelQuizEdit.setOnClickListener {
				//open up an unfinalized quiz for editing
				if (!this.currentQuiz!!.finalized!!){
					App.currentQuiz = App.getQuiz(context, this.currentQuiz!!.id!!)
					(context as AppCompatActivity).supportFragmentManager.beginTransaction()
							.replace(R.id.AdminContainer, FragQuizMain(), "FragQuizMain")
							.addToBackStack(null)
							.commit()
				}
			}

			itemView.btnPanelQuizScores.setOnClickListener {

			}



		}
	}

	enum class ViewMode {
		ADMIN,
		STUDENT
	}
}