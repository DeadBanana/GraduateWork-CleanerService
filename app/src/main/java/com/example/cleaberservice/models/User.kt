package com.example.cleaberservice.models

class User(var id: String, var name: String, var email: String) {
    var orders: MutableMap<String, Boolean> = mutableMapOf()
    constructor() : this(ID, NAME, EMAIL)

    companion object PATH {
        const val ROOT = "User"
        const val ID = "id"
        const val NAME = "name"
        const val EMAIL = "email"
        const val ORDERS = "orders"
    }
}