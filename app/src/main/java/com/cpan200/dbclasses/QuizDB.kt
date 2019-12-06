package com.cpan200.dbclasses

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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

    val tableName = "quiz${quizID}"


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
}