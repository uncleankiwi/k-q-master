package com.cpan200.finalproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cpan200.finalproject.login_fragments.FragLogin


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //when starting the app, show login fragment
        if (savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .add(R.id.LoginContainer, FragLogin(), "FragLogin")
                .commit()
        }

    }



}
