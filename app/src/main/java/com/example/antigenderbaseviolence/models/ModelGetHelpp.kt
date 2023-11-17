package com.example.antigenderbaseviolence.models

class ModelGetHelpp {

    var id:String = ""
    var uid:String = ""
    var organisationName:String = ""
    var organisationDescription:String = ""
    var organisationTelephoneNumber:String = ""
    var organisationEmail:String = ""
    var organisationProfileImage:String =""
    var timestamp:Long = 0

    constructor()
    constructor(
        id: String,
        uid: String,
        organisationName: String,
        organisationDescription: String,
        organisationTelephoneNumber: String,
        organisationEmail: String,
        organisationProfileImage: String,
        timestamp: Long
    ) {
        this.id = id
        this.uid = uid
        this.organisationName = organisationName
        this.organisationDescription = organisationDescription
        this.organisationTelephoneNumber = organisationTelephoneNumber
        this.organisationEmail = organisationEmail
        this.organisationProfileImage = organisationProfileImage
        this.timestamp = timestamp
    }

}