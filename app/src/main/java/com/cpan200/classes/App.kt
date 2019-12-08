package com.cpan200.classes

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.widget.Toast
import com.cpan200.dbclasses.QuizDB
import com.cpan200.dbclasses.QuizzesDB
import com.cpan200.dbclasses.UserDB
import com.cpan200.finalproject.AdminActivity
import com.cpan200.finalproject.LoginActivity
import com.cpan200.finalproject.StudentActivity
import java.sql.Blob
import java.util.*

class App {
	companion object {
		private var isLoggedIn: Boolean = false
		private var currentUser: User? = null

		private const val APP_KEY: String = "App"
		private const val USERNAME_KEY: String = "username"
		private const val PASSWORD_KEY: String = "password"

		fun getQuizList(context: Context): MutableList<Quiz> {
			val quizList = mutableListOf<Quiz>()

			//read entire quizzesDB, populate quizList
			val quizzesCursor = QuizzesDB(context, null).getAllRows()
			if (quizzesCursor!!.count != 0){
				quizzesCursor.moveToFirst()
				quizList.add(cursorToQuiz(quizzesCursor))

				while (quizzesCursor.moveToNext()){
					quizList.add(cursorToQuiz(quizzesCursor))
				}
			}

			return quizList
		}

		fun getQuiz(context: Context, id: Int): Quiz {
			var quiz = Quiz()
			val questionList = mutableListOf<Question>()

			//read 'id'th row of quizzesDB, take that one quiz out
			val quizzesCursor = QuizzesDB(context, null).getRow(id)
			if (quizzesCursor!!.count != 0){
				quizzesCursor.moveToFirst()
				quiz = cursorToQuiz(quizzesCursor)
			}

			//read quizDB_N, populate questionList
			val questionsCursor = QuizDB(context, id, null).getAllRows()
			if (questionsCursor!!.count != 0){
				questionsCursor.moveToFirst()
				questionList.add(cursorToQuestion(questionsCursor, quiz.maxOptions!!))

				while (questionsCursor.moveToNext()){
					questionList.add(cursorToQuestion(questionsCursor, quiz.maxOptions!!))
				}
			}

			//put questionList into the quiz
			quiz.questionList = questionList
			return quiz

		}

		fun addQuiz(context: Context) {
			QuizzesDB(context, null).addRow()
		}

		fun addQuestion(context: Context, question: String?, answers: MutableList<String>?, correctAnswer: Int, image: Blob?) {

		}

		fun editQuiz(id: Int, quiz: Quiz) {
			//this edits 1 line in QuizzesDB
			//then drops QuizDB_N, recreates it,
			//then adds every question
		}

		fun login(context: Context, tryUsername: String?, tryPassword: String?, verbose: Boolean = true) {
			//check if username and password entered
			if (tryUsername == null || tryUsername.trim() == "") {
				if (verbose)
					showToast(context, "Please enter a username")
				return
			} else if (tryPassword == null || tryPassword.trim() == "") {
				if (verbose)
					showToast(context, "Please enter your password")
				return
			} else {

				//checking if username and password is correct
				val userDB = UserDB(context, null)
				val cursor = userDB.tryLogin(tryUsername.trim(), tryPassword.trim())

				when (cursor?.count) {
					0 -> {
						//no user with this name/password
						if (verbose)
							showToast(context, "Username or password is incorrect")
						return
					}
					1 -> {
						//username found
						cursor.moveToFirst()
						currentUser = cursorToUser(cursor)
						isLoggedIn = true

						//shared prefs to remember current user
						val editor =
								context.getSharedPreferences(APP_KEY, Context.MODE_PRIVATE).edit()
						editor.putString(USERNAME_KEY, currentUser!!.name)
						editor.putString(PASSWORD_KEY, currentUser!!.password)
						editor.apply()

						//now opening the appropriate activity - admin or student
						if (context is Activity) {
							if (currentUser!!.status == User.UserStatus.ADMIN || currentUser!!.status == User.UserStatus.SUPERUSER) {
								//user logged in is admin. open admin activity
								context.startActivity(Intent(context, AdminActivity::class.java))
							} else if (currentUser!!.status == User.UserStatus.STUDENT) {
								//user logged in is a student. open student activity
								context.startActivity(Intent(context, StudentActivity::class.java))
							}
							if (verbose)
								showToast(
										context,
										"Logged in as ${currentUser!!.status.toString().toLowerCase(
												Locale.getDefault()
										)} ${currentUser!!.name}"
								)
							context.finish()
						}
					}
					else -> {
						//multiple users with same username and password found? no idea what's going on.
						if (verbose)
							showToast(context, "Error reading username and password")
						return
					}
				}


			}

		}

		fun loginWithPrefs(context: Context) {
			val prefs = context.getSharedPreferences(APP_KEY, Context.MODE_PRIVATE)
			val tryUsername = prefs.getString(USERNAME_KEY, null)
			val tryPassword = prefs.getString(PASSWORD_KEY, null)
			if (tryUsername != null && tryPassword != null) {
				login(context, tryUsername, tryPassword, false)
			}
		}

		fun logout(context: Context, verbose: Boolean = true) {
			//remove user from shared prefs
			val editor = context.getSharedPreferences(APP_KEY, Context.MODE_PRIVATE).edit()
			editor.clear().apply()

			//move user back to login activity
			if (context is Activity) {
				if (verbose)
					showToast(
							context,
							"Logged out of ${currentUser!!.status.toString().toLowerCase(Locale.getDefault())} ${currentUser?.name}"
					)
				currentUser = null
				isLoggedIn = false
				context.startActivity(Intent(context, LoginActivity::class.java))
				context.finish()
			}


		}

		fun createUser(
				context: Context, username: String?, password: String?,
				email: String?, firstName: String?, lastName: String?
		) {
			if (username == null || username.trim() == "") {
				showToast(context, "Please enter a username")
				return
			} else if (password == null || password.trim() == "") {
				showToast(context, "Please enter a password")
				return
			} else {
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
				if (userDB.rows() == 0) {
					//...unless this is the first user ever created
					//in which case make a SUPERUSER
					statusE = User.UserStatus.SUPERUSER.toString()
				}

				userDB.addRow(usernameE, passwordE, statusE, emailE, firstNameE, lastNameE)

			}
		}

		fun deleteUser() {
			//todo
		}

		fun changeUserStatus() {
			//todo
		}

		fun changeOwnParticulars() {
			//todo
		}

		fun showToast(context: Context, msg: String, length: Int = Toast.LENGTH_LONG) {
			Toast.makeText(context, msg, length).show()
		}

		private fun cursorToQuestion(cursor: Cursor, maxOptions: Int): Question {
			val id: Int = cursor.getInt(cursor.getColumnIndex(QuizDB.COL_ID))
			val question: String = cursor.getString(cursor.getColumnIndex(QuizDB.COL_QUESTION))
			val answers = mutableListOf<String>()
			for (i in 0 until (maxOptions - 1)){
				//get answer in the ith column
				answers.add(cursor.getString(cursor.getColumnIndex(QuizDB.COL_ANS_N + i.toString())))
			}
			val correctAnswer: Int = cursor.getInt(cursor.getColumnIndex(QuizDB.COL_CORRECTANS))
			return Question(id, question, answers, correctAnswer)
		}

		private fun cursorToQuiz(cursor: Cursor): Quiz{
			val id: Int = cursor.getInt(cursor.getColumnIndex(QuizzesDB.COL_ID))
			val title: String = cursor.getString(cursor.getColumnIndex(QuizzesDB.COL_TITLE))
			val questions: Int = cursor.getInt(cursor.getColumnIndex(QuizzesDB.COL_QUESTIONS))
			val totalMarks: Int = cursor.getInt(cursor.getColumnIndex(QuizzesDB.COL_TOTALMARKS))
			//converts int to boolean
			val finalized: Boolean = (cursor.getInt(cursor.getColumnIndex(QuizzesDB.COL_FINALIZED)) == 1)
			val maxOptions: Int = cursor.getInt(cursor.getColumnIndex(QuizzesDB.COL_MAXOPTIONS))
			val maxAttempts: Int = cursor.getInt(cursor.getColumnIndex(QuizzesDB.COL_MAXATTEMPTS))
			return Quiz(id, title, questions, totalMarks, finalized, maxOptions, maxAttempts, mutableListOf())
		}

		private fun cursorToUser(cursor: Cursor): User {
			val username = cursor.getString(cursor.getColumnIndex(UserDB.COL_USERNAME))
			val password = cursor.getString(cursor.getColumnIndex(UserDB.COL_PASSWORD))
			val id: Int = cursor.getInt(cursor.getColumnIndex(UserDB.COL_ID))
			val status: User.UserStatus =
					User.UserStatus.valueOf(cursor.getString(cursor.getColumnIndex(UserDB.COL_STATUS)))
			val email: String? = cursor.getString(cursor.getColumnIndex(UserDB.COL_EMAIL))
			val firstName: String? = cursor.getString(cursor.getColumnIndex(UserDB.COL_FIRSTNAME))
			val lastName: String? = cursor.getString(cursor.getColumnIndex(UserDB.COL_LASTNAME))
			return User(username, password, id, status, email, firstName, lastName)
		}
	}
}