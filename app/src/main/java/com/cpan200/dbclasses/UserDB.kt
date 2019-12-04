package com.cpan200.dbclasses

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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
        const val COL_1 = "username"
        const val COL_2 = "password"
        const val COL_3 = "status"
        const val COL_4 = "email"
        const val COL_5 = "firstname"
        const val COL_6 = "lastname"
        const val COL_N1 = "quiz"
        const val COL_N2 = "attempts"

    }
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $TABLE_NAME ($COL_ID INTEGER PRIMARY KEY, $COL_1 TEXT, $COL_2 TEXT, $COL_3 TEXT, $COL_4 TEXT, $COL_5 TEXT, $COL_6 TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}