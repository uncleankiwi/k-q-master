package com.cpan200.classes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cpan200.finalproject.R

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
        //holder.setdata
    }

    inner class StudentPanelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    }
}