package com.cpan200.classes

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.util.Log
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
		const val LOG = "test123"

		private var isLoggedIn: Boolean = false
		var currentUser: User? = null

		private const val APP_KEY: String = "App"
		private const val USERNAME_KEY: String = "username"
		private const val PASSWORD_KEY: String = "password"

		var currentQuiz: Quiz? = null

		var currentEditingQuiz: Quiz? = null
		var currentQuizAttempt = mutableListOf<Int>()

		//workaround for passing info to fragments. should use interfaces
		var quizListViewMode: QuizListAdapter.ViewMode = QuizListAdapter.ViewMode.ADMIN
		var questionListViewMode: QuestionListAdapter.ViewMode = QuestionListAdapter.ViewMode.EDIT

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
			quizzesCursor.close()

			return quizList
		}

		fun getFinalizedQuizList(context: Context):MutableList<Quiz>{
			val quizList = getQuizList(context)
			val finalizedQuizList = mutableListOf<Quiz>()
			for (quiz in quizList){
				if (quiz.finalized == true)
					finalizedQuizList.add(quiz)
			}
			return finalizedQuizList
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
			val quizzesDB = QuizzesDB(context, null)
			quizzesDB.addRow()
			quizzesDB.close()
		}

		fun editQuiz(context: Context, id: Int, quiz: Quiz) {
			//this edits 1 line in QuizzesDB
			//then drops QuizDB_N, recreates it,
			//then adds every question

			//checking quiz input
			when {
				quiz.title == "" -> {
					showToast(context, "Please enter a quiz title.")
					return
				}
				quiz.finalized == true -> {
					showToast(context, "Cannot modify a published quiz.")
					return
				}
				else -> {
					//editing quizzesDB entry
					val quizzesDB = QuizzesDB(context, null)
					quizzesDB.updateRow(id, quiz.title, quiz.questionList, quiz.finalized, quiz.maxAttempts)
					quizzesDB.close()

					//recreate QuizDB_N and add questions
					val quizDB = QuizDB(context, id, null)
					quizDB.recreateTable(id, quiz)
					quizDB.close()
				}
			}



		}

		fun deleteQuiz(context: Context, id: Int){
			val quizzesDB = QuizzesDB(context, null)
			quizzesDB.deleteRow(id)
			quizzesDB.close()
		}

		fun publishQuiz(context: Context, id: Int){
			val quizzesDB = QuizzesDB(context, null)
			quizzesDB.publishQuiz(id)
			quizzesDB.close()
		}

		fun addBlankQuestion(){
			//adds a blank question to App.currentQuiz. does not affect DB!
			//also adds list of answers of size maxOptions
			if (currentEditingQuiz!!.maxOptions == null) currentEditingQuiz!!.maxOptions = 5	//todo find val
			val options = currentEditingQuiz!!.maxOptions!!
			val emptyAns = mutableListOf<String>()
			for (i in 0 until options){
				emptyAns.add("")
			}
			currentEditingQuiz!!.questionList!!.add(Question(null, "(New question)", emptyAns, null))
		}

		fun submitScore(context: Context, id: Int, score: Double){
			showToast(context, "Your score: $score")
			val userDB = UserDB(context, null)
			val userCursor = userDB.getScoreAttempt(currentUser!!.name!!, id)

			if (userCursor != null){
				var savedScore: Double? = userCursor.getDouble(userCursor.getColumnIndex(UserDB.COL_QUIZN + id.toString()))
				var savedAttempts: Int? = userCursor.getInt(userCursor.getColumnIndex(UserDB.COL_ATTEMPTN + id.toString()))
				if (savedScore == null) savedScore = 0.0
				if (savedAttempts == null) savedAttempts = 0
				var highScore = score
				if (savedScore > highScore) highScore = savedScore
				userDB.updateScoreAttempt(currentUser!!.name!!, id, highScore, savedAttempts + 1)
			}

			userDB.close()
			userCursor?.close()
		}

		fun getScore(context: Context, id: Int): Double {
			val userDB = UserDB(context, null)
			val userCursor = userDB.getScoreAttempt(currentUser!!.name!!, id)

			var savedScore: Double? = 0.0
			if (userCursor != null){
				savedScore = userCursor.getDouble(userCursor.getColumnIndex(UserDB.COL_QUIZN + id.toString()))
			}
			if (savedScore == null)
				return 0.0
			else
				return savedScore
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

		fun createUser(context: Context, username: String?, password: String?, email: String?, firstName: String?, lastName: String?) {
			if (username == null || username.trim() == "") {
				showToast(context, "Please enter a username")
				return
			} else if (password == null || password.trim() == "") {
				showToast(context, "Please enter a password")
				return
			} else {
				val userDB = UserDB(context, null)
				val usernameE = username.trim()

				//check if username already taken
				val userCursor = userDB.findExistingUser(usernameE)
				val existingUsers: Int = userCursor!!.count
				userCursor.close()
				if (existingUsers != 0){
					showToast(context, "Username already taken")
					return
				}
				else {
					//start creating new user
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
					userDB.close()
				}

			}
		}

		fun deleteUser(context: Context, username: String) {
			//check if this is current user
			if (username == currentUser!!.name){
				showToast(context, "Can't delete a user currently logged in.")
			}
			else {
				val userDB = UserDB(context, null)

				//check if this user is a superuser. if so fail.
				val userCursor = userDB.findExistingUser(username)
				userCursor!!.moveToFirst()
				val user = cursorToUser(userCursor)
				if (user.status == User.UserStatus.SUPERUSER){
					showToast(context, "Can't delete a superuser!")
					userCursor.close()
					userDB.close()
					return
				}
				else {
					userDB.deleteRow(username)
					showToast(context, "Deleted user $username")
					userCursor.close()
					userDB.close()
				}
			}

		}

		fun changeUserStatus(context: Context, username: String, newStatus: User.UserStatus) {
			val userDB = UserDB(context, null)
			val userCursor = userDB.findExistingUser(username)
			userCursor!!.moveToFirst()
			val oldStatus = userCursor.getString(userCursor.getColumnIndex(UserDB.COL_STATUS))

			when {
				oldStatus == newStatus.toString() -> {
					userDB.close()
					userCursor.close()
					return
				}

				username == currentUser!!.name -> {
					showToast(context, "Can't change the status of a user currently logged in.")
					userDB.close()
					userCursor.close()
					return
				}

				else -> {
					userDB.updateUserStatus(username, newStatus)
					showToast(context, "User status changed.")
					userDB.close()
					userCursor.close()

					//press back button
					if (context is Activity) context.onBackPressed()
				}
			}

		}

		fun changeParticulars(context: Context, username: String, newPassword: String, newEmail:String?, newFirstName: String?, newLastName: String?) {
			//change password, email, firstName, lastName

			//sanitization
			val newPasswordE = newPassword.trim()
			val newEmailE = newEmail?.trim()
			val newFirstNameE = newFirstName?.trim()
			val newLastNameE = newLastName?.trim()

			if (newPasswordE == "") {
				showToast(context, "Please enter a password")
				return
			}
			else{
				//get old password
				val userDB = UserDB(context, null)
				val userCursor = userDB.findExistingUser(username)
				userCursor!!.moveToFirst()
				val oldPassword = userCursor.getString(userCursor.getColumnIndex(UserDB.COL_PASSWORD))
				val passwordChanged = (oldPassword == newPasswordE)

				//update user info
				userDB.updateUserInfo(username, newPasswordE, newEmailE, newFirstNameE, newLastNameE)

				//finishing
				if (!passwordChanged && currentUser!!.name == username){
					//if user specified is current user and password was changed, log out
					showToast(context, "User information changed.")

					//close fragment
					if (context is Activity) context.onBackPressed()
				}
				else {
					showToast(context, "Password changed. Please sign in with the new password.")
					logout(context, false)
				}

			}



		}

		fun getUserList(context: Context): MutableList<User>{
			val userList = mutableListOf<User>()
			val userCursor = UserDB(context, null).getAllRows()
			if (userCursor!!.count != 0){
				userCursor.moveToFirst()
				userList.add(cursorToUser(userCursor))

				while (userCursor.moveToNext()){
					userList.add(cursorToUser(userCursor))
				}
			}
			userCursor.close()

			return userList
		}

		fun showToast(context: Context, msg: String, length: Int = Toast.LENGTH_LONG) {
			Toast.makeText(context, msg, length).show()
		}

		fun showLog(msg: String){
			Log.i(LOG, msg)
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