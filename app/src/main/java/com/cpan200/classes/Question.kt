package com.cpan200.classes

class Question(
    val id: Int? = null,
    var question: String = "",
    var answers: MutableList<String> = mutableListOf(),
    var correctAnswer: Int? = null,

    var image: ByteArray? = null)