package com.cpan200.dbclasses

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.cpan200.classes.Question
import com.cpan200.classes.Quiz

class QuizzesDB(
		context: Context?,
		//name: String?,
		factory: SQLiteDatabase.CursorFactory?
		//version: Int
) : SQLiteOpenHelper(context, filename, factory, version) {
	companion object{
		private const val version = 1
		private const val filename = "Quizzes.db"
		const val TABLE_NAME = "quizzes"
		const val COL_ID = "_id"
		const val COL_TITLE = "title"
		const val COL_QUESTIONS = "questions"
		const val COL_TOTALMARKS = "totalmarks"
		const val COL_FINALIZED = "finalized"       //ready to publish?
		const val COL_MAXOPTIONS = "maxoptions"     //highest number of choices in an MCQ
		const val COL_MAXATTEMPTS = "maxattempts"
	}

	override fun onCreate(db: SQLiteDatabase?) {
		db?.execSQL("CREATE TABLE $TABLE_NAME (" +
				"$COL_ID INTEGER PRIMARY KEY, " +
				"$COL_TITLE TEXT, " +
				"$COL_QUESTIONS INT, " +
				"$COL_TOTALMARKS REAL, " +
				"$COL_FINALIZED INT, " +
				"$COL_MAXOPTIONS INT, " +
				"$COL_MAXATTEMPTS INT)")
	}

	override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
		db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
		onCreate(db)
	}

	fun getRow(id: Int) : Cursor? {
		return this.readableDatabase.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COL_ID = \"$id\"", null)
	}

	fun getAllRows() : Cursor? {
		return this.readableDatabase.rawQuery("SELECT * FROM $TABLE_NAME", null)
	}

	fun addRow(){
		val db = this.writableDatabase
		val row = ContentValues()
		val dummyQuiz = Quiz()
		row.put(COL_TITLE, dummyQuiz.title)
		row.put(COL_QUESTIONS, dummyQuiz.questions)
		row.put(COL_TOTALMARKS, dummyQuiz.totalMarks)
		row.put(COL_FINALIZED, dummyQuiz.finalized)
		row.put(COL_MAXOPTIONS, dummyQuiz.maxOptions)
		row.put(COL_MAXATTEMPTS, dummyQuiz.maxAttempts)
		db.insert(TABLE_NAME, null, row)
		db.close()
	}

	fun updateRow(id: Int, title: String, questionList: MutableList<Question>, finalized: Boolean, maxAttempts: Int){
		val row = ContentValues()
		val db = this.writableDatabase
		row.put(COL_TITLE, title)
		row.put(COL_QUESTIONS, questionList.size)
		row.put(COL_TOTALMARKS, questionList.size)

		row.put(COL_FINALIZED, finalized)
		row.put(COL_MAXOPTIONS, 5)  //future feature: allow more options...
		row.put(COL_MAXATTEMPTS, maxAttempts)
		db.update(TABLE_NAME, row, "$COL_ID = $id", null)
		db.close()
	}

	fun deleteRow(id: Int){
		val db = this.writableDatabase
		db.execSQL("DELETE FROM $TABLE_NAME WHERE $COL_ID = $id")
		db.close()
	}

	fun publishQuiz(id: Int){
		val db = this.writableDatabase
		val row = ContentValues()
		row.put(COL_FINALIZED, true)
		db.update(TABLE_NAME, row, "$COL_ID = $id", null)
		db.close()
	}


	fun getIDSet(): HashSet<Int>{
		val db = this.writableDatabase

		val idSet = hashSetOf<Int>()
		val idCursor = db.rawQuery("SELECT $COL_ID FROM $TABLE_NAME", null)
		if (idCursor.count != 0){
			idCursor.moveToFirst()
			idSet.add(idCursor.getInt(idCursor.getColumnIndex(COL_ID)))
			while (idCursor.moveToNext()){
				idSet.add(idCursor.getInt(idCursor.getColumnIndex(COL_ID)))
			}
		}
		idCursor.close()
		db.close()
		return idSet
	}

}