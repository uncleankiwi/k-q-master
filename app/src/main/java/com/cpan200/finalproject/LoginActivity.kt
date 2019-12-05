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
                .add(R.id.LoginContainer, FragOnboarding(), "FragLogin")
                .commit()
        }

    }

//    fun showOnboarding() {
//        Log.i("test123", "button start")
//        transaction.add(R.id.LoginContainer, FragOnboarding())
//        Log.i("test123", "button end")
//        transaction.addToBackStack("LoginFragment")
//        Log.i("test123", "back stack end")
//        transaction.commit()
//        Log.i("test123", "commit end")
//
//    }
//
//
//    fun closeOnboarding(){
//        Log.i("test123", "in reg button")
//        transaction.replace(R.id.LoginContainer, FragLogin())
//        transaction.addToBackStack(null)
//        transaction.commit()
//        Log.i("test123", "after reg button")
//    }

}
