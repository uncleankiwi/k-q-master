package com.cpan200.classes

import android.content.Context
import android.widget.Toast
import com.cpan200.dbclasses.UserDB

class App {
    companion object{
        var isLoggedIn: Boolean = false
        var currentUser: User? = null

        fun login(context: Context, username: String, password: String) : User? {
            val userDB = UserDB(context, null)
            user: User? = userDB.tryLogin(username, password)
            return
        }

        fun logout(){}

        fun showToast(context: Context, msg: String, length: Int = Toast.LENGTH_LONG){
            Toast.makeText(context, msg, length).show()
        }
    }
}