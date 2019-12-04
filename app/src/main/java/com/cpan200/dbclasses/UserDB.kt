package com.cpan200.dbclasses

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDB(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?
    //version: Int
) : SQLiteOpenHelper(context, filename, factory, version) {
    companion object {
        private const val version = 1
        private const val filename = "Users.db"
        val TABLE_NAME = "users"
        val COL_ID = "_id"
        val COL_1 = "username"
        val COL_2 = "password"
        val COL_3 = "status"
        val COL_4 = "email"
        val COL_N1 = "quiz"
        val COL_N2 = "attempts"

    }
    override fun onCreate(p0: SQLiteDatabase?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}