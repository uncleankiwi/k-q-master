package com.cpan200.classes

class Quiz(
		var id: Int? = null,
		var title: String? = null,
		var questions: Int? = null,
		var totalMarks: Int? = null,
		var finalized: Boolean? = null,
		var maxOptions: Int? = null,
		var maxAttempts: Int? = null,
		var questionList: MutableList<Question>? = null)