package com.cpan200.classes

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cpan200.dbclasses.QuizDB
import com.cpan200.dbclasses.QuizzesDB
import com.cpan200.dbclasses.UserDB
import com.cpan200.finalproject.AdminActivity
import com.cpan200.finalproject.LoginActivity
import com.cpan200.finalproject.R
import com.cpan200.finalproject.StudentActivity
import com.cpan200.finalproject.user_fragments.FragAdminMain
import com.cpan200.finalproject.user_fragments.FragScoresMain
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.util.*

class App {
	companion object {
		private const val LOG = "test123"

		private var isLoggedIn: Boolean = false
		var currentUser: User? = null
		var currentEditUser: User? = null

		private const val APP_KEY: String = "App"
		private const val USERNAME_KEY: String = "username"
		private const val PASSWORD_KEY: String = "password"

		var currentQuiz: Quiz = Quiz()
		var currentQuizAttempt = mutableListOf<Int>()

		var scoreViewMode: FragScoresMain.ViewMode = FragScoresMain.ViewMode.User

		//workaround for passing info to fragments. should use interfaces
		var quizListViewMode: QuizListAdapter.ViewMode = QuizListAdapter.ViewMode.STUDENT
			private set
			get() {
				return if (currentUser?.status == null || currentUser?.status == User.UserStatus.STUDENT)
					QuizListAdapter.ViewMode.STUDENT
				else if (currentUser?.status == User.UserStatus.SUPERUSER || currentUser?.status == User.UserStatus.ADMIN)
					QuizListAdapter.ViewMode.ADMIN
				else
					QuizListAdapter.ViewMode.STUDENT
			}

		var fragAdminMainViewMode: FragAdminMain.ViewMode = FragAdminMain.ViewMode.Quizzes


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
				if (quiz.finalized)
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
				questionList.add(cursorToQuestion(questionsCursor, quiz.maxOptions))

				while (questionsCursor.moveToNext()){
					questionList.add(cursorToQuestion(questionsCursor, quiz.maxOptions))
				}
			}

