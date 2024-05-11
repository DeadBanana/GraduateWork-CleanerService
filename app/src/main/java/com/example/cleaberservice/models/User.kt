package com.example.cleaberservice.models

class User {
    var id: String
    var name: String
    var email: String

    constructor(id: String, name: String, email: String) {
        this.id = id
        this.name = name
        this.email = email
    }

    constructor() {
        id = "null"
        name = "null"
        email = "null"
    }
    companion object PATH {
        const val ROOT = "User"
        const val  ID = "id"
        const val  NAME = "name"
        const val  EMAIL = "email"
    }
}