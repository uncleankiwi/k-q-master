package com.cpan200.classes

class Superuser(name: String, password: String) : User(name, password) {
    override var status: UserStatus? = UserStatus.SUPERUSER
}