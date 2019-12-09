package com.cpan200.classes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cpan200.finalproject.R
import kotlinx.android.synthetic.main.panel_student.view.*

class StudentListAdapter(
    val context: Context,
    val studentList: MutableList<User>
) : RecyclerView.Adapter<StudentListAdapter.StudentPanelViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentPanelViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.panel_student, parent, false)
        return StudentPanelViewHolder(view)
    }

    override fun getItemCount(): Int {
        return studentList.size
    }

    override fun onBindViewHolder(holder: StudentPanelViewHolder, position: Int) {
        val user = studentList[position]
        holder.setData(user, position)
    }

    inner class StudentPanelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var currentUser: User? = null
        var currentPosition = 0

        fun setData(user: User, pos: Int){
            this.currentUser = user
            this.currentPosition = pos

            itemView.txtStudentPanelUsername.text = currentUser!!.name
        }

    }
}