			//put questionList into the quiz
			quiz.questionList = questionList
			quizzesCursor.close()
			return quiz

		}

		fun addQuiz(context: Context) {
			val quizzesDB = QuizzesDB(context, null)

			//getting a set of IDs in the old table
			val oldIDSet = quizzesDB.getIDSet()

			//adding a new row to table
			quizzesDB.addRow()

			//getting the difference. hopefully exactly 1 result.
			val newIDSet = quizzesDB.getIDSet()
			val newID = newIDSet.minus(oldIDSet).first()
			quizzesDB.close()

			//now create columns for the new quiz's scores and attempts in the user table
			val userDB = UserDB(context, null)
			userDB.createQuizCol(newID)
			userDB.close()

			//create a db for this quiz's questions
			val quizDB = QuizDB(context, newID, null)
			quizDB.addQuiz()
			quizDB.close()

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
				quiz.finalized -> {
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
					quizDB.recreateTable(quiz)
					quizDB.close()
				}
			}
		}

		fun deleteQuiz(context: Context, id: Int){
			val quizzesDB = QuizzesDB(context, null)
			quizzesDB.deleteRow(id)
			quizzesDB.close()

			val quizDB = QuizDB(context, id, null)
			quizDB.deleteQuiz()
			quizDB.close()

			val userDB = UserDB(context, null)
			userDB.deleteQuizCol(id)
			userDB.close()
		}

		fun publishQuiz(context: Context, id: Int){
			val quizzesDB = QuizzesDB(context, null)
			quizzesDB.publishQuiz(id)
			quizzesDB.close()
		}

		fun maxOptionsToQuestions(quiz: Quiz): Quiz{
			for (question in quiz.questionList){
				while (question.answers.count() < quiz.maxOptions){
					question.answers.add("")
				}

				while (question.optionIds.count() < quiz.maxOptions){
					question.optionIds.add(-1)
				}
			}
			return quiz
		}

		fun submitScore(context: Context, id: Int, score: Double){
			showToast(context, "Your score: $score")
			val userDB = UserDB(context, null)
			val userCursor = userDB.getScoreAttempt(currentUser!!.name!!, id)

			if (userCursor != null){
				userCursor.moveToFirst()
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

			(context as AppCompatActivity).onBackPressed()
		}

		fun getScore(context: Context, id: Int): Double {
			val userDB = UserDB(context, null)
			val userCursor = userDB.getScoreAttempt(currentUser!!.name!!, id)

			var savedScore: Double? = 0.0
			if (userCursor != null){
				userCursor.moveToFirst()
				savedScore = userCursor.getDouble(userCursor.getColumnIndex(UserDB.COL_QUIZN + id.toString()))
			}
			userCursor?.close()
			userDB.close()
			return savedScore ?: 0.0
		}

		fun getAttempts(context: Context, id: Int): Int {
			val userDB = UserDB(context, null)
			val userCursor = userDB.getScoreAttempt(currentUser!!.name!!, id)

			var attempts: Int? = 0
			if (userCursor != null){
				userCursor.moveToFirst()
				attempts = userCursor.getInt(userCursor.getColumnIndex(UserDB.COL_ATTEMPTN + id.toString()))
			}
			userCursor?.close()
			userDB.close()
			return attempts ?: 0
		}

		private fun refreshCurrentUser(context: Context){
			val userDB = UserDB(context, null)
			val userCursor = userDB.findExistingUser(currentUser!!.name!!)
			if (userCursor != null && userCursor.count > 0){
				userCursor.moveToFirst()
				val freshUser = cursorToUser(userCursor)
				if (freshUser.name == currentUser!!.name!!){
					currentUser = freshUser
				}
			}
			userCursor?.close()
			userDB.close()
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
				cursor.close()
				userDB.close()
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

					showToast(context, "User $usernameE created.")

					(context as AppCompatActivity).onBackPressed()
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

				oldStatus != newStatus.toString() && username == currentUser!!.name -> {
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
					//if (context is Activity) context.onBackPressed()
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
				val passwordChanged = (oldPassword != newPasswordE)

				//update user info
				userDB.updateUserInfo(username, newPasswordE, newEmailE, newFirstNameE, newLastNameE)
				userCursor.close()
				userDB.close()

				//update currentUser global variable if current user was edited
				if (currentUser!!.name == username) {
					refreshCurrentUser(context)
				}

				//finishing
				if (!passwordChanged || currentUser!!.name != username){
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

		fun userScoresToTextView(context: Context, txtUsernames: TextView, txtScores: TextView){
			//get all users' scores for a particular quiz - currentQuiz
			val userDB = UserDB(context, null)
			val userCursor = userDB.getAllScores(currentQuiz.id!!)

			if (userCursor.count != 0){
				userCursor.moveToFirst()
				populateScoreRow(userCursor, currentQuiz.id!!, txtUsernames, txtScores)

				while (userCursor.moveToNext()){
					populateScoreRow(userCursor, currentQuiz.id!!, txtUsernames, txtScores)
				}
			}
			userCursor.close()
			userDB.close()
		}

		fun quizScoresToTextView(context: Context, user: User, txtQuizzes: TextView, txtScores: TextView){
			//gets specified user's scores for all quizzes
			//could also get attempts...
			val userDB = UserDB(context, null)
			val userCursor = userDB.findExistingUser(user.name!!)
			if (userCursor != null && userCursor.count != 0) {
				val quizDB = QuizzesDB(context, null)
				val quizCursor = quizDB.getAllRows()

				if (quizCursor != null && quizCursor.count != 0){
					//getting list of quiz IDs in the database
					val quizIDs = mutableListOf<Int>()
					val quizTitles = mutableListOf<String>()
					val userScores = mutableListOf<Double>()

					quizCursor.moveToFirst()
					quizIDs.add(quizCursor.getInt(quizCursor.getColumnIndex(QuizzesDB.COL_ID)))
					quizTitles.add(quizCursor.getString(quizCursor.getColumnIndex(QuizzesDB.COL_TITLE)))
					while (quizCursor.moveToNext()){
						quizIDs.add(quizCursor.getInt(quizCursor.getColumnIndex(QuizzesDB.COL_ID)))
						quizTitles.add(quizCursor.getString(quizCursor.getColumnIndex(QuizzesDB.COL_TITLE)))
					}

					//get scores in user row
					userCursor.moveToFirst()
					for (id in quizIDs){
						userScores.add(userCursor.getDouble(userCursor.getColumnIndex(UserDB.COL_QUIZN + id.toString())))
					}

					//set TextViews with quiz titles and user's scores
					for (i in 0 until quizIDs.count()){
						txtQuizzes.append(quizTitles[i])
						txtQuizzes.append("\n")
						txtScores.append(userScores[i].toString())
						txtScores.append("\n")
					}
				}
				quizCursor?.close()
				quizDB.close()
			}
			else {
				showToast(context, "Error fetching this user's scores.")
			}
			userCursor?.close()
			userDB.close()
		}

		fun openImageUriAndSave (context: Context, questionIndex: Int, recyclerAdapter: QuestionListAdapter) {
			//specify uri
			var link: String? = null
			var imgArray: ByteArray? = null

			val dialogue = AlertDialog.Builder(context)
			dialogue.setMessage((context as AppCompatActivity).getString(R.string.Enter_link_of_image_to_add))
			dialogue.setCancelable(true)
			val urlInput = EditText(context)
			dialogue.setView(urlInput)
			dialogue.setPositiveButton(context.getString(R.string.Add)
			) { _, _ ->
				link = urlInput.text.toString()

				if (link != null && link != ""){
					val url = URL(link) //"https://i.imgur.com/kt2cYyF.png"

					try{
						//getting the image...
						val httpConnection = url.openConnection() as HttpURLConnection
						httpConnection.doInput = true
						httpConnection.connect()
						val responseCode = httpConnection.responseCode

						if (responseCode == HttpURLConnection.HTTP_OK){

							val inputStream = (httpConnection as URLConnection).getInputStream()
							imgArray = inputStream.readBytes()
							inputStream.close()

							//save into quiz whether image is null or not
							currentQuiz.questionList[questionIndex].image = imgArray
							recyclerAdapter.refreshData()
						}
						else {
							showToast(context, "HTTP connection to image failed.")
						}
					}
					catch (e: Exception){
						showToast(context, "Exception fetching image: ${e.message.toString()}")
					}
				}
			}
			dialogue.setNegativeButton((context).getString(R.string.Cancel)
			) { thisDialogue, _ -> thisDialogue.cancel() }
			dialogue.create().show()

		}

		fun showToast(context: Context, msg: String, length: Int = Toast.LENGTH_LONG) {
			Toast.makeText(context, msg, length).show()
		}

//		fun showLog(msg: String){
//			Log.i(LOG, msg)
//		}

//		fun showusercols(context: Context){
//			val userDB = UserDB(context, null)
//			val usertest = userDB.getAllRows()
//			val tempList = mutableListOf<String>()
//			for (string in usertest!!.columnNames){
//				tempList.add(string)
//			}
//			showLog(tempList.toString())
//			usertest.close()
//			userDB.close()
//		}

		private fun populateScoreRow(cursor: Cursor, id: Int, txtUsernames: TextView, txtScores: TextView){
			txtUsernames.append(cursor.getString(cursor.getColumnIndex(UserDB.COL_USERNAME)))
			txtUsernames.append("\n")
			txtScores.append(cursor.getDouble(cursor.getColumnIndex(UserDB.COL_QUIZN + id.toString())).toString())
			txtScores.append("\n")
		}

		private fun cursorToQuestion(cursor: Cursor, maxOptions: Int): Question {
			val id: Int = cursor.getInt(cursor.getColumnIndex(QuizDB.COL_ID))
			val question: String = cursor.getString(cursor.getColumnIndex(QuizDB.COL_QUESTION))

			val answers = mutableListOf<String>()
			for (i in 0 until (maxOptions)){
				//get answer in the ith column
				val ansString: String? =cursor.getString(cursor.getColumnIndex(QuizDB.COL_ANS_N + i.toString()))
				answers.add(ansString ?: "")
			}
			val correctAnswer: Int = cursor.getInt(cursor.getColumnIndex(QuizDB.COL_CORRECTANS))
			val image: ByteArray? = cursor.getBlob(cursor.getColumnIndex(QuizDB.COL_IMAGE))
			return Question(id, question, answers, correctAnswer, image = image)
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