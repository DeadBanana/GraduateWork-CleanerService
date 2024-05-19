package com.example.cleaberservice.models

class Order(var id: String, var userId: String, var address: String, var date: Long,
            var description: String, var status: Boolean) {
    var services: MutableMap<String, Boolean> = mutableMapOf()
    var cleaners: MutableMap<String, Boolean> = mutableMapOf()

    constructor() : this(ID, USERID, ADDRESS, 0, DESCRIPTION, false)
    constructor(address: String, date: Long, description: String, status: Boolean) :
            this (ID, USERID, address, date, description, status)

    companion object PATH {
        const val ROOT = "Order"
        const val ID = "id"
        const val USERID = "userId"
        const val ADDRESS = "address"
        const val DATE = "date"
        const val DESCRIPTION = "description"
        const val STATUS = "status"
        const val SERVICES = "services"
        const val CLEANERS = "cleaners"
    }
}