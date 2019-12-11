package com.cpan200.classes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.cpan200.classes.QuizListAdapter.QuizPanelViewHolder
import com.cpan200.finalproject.R
import com.cpan200.finalproject.user_fragments.FragQuizMain
import com.cpan200.finalproject.user_fragments.FragScoresMain
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
		val isAdmin = App.currentUser!!.status == User.UserStatus.SUPERUSER || App.currentUser!!.status == User.UserStatus.ADMIN
		quizzes = if (!isAdmin)
			App.getFinalizedQuizList(context)
		else
			App.getQuizList(context)
		notifyDataSetChanged()
	}

	inner class QuizPanelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		private var currentQuiz: Quiz? = null
		private var currentPosition: Int = 0

		fun setData(quiz: Quiz, pos: Int, viewMode: ViewMode){
			this.currentQuiz = quiz
			this.currentPosition = pos

			itemView.txtPanelQuizTitle.text = this.currentQuiz!!.title

			itemView.txtPanelQuizStats.text = context.getString(
					R.string.txtPanelQuizStats,
					this.currentQuiz!!.finalized.toString(),
					this.currentQuiz!!.questions,
					App.getAttempts(context, pos),
					App.getScore(context, pos))

			itemView.btnPanelQuizEdit.isEnabled = !this.currentQuiz!!.finalized
			itemView.btnPanelQuizPublish.isEnabled = !this.currentQuiz!!.finalized
			itemView.btnPanelQuizScores.isEnabled = this.currentQuiz!!.finalized

			val isAdmin = App.currentUser!!.status == User.UserStatus.SUPERUSER || App.currentUser!!.status == User.UserStatus.ADMIN
			//val isEditMode = isAdmin && !this.currentQuiz!!.finalized!!
			itemView.llPanelQuizContainer.isGone = !isAdmin

		}



		init {
			val isAdmin = App.currentUser!!.status == User.UserStatus.SUPERUSER || App.currentUser!!.status == User.UserStatus.ADMIN
			var container = R.id.StudentContainer
			if (isAdmin) {
				container = R.id.AdminContainer
			}

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
				if (this.currentQuiz!!.finalized){
					App.currentQuiz = App.getQuiz(context, this.currentQuiz!!.id!!)
					(context as AppCompatActivity).supportFragmentManager.beginTransaction()
							.replace(container, FragQuizMain(), "FragQuizMain")
							.addToBackStack(null)
							.commit()
				}
			}

			itemView.btnPanelQuizEdit.setOnClickListener {
				//open up an unfinalized quiz for editing AND the current user is a superuser/admin
				if (isAdmin && !this.currentQuiz!!.finalized){
					App.currentQuiz = App.getQuiz(context, this.currentQuiz!!.id!!)
					(context as AppCompatActivity).supportFragmentManager.beginTransaction()
							.replace(R.id.AdminContainer, FragQuizMain(), "FragQuizMain")
							.addToBackStack(null)
							.commit()
				}
			}

			itemView.btnPanelQuizScores.setOnClickListener {
				if (isAdmin && this.currentQuiz!!.finalized){
					App.currentQuiz = App.getQuiz(context, this.currentQuiz!!.id!!)
					(context as AppCompatActivity).supportFragmentManager.beginTransaction()
						.replace(R.id.AdminContainer, FragScoresMain(), "FragScoresMain")
						.addToBackStack(null)
						.commit()
				}
			}



		}
	}

	enum class ViewMode {
		ADMIN,
		STUDENT
	}
}