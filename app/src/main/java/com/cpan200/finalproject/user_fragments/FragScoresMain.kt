package com.cpan200.finalproject.user_fragments


import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        val userDB = UserDB(context, null)
        val userCursor = userDB.getAllScores(App.currentQuiz!!.id!!)

        if (userCursor.count != 0){
            userCursor.moveToFirst()
            populateScoreRow(userCursor, App.currentQuiz!!.id!!)

            while (userCursor.moveToNext()){
                populateScoreRow(userCursor, App.currentQuiz!!.id!!)
            }
        }

        userDB.close()
        userCursor.close()

        return view
    }

    private fun populateScoreRow(cursor: Cursor, id: Int){
        txtScoresMainUsername.append(cursor.getString(cursor.getColumnIndex(UserDB.COL_USERNAME)))
        txtScoresMainUsername.append("\n")
        txtScoresMainScore.append(cursor.getDouble(cursor.getColumnIndex(UserDB.COL_QUIZN + id.toString())).toString())
        txtScoresMainScore.append("\n")
    }


}
