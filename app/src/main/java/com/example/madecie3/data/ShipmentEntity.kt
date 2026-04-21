package com.example.madecie3.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shipments")
data class ShipmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sender: String,
    val receiver: String,
    val pickupAddress: String,
    val deliveryAddress: String,
    val weight: Double,
    val cost: Int,
    val trackingId: String,
    val paymentMethod: String,
    val timestamp: Long = System.currentTimeMillis()
)
