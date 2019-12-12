package com.cpan200.finalproject.user_fragments


import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cpan200.classes.App
import com.cpan200.dbclasses.UserDB
import com.cpan200.finalproject.R
import kotlinx.android.synthetic.main.fragment_scores_main.*

/**
 * A simple [Fragment] subclass.
 */
class FragScoresMain : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_scores_main, container, false)

		val valTxtScoresMainHeader = (context!! as AppCompatActivity).findViewById<TextView>(R.id.txtScoresMainHeader)
		val valTxtScoresMainUsername = (context!! as AppCompatActivity).findViewById<TextView>(R.id.txtScoresMainUsername)
		val valTxtScoresMainScore = (context!! as AppCompatActivity).findViewById<TextView>(R.id.txtScoresMainScore)

		val valBtnScoresMainClose = (context!! as AppCompatActivity).findViewById<Button>(R.id.btnScoresMainClose)
		if (valBtnScoresMainClose == null)
		App.showLog("is null")
		valBtnScoresMainClose.setOnClickListener { (context!! as AppCompatActivity).onBackPressed() }	//todo crash here

		when (App.scoreViewMode){
			ViewMode.User -> {
				//when viewing a user's scores in all quizzes
				valTxtScoresMainHeader.text = getString(R.string.Quiz_prefix)
				App.quizScoresToTextView(context!!, App.currentEditUser!!, valTxtScoresMainUsername, valTxtScoresMainScore)
			}
			ViewMode.Quiz -> {
				//when viewing all users' scores for CurrentQuiz
				valTxtScoresMainHeader.text = getString(R.string.Username_prefix)
				App.userScoresToTextView(context!!, valTxtScoresMainUsername, valTxtScoresMainScore)		//todo another crash here
			}
		}
        return view
    }

	enum class ViewMode{
		Quiz,
		User
	}
}
