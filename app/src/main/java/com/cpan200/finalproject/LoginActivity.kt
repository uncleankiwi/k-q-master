package com.cpan200.finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.cpan200.finalproject.login_fragments.FragLogin
import com.cpan200.finalproject.login_fragments.FragOnboarding
import kotlinx.android.synthetic.main.fragment_login.*



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

//    override fun onBackPressed() {
//        super.onBackPressed()
//        //go back from onboarding fragment to login if onboarding is shown
//        val transaction = supportFragmentManager.beginTransaction()
//        this.
//
//    }


}
