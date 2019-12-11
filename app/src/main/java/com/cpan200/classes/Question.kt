package com.cpan200.classes

import java.sql.Blob

class Question(
    val id: Int? = null,
    var question: String = "",
    var answers: MutableList<String> = mutableListOf(),
    var correctAnswer: Int? = null,

    var image: ByteArray? = null)