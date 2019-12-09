package com.cpan200.dbclasses

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
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
        if (cursor == null || cursor.count == 0){
            return 0
        }
        else {
            if (cursor.moveToFirst()){
                val n: Int = cursor.getInt(0)
                cursor.close()
                return n
            }
        }
        return 0
    }

    fun getAllRows() : Cursor? {
        return this.readableDatabase.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    fun findExistingUser(tryUsername: String): Cursor?{
        return this.readableDatabase.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COL_USERNAME = \"$tryUsername\"", null)

    }

    fun deleteRow(username: String){
        this.writableDatabase.execSQL("DELETE FROM $TABLE_NAME WHERE $COL_USERNAME = \"$username\"")
    }

    fun updateUserInfo(username: String, password: String, email: String?, firstName: String?, lastName: String?){
        val row = ContentValues()
        row.put(COL_PASSWORD, password)
        row.put(COL_EMAIL, email)
        row.put(COL_FIRSTNAME, firstName)
        row.put(COL_LASTNAME, lastName)
        this.readableDatabase.update(TABLE_NAME, row, "$COL_USERNAME = $username", null)
    }

    fun updateUserStatus(username: String, status: User.UserStatus){
        val row = ContentValues()
        row.put(COL_STATUS, status.toString())
        this.readableDatabase.update(TABLE_NAME, row, "$COL_USERNAME = $username", null)
    }

    fun deleteQuizCol(id: Int){
        //todo del quiz col
    }

    fun createQuizCol(id: Int){
        //todo create quiz cols
    }

    fun updateScoreAttempt(username: String, id: Int, score: Double, attempt: Int){
        //todo record a score


        //todo add 1 to attempts
    }

    fun getScoreAttempt(username: String, id: Int): Cursor? {
        //todo
    }

}