package com.cpan200.classes

class Admin(name: String, password: String) : User(name, password) {
    override var status: UserStatus? = UserStatus.ADMIN
}