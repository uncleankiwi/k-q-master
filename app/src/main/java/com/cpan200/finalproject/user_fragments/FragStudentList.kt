package com.cpan200.finalproject.user_fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cpan200.classes.App
import com.cpan200.classes.StudentListAdapter
import com.cpan200.finalproject.R


/**
 * A simple [Fragment] subclass.
 */
class FragStudentList : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student_list, container, false)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        val valRcyStudentList = view.findViewById<RecyclerView>(R.id.rcyStudentList)
        valRcyStudentList.layoutManager = layoutManager
        valRcyStudentList.adapter = StudentListAdapter(context!!, App.getUserList(context!!))

        return view
    }


}
