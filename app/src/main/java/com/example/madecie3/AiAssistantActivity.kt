package com.example.madecie3

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madecie3.ai.*
import kotlinx.coroutines.launch

class AiAssistantActivity : AppCompatActivity() {

    private val messages = mutableListOf<AiMessage>()
    private lateinit var adapter: ChatAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_assistant)

        recyclerView = findViewById(R.id.chatRecyclerView)
        statusText   = findViewById(R.id.chatStatus)
        val input    = findViewById<EditText>(R.id.messageInput)
        val sendBtn  = findViewById<ImageButton>(R.id.sendBtn)

        adapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Initial welcome message
        addMessage(AiMessage("assistant", "Hello! I am your Logistics Assistant. Ask me about your shipments, rates, or available products."))

        sendBtn.setOnClickListener {
            val query = input.text.toString().trim()
            if (query.isNotEmpty()) {
                input.text.clear()
                handleQuery(query)
            }
        }
    }

    private fun handleQuery(query: String) {
        addMessage(AiMessage("user", query))
        
        statusText.text = "Thinking..."
        statusText.setTextColor(getColor(R.color.accent_red))

        lifecycleScope.launch {
            try {
                // 1. Build context
                val systemPrompt = AiContextManager.buildSystemPrompt(this@AiAssistantActivity)
                
                // 2. Prepare request (System context + all history)
                val conversation = mutableListOf<AiMessage>()
                conversation.add(AiMessage("system", systemPrompt))
                conversation.addAll(messages)

                val request = AiRequest(messages = conversation)

                // 3. Call API
                val response = AiClient.api.getCompletion(AiClient.getAuthHeader(), request)

                if (response.isSuccessful && response.body() != null) {
                    val aiReply = response.body()!!.choices.firstOrNull()?.message?.content ?: "I'm sorry, I couldn't process that."
                    addMessage(AiMessage("assistant", aiReply))
                } else {
                    addMessage(AiMessage("assistant", "Error: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                addMessage(AiMessage("assistant", "Network failure: ${e.localizedMessage}"))
            } finally {
                statusText.text = "Online"
                statusText.setTextColor(getColor(R.color.color_success))
            }
        }
    }

    private fun addMessage(message: AiMessage) {
        messages.add(message)
        adapter.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)
    }
}
