package com.example.antigenderbaseviolence.models

class ModelForums {

    var id:String = ""
    var uid:String =""
    var message:String =""
    var username:String =""
    var profileImage:String =""
    var forumDate:String = ""
    var reports:Int? = 0
    var timestamp:Long = 0
    var userType:String = ""

    constructor()
    constructor(
        id: String,
        uid: String,
        message:String,
        username:String,
        profileImage:String,
        forumDate:String,
        reports:Int?,
        timestamp: Long,
        userType: String
    ) {
        this.id = id
        this.uid = uid
        this.message = message
        this.username = username
        this.profileImage = profileImage
        this.forumDate = forumDate
        this.reports = reports
        this.timestamp = timestamp
        this.userType = userType
    }
}