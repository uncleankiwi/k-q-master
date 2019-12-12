package com.cpan200.finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.cpan200.classes.App
import com.cpan200.finalproject.user_fragments.FragQuizList

class StudentActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_student)
		actionBar?.title = getString(R.string.ActionBarUsername,
			App.currentUser!!.name,
			App.currentUser!!.status)

		if (savedInstanceState == null){
			supportFragmentManager.beginTransaction()
				.add(R.id.StudentContainer, FragQuizList(), "FragQuizList").commit()
		}
	}
	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		val inflater: MenuInflater = menuInflater
		inflater.inflate(R.menu.student_menu, menu)
		return true
	}

	fun studentLogout(item: MenuItem) {
		App.logout(this)
	}
}
