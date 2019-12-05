package com.cpan200.classes

import android.content.Context
import android.widget.Toast
import com.cpan200.dbclasses.UserDB

class App {
	companion object {
		var isLoggedIn: Boolean = false
		var currentUser: User? = null

		fun login(context: Context, tryUsername: String?, tryPassword: String?) {
			//check if username and password entered
			if (tryUsername == null || tryUsername == "") {
				showToast(context, "Please enter a username")
				return
			} else if (tryPassword == null || tryPassword == "") {
				showToast(context, "Please enter your password")
				return
			} else {

				//checking if username and password is correct
				val userDB = UserDB(context, null)
				val cursor = userDB.tryLogin(tryUsername, tryPassword)

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
						val email = cursor.getString(cursor.getColumnIndex(UserDB.COL_EMAIL))
						val firstName: String = cursor.getString(cursor.getColumnIndex(UserDB.COL_FIRSTNAME))
						val lastName: String = cursor.getString(cursor.getColumnIndex(UserDB.COL_LASTNAME))
						currentUser = User(username, password, id, status, email, firstName, lastName)
						isLoggedIn = true

						//open appropriate activity: admin or student


					}

					else -> {
						//no idea what's going on
						showToast(context, "Error reading username and password")
						return
					}
				}


			}

		}

		fun logout() {
			currentUser = null
			isLoggedIn = false

		}

		fun showToast(context: Context, msg: String, length: Int = Toast.LENGTH_LONG) {
			Toast.makeText(context, msg, length).show()
		}
	}
}