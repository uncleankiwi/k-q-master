package com.cpan200.classes

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class QuestionListAdapter(
    context: Context,
    val quiz: Quiz
) : RecyclerView.Adapter<QuestionListAdapter.QuestionPanelViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionPanelViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        return quiz.questionList?.count() ?: 0
    }

    override fun onBindViewHolder(holder: QuestionPanelViewHolder, position: Int) {
        quiz.questionList = quiz.questionList ?: mutableListOf()
        val question = quiz.questionList!![position]
        //holder set data
    }

    inner class QuestionPanelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    enum class viewMode {
        EDIT,
        DO
    }
}