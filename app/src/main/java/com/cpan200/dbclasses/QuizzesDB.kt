package com.cpan200.dbclasses

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.cpan200.classes.Question

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
        val row = ContentValues()
        val db = this.writableDatabase
        row.put(COL_QUESTIONS, 0)
        row.put(COL_TOTALMARKS, 0)
        row.put(COL_FINALIZED, 0)
        row.put(COL_MAXOPTIONS, 5)
        row.put(COL_MAXATTEMPTS, 1)
        db.insert(TABLE_NAME, null, row)
        db.close()
    }

    fun updateRow(id: Int, title: String?, questionList: MutableList<Question>?, finalized: Boolean?, maxAttempts: Int?){
        val row = ContentValues()
        val db = this.writableDatabase
        row.put(COL_TITLE, title)
        row.put(COL_QUESTIONS, questionList?.size)
        row.put(COL_TOTALMARKS, questionList?.size)
        row.put(COL_FINALIZED, finalized)
        row.put(COL_MAXOPTIONS, 5)  //future feature: allow more options...
        row.put(COL_MAXATTEMPTS, maxAttempts)
        db.update(TABLE_NAME, row, "$COL_ID = $id", null)
    }


}