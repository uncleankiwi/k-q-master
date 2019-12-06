package com.cpan200.classes

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.cpan200.dbclasses.UserDB
import com.cpan200.finalproject.AdminActivity
import com.cpan200.finalproject.LoginActivity
import com.cpan200.finalproject.StudentActivity
import java.util.*

class App {
	companion object {
		private var isLoggedIn: Boolean = false
		private var currentUser: User? = null

		fun login(context: Context, tryUsername: String?, tryPassword: String?) {
			//check if username and password entered
			if (tryUsername == null || tryUsername.trim() == "") {
				showToast(context, "Please enter a username")
				return
			} else if (tryPassword == null || tryPassword.trim() == "") {
				showToast(context, "Please enter your password")
				return
			} else {

				//checking if username and password is correct
				val userDB = UserDB(context, null)
				val cursor = userDB.tryLogin(tryUsername.trim(), tryPassword.trim())

				when (cursor?.count) {
					0 -> {
						//no user with this name/password
						showToast(context, "Username or password is incorrect")
						return
					}
					1 -> {
						//username found
						cursor.moveToFirst()
						val username = cursor.getString(cursor.getColumnIndex(UserDB.COL_USERNAME))
						val password = cursor.getString(cursor.getColumnIndex(UserDB.COL_PASSWORD))
						val id: Int = cursor.getInt(cursor.getColumnIndex(UserDB.COL_ID))
						val status: User.UserStatus = User.UserStatus.valueOf(cursor.getString(cursor.getColumnIndex(UserDB.COL_STATUS)))
						val email: String? = cursor.getString(cursor.getColumnIndex(UserDB.COL_EMAIL))
						val firstName: String? = cursor.getString(cursor.getColumnIndex(UserDB.COL_FIRSTNAME))
						val lastName: String? = cursor.getString(cursor.getColumnIndex(UserDB.COL_LASTNAME))
						currentUser = User(username, password, id, status, email, firstName, lastName)
						isLoggedIn = true

						//now opening the appropriate activity - admin or student
						if (context is Activity) {
							if (currentUser!!.status == User.UserStatus.ADMIN || currentUser!!.status == User.UserStatus.SUPERUSER) {
								//user logged in is admin. open admin activity
								context.startActivity(Intent(context, AdminActivity::class.java))
							} else if (currentUser!!.status == User.UserStatus.STUDENT) {
								//user logged in is a student. open student activity
								context.startActivity(Intent(context, StudentActivity::class.java))
							}
							showToast(context, "Logged in as ${currentUser!!.status.toString().toLowerCase(Locale.getDefault())} ${currentUser!!.name}")
							context.finish()
						}


						//shared prefs to remember current user
						//TODO

					}

					else -> {
						//no idea what's going on
						showToast(context, "Error reading username and password")
						return
					}
				}


			}

		}

		fun logout(context: Context) {
			//remove user from shared prefs
			//todo

			//move user back to login activity
			if (context is Activity){
				showToast(context, "Logged out of ${currentUser!!.status.toString().toLowerCase(Locale.getDefault())} ${currentUser?.name}")
				currentUser = null
				isLoggedIn = false
				context.startActivity(Intent(context, LoginActivity::class.java))
				context.finish()
			}


		}

		fun createUser(context: Context, username: String?, password: String?,
					   email: String?, firstName: String?, lastName: String?){
			if (username == null || username.trim() == "") {
				showToast(context, "Please enter a username")
				return
			}
			else if (password == null || password.trim() == ""){
				showToast(context, "Please enter a password")
				return
			}
			else {
				val userDB = UserDB(context, null)
				val usernameE = username.trim()
				val passwordE: String = password.trim()
				var emailE: String? = email?.trim()
				if (emailE == "") emailE = null
				var firstNameE: String? = firstName?.trim()
				if (firstNameE == "") firstNameE = null
				var lastNameE: String? = lastName?.trim()
				if (lastNameE == "") lastNameE = null

				//make a student
				var statusE: String = User.UserStatus.STUDENT.toString()
				if (userDB.rows() == 0){
					//...unless this is the first user ever created
					//in which case make a SUPERUSER
					statusE = User.UserStatus.SUPERUSER.toString()
				}

				userDB.addRow(usernameE, passwordE, statusE, emailE, firstNameE, lastNameE)

			}
		}

		fun deleteUser(){
			//todo
		}

		fun changeUserStatus(){
			//todo
		}

		fun changeOwnParticulars(){
			//todo
		}

		fun showToast(context: Context, msg: String, length: Int = Toast.LENGTH_LONG) {
			Toast.makeText(context, msg, length).show()
		}
	}
}