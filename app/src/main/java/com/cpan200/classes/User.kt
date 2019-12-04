package com.cpan200.classes

open class User {
    var name: String? = null
    var password: String? = null
    var id: Int? = null
    open var status: UserStatus? = null

    var email: String? = null
    val quizScores: MutableList<Double>? = null
    val quizAttempts: MutableList<Int>? = null

    enum class UserStatus {
        SUPERUSER,
        ADMIN,
        STUDENT
    }

    constructor()

    constructor(name: String, password: String){
        this.name = name
        this.password = password
    }

}