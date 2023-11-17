package com.example.antigenderbaseviolence.models

class ModelSendAlert {
    var id:String = ""
    var latitude:Double = 0.0
    var longitude:Double = 0.0
    var alertDate:String = ""
    var timestamp:Long =0
    var uid:String = ""
    var email:String = ""
    var userType:String = ""
    var phoneNumber:String = ""
    var name:String = ""
    var selectedAbuseType:String = ""
    var alertCategory:String=""

    constructor()
    constructor(
        id: String,
        latitude: Double,
        longitude: Double,
        alertDate:String,
        timestamp: Long,
        uid: String,
        email: String,
        userType: String,
        phoneNumber: String,
        name: String,
        selectedAbuseType: String,
        alertCategory:String,
    ) {
        this.id = id
        this.latitude = latitude
        this.longitude = longitude
        this.alertDate = alertDate
        this.timestamp = timestamp
        this.uid = uid
        this.email = email
        this.userType = userType
        this.phoneNumber = phoneNumber
        this.name = name
        this.selectedAbuseType = selectedAbuseType
        this.alertCategory = alertCategory
    }
}