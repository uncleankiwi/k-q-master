package com.cpan200.classes

class Question(
    val id: Int? = null,
    var question: String = "(New question)",
    var answers: MutableList<String> = mutableListOf("(New answer)"),
    var correctAnswer: Int? = null,
    var optionIds: MutableList<Int> = mutableListOf(-1),

    var image: ByteArray? = null)