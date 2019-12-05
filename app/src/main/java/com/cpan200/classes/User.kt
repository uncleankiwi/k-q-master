package com.cpan200.classes

open class User(
        var name: String? = null,
        var password: String? = null,
        var id: Int? = null,
        var status: UserStatus? = null,
        var email: String? = null,
        var firstName: String? = null,
        var lastName: String? = null) {

    val quizScores: MutableList<Double>? = null
    val quizAttempts: MutableList<Int>? = null

    enum class UserStatus {
        SUPERUSER,
        ADMIN,
        STUDENT
    }



}