package com.cpan200.finalproject.user_fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.cpan200.classes.App
import com.cpan200.classes.StudentListAdapter
import com.cpan200.finalproject.R
import kotlinx.android.synthetic.main.fragment_student_list.*


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
        rcyStudentList.layoutManager = layoutManager
        rcyStudentList.adapter = StudentListAdapter(context!!, App.getUserList(context!!))
        return view
    }


}
