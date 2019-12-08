package com.cpan200.classes

class Quiz(
		var title: String? = null,
		var questions: Int? = null,
		var totalMarks: Int? = null,
		var finalized: Boolean? = null,
		var maxOptions: Boolean? = null,
		var maxAttempts: Boolean? = null,
		var questionList: MutableList<Question>? = null)