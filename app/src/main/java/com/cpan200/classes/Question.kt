package com.cpan200.classes

import java.sql.Blob

class Question(
    val id: Int?,
    var question: String,
    var answers: MutableList<String>?,
    var correctAnswer: Int?) {

    var image: ByteArray? = null
}