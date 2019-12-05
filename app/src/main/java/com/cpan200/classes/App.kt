package com.cpan200.classes

import android.content.Context
import android.widget.Toast

class App {
    companion object{
        var isLoggedIn: Boolean = false
        var currentUser: User? = null

        fun login(username: String, password: String) : User? {

        }

        fun logout(){}

        fun showToast(context: Context, msg: String, length: Int = Toast.LENGTH_LONG){
            Toast.makeText(context, msg, length).show()
        }
    }
}