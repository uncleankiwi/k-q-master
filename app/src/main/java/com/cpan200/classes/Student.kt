package com.cpan200.classes

class Student(name: String, password: String) : User(name, password) {
    override var status: UserStatus? = UserStatus.STUDENT

}