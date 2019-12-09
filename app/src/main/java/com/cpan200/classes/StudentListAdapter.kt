package com.cpan200.classes

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class StudentListAdapter(
    context: Context,
    val studentList: MutableList<User>
) : RecyclerView.Adapter<StudentListAdapter.StudentPanelViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentPanelViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        return studentList.size
    }

    override fun onBindViewHolder(holder: StudentPanelViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    inner class StudentPanelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    }
}