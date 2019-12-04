package com.cpan200.classes

import android.media.Image

class Question(
    val id: Int,
    var question: String,
    var answers: MutableList<String>,
    var correctAnswer: Int) {

    var image: Image? = null
}