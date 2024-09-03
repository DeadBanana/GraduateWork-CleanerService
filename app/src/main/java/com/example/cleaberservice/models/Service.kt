package com.example.cleaberservice.models

class Service(var id: String, var name: String, var description: String) {

    constructor() : this(ID, NAME, DESCRIPTION)

    companion object PATH {
        const val ROOT = "Service"
        const val ID = "id"
        const val NAME = "name"
        const val DESCRIPTION = "description"
    }
}