package com.cpan200.dbclasses

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.cpan200.classes.User

class UserDB(
    context: Context?,
    //name: String?,
    factory: SQLiteDatabase.CursorFactory?
    //version: Int
) : SQLiteOpenHelper(context, filename, factory, version) {

    companion object {
        private const val version = 1
        private const val filename = "Users.db"
        const val TABLE_NAME = "users"
        const val COL_ID = "_id"
        const val COL_USERNAME = "username"
        const val COL_PASSWORD = "password"
        const val COL_STATUS = "status"
        const val COL_EMAIL = "email"
        const val COL_FIRSTNAME = "firstname"
        const val COL_LASTNAME = "lastname"
        const val COL_QUIZN = "quiz"
        const val COL_ATTEMPTN = "attempts"

    }
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $TABLE_NAME (" +
                "$COL_ID INTEGER PRIMARY KEY, " +
                "$COL_USERNAME TEXT, " +
                "$COL_PASSWORD TEXT, " +
                "$COL_STATUS TEXT, " +
                "$COL_EMAIL TEXT, " +
                "$COL_FIRSTNAME TEXT, " +
                "$COL_LASTNAME TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun tryLogin(username: String, password: String): Cursor?{
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COL_USERNAME = \"$username\" AND $COL_PASSWORD = \"$password\"", null)

    }

    fun addRow(username: String, password: String, status: String, email: String?, firstName: String?, lastName: String?){
        val row = ContentValues()
        val db = this.writableDatabase
        row.put(COL_USERNAME, username)
        row.put(COL_PASSWORD, password)
        row.put(COL_STATUS, status)
        row.put(COL_EMAIL, email)
        row.put(COL_FIRSTNAME, firstName)
        row.put(COL_LASTNAME, lastName)
        db.insert(TABLE_NAME, null, row)
        db.close()
    }

    fun rows(): Int{
        //returns number of rows including deleted rows. uses primary key.
        val db = this.writableDatabase
        val cursor: Cursor? = db.rawQuery("SELECT MAX($COL_ID) FROM $TABLE_NAME", null)
        when (cursor!!.count){
            0 -> return 0
            else ->{
                Log.i("test123", "counting rows")
                cursor.moveToNext()
                val n: Int = cursor.getInt(1)
                cursor.close()
                Log.i("test123", "rows: $n")
                return n
            }
        }

    }


}