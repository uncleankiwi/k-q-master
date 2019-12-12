package com.cpan200.classes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.cpan200.finalproject.R
import com.cpan200.finalproject.user_fragments.FragParticularsMain
import com.cpan200.finalproject.user_fragments.FragScoresMain
import kotlinx.android.synthetic.main.panel_student.view.*

class StudentListAdapter(
        val context: Context,
        private var studentList: MutableList<User>
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

    fun refreshData(){
        studentList = App.getUserList(context)
        notifyDataSetChanged()
    }

    inner class StudentPanelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private var currentUser: User? = null
        private var currentPosition = 0

        fun setData(user: User, pos: Int){
            this.currentUser = user
            this.currentPosition = pos

            itemView.txtStudentPanelUsername.text = context.getString(R.string.txtStudentPanelUsername,
                    currentUser!!.name,
                    currentUser!!.status)
        }

        init {
            itemView.btnStudentPanelDelete.setOnClickListener {
                App.deleteUser(context, this.currentUser!!.name!!)
                refreshData()
            }
            itemView.btnStudentPanelScores.setOnClickListener {
                App.currentEditUser = this.currentUser
                App.scoreViewMode = FragScoresMain.ViewMode.User
                (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.AdminContainer, FragScoresMain(), "FragScoresMain")
                    .addToBackStack(null)
                    .commit()
            }
            itemView.btnStudentPanelEdit.setOnClickListener {
                App.currentEditUser = this.currentUser
                (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.AdminContainer, FragParticularsMain(), "FragParticularsMain")
                    .addToBackStack(null)
                    .commit()
            }
        }

    }
}