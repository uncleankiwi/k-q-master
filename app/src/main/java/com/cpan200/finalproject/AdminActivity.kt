package com.cpan200.finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.cpan200.classes.App
import com.cpan200.finalproject.user_fragments.FragAdminMain
import com.cpan200.finalproject.user_fragments.FragParticularsMain

class AdminActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_admin)
		supportActionBar?.title = getString(R.string.ActionBarUsername,
			App.currentUser!!.name,
			App.currentUser!!.status)

		//show admin main frag when logged in as admin
		if (savedInstanceState == null){
			supportFragmentManager.beginTransaction()
				.add(R.id.AdminContainer, FragAdminMain(), "FragAdminMain")
				.commit()
		}

	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		val inflater: MenuInflater = menuInflater
		inflater.inflate(R.menu.admin_menu, menu)
		return true
	}


	fun adminLogout(item: MenuItem) {
		App.logout(this)
	}

	fun adminEditParticulars(item: MenuItem){
		App.currentEditUser = App.currentUser
		supportFragmentManager.beginTransaction()
			.replace(R.id.AdminContainer, FragParticularsMain(), "FragParticularsMain")
			.addToBackStack(null)
			.commit()
	}
}
