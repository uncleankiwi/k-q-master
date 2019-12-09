package com.cpan200.dbclasses

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.cpan200.classes.Question
import com.cpan200.classes.Quiz

class QuizDB(
    context: Context?,
    quizID: Int,
    factory: SQLiteDatabase.CursorFactory?
    //version: Int
) : SQLiteOpenHelper(context, "$filenameA$quizID$filenameB", factory, version) {
    companion object{
        private const val version = 1
        private const val filenameA = "Quiz"
        private const val filenameB = ".db"
        const val COL_ID = "_id"
        const val COL_QUESTION = "question"
        const val COL_ANS_N = "ans"             //choices: ans1, ans2, ans3, etc
        const val COL_MARKS = "marks"
        const val COL_CORRECTANS = "correctans"
        const val COL_IMAGE = "image"             //picture associated with this question
    }

    private val tableName = "quiz${quizID}"


    override fun onCreate(db: SQLiteDatabase?) {
        //questions, students, quiz IDs all begin with 0 in storage
        //in actual use, 1 is added
        db?.execSQL("CREATE TABLE $tableName (" +
                "$COL_ID INTEGER PRIMARY KEY, " +
                "$COL_QUESTION TEXT, " +
                "${COL_ANS_N}0 INT, " +
                "${COL_ANS_N}1 INT, " +
                "${COL_ANS_N}2 INT, " +
                "${COL_ANS_N}3 INT, " +
                "${COL_ANS_N}4 INT, " +
                "$COL_MARKS REAL, " +
                "$COL_CORRECTANS INT, " +
                "$COL_IMAGE BLOB)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $tableName")
        onCreate(db)
    }


    fun getAllRows() : Cursor? {
        return this.readableDatabase.rawQuery("SELECT * FROM $tableName", null)
    }

    fun recreateTable(id: Int, quiz: Quiz){
        val db = this.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS $tableName")
        db.execSQL("CREATE TABLE $tableName (" +
                "$COL_ID INTEGER PRIMARY KEY, " +
                "$COL_QUESTION TEXT, " +
                "${COL_ANS_N}0 INT, " +
                "${COL_ANS_N}1 INT, " +
                "${COL_ANS_N}2 INT, " +
                "${COL_ANS_N}3 INT, " +
                "${COL_ANS_N}4 INT, " +
                "$COL_MARKS REAL, " +
                "$COL_CORRECTANS INT, " +
                "$COL_IMAGE BLOB)")

        for (question: Question in quiz.questionList!!){
            addRow(db, question)
        }

        db.close()
    }

    private fun addRow(db: SQLiteDatabase, question: Question){
        val row = ContentValues()
        row.put(COL_QUESTION, question.question)
        if (question.answers != null) {
            for ((i, ans: String?) in question.answers!!.withIndex()) {
                if (ans != "")
                    row.put(COL_ANS_N + i.toString(), ans)
            }
        }
        row.put(COL_MARKS, 1)
        row.put(COL_CORRECTANS, question.correctAnswer)
        row.put(COL_IMAGE, question.image)

        db.insert(tableName, null, row)
    }
}