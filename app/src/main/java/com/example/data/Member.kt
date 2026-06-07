package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "members")
data class Member(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val fatherOrHusbandName: String,
    val age: Int,
    val mobileNumber: String,
    val address: String,
    val district: String,
    val occupation: String,
    val registeredAt: Long = System.currentTimeMillis()
) : Serializable
