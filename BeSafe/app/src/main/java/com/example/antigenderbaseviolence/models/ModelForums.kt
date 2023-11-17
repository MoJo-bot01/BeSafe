package com.example.antigenderbaseviolence.models

class ModelForums {

    var id:String = ""
    var uid:String =""
    var message:String =""
    var username:String =""
    var forumDate:String = ""
    var timestamp:Long = 0
    var userType:String = ""

    constructor()
    constructor(
        id: String,
        uid: String,
        message:String,
        username:String,
        forumDate:String,
        timestamp: Long,
        userType: String
    ) {
        this.id = id
        this.uid = uid
        this.message = message
        this.username = username
        this.forumDate = forumDate
        this.timestamp = timestamp
        this.userType = userType
    }
}