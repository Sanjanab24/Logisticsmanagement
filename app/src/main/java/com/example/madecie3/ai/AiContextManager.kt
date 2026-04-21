package com.example.madecie3.ai

import android.content.Context
import com.example.madecie3.api.RetrofitClient
import com.example.madecie3.data.AppDatabase
import java.text.SimpleDateFormat
import java.util.*

object AiContextManager {

    suspend fun buildSystemPrompt(context: Context): String {
        val sb = StringBuilder()
        sb.append("You are the ShipWise Logistics AI Assistant. Your goal is to help users manage their shipments, track orders, and estimate costs.\n\n")

        // 1. App Knowledge
        sb.append("APP CONTEXT & RULES:\n")
        sb.append("- App Name: ShipWise Logistics\n")
        sb.append("- Shipping Rates: ₹50 per Kilogram (KG) for all zones.\n")
        sb.append("- Tracking IDs: User-created shipments start with 'TRK' followed by numbers.\n")
        sb.append("- Current Date: ${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())}\n\n")

        // 2. Fetch Inventory (Products)
        try {
            val productResponse = RetrofitClient.api.getProducts()
            if (productResponse.isSuccessful && productResponse.body() != null) {
                sb.append("CURRENT PRODUCT INVENTORY:\n")
                productResponse.body()!!.take(10).forEach { p ->
                    sb.append("- ID ${p.id}: ${p.title} ($${p.price}) [Category: ${p.category}]\n")
                }
                sb.append("\n")
            }
        } catch (e: Exception) {
            sb.append("Inventory data is currently unavailable via API.\n\n")
        }

        // 3. Fetch User Shipment Memory (Local DB)
        try {
            val db = AppDatabase.getDatabase(context)
            val shipments = db.shipmentDao().getAllShipments()
            if (shipments.isNotEmpty()) {
                sb.append("USER'S SHIPMENT HISTORY (Local Memory):\n")
                shipments.take(15).forEach { s ->
                    val date = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(s.timestamp))
                    sb.append("- [$date] Tracking ${s.trackingId}: ${s.weight}kg from ${s.sender} to ${s.receiver}. Cost: ₹${s.cost}. Method: ${s.paymentMethod}.\n")
                }
                sb.append("\n")
            } else {
                sb.append("The user has not created any shipments yet.\n\n")
            }
        } catch (e: Exception) {
            sb.append("Local shipment history is currently unavailable.\n\n")
        }

        sb.append("STRICT INSTRUCTIONS:\n")
        sb.append("- DO NOT USE EMOJIS. Maintain a professional, high-end logistics tone.\n")
        sb.append("- Keep responses concise and human-like. Avoid long paragraphs.\n")
        sb.append("- If the user asks to create/book a shipment, confirm the action in text AND include a hidden action tag at the end of your response exactly like this: [ACTION:CREATE_SHIPMENT|SENDER:name|RECEIVER:name|PICKUP:addr|DELIVERY:addr|WEIGHT:num]\n")
        sb.append("- Do not use emojis in the action tag either.")
        
        return sb.toString()
    }
}
