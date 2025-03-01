package com.cpan200.classes

class Quiz(
		var id: Int? = null,
		var title: String = "(New quiz)",
		var questions: Int = 1,
		var totalMarks: Int = 1,
		var finalized: Boolean = false,
		var maxOptions: Int = 5,
		var maxAttempts: Int = 1,
		var questionList: MutableList<Question> = mutableListOf(Question()))