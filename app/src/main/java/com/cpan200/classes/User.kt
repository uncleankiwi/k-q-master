package com.cpan200.classes

abstract class User(
    var name: String,
    var password: String) {

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

}