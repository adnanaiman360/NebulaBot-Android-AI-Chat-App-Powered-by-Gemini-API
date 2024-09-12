package com.example.chatapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> messages;  // List of Message objects
    private EditText inputMessage;
    private ImageButton sendButton;

    // Firebase Database reference
    private DatabaseReference chatDatabase;

    private DatabaseHelper databaseHelper;  // DatabaseHelper instance

    // Initialize GenerativeModel for Gemini API
    private GenerativeModel generativeModel;
    private Executor executor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        // Initialize Firebase database
        chatDatabase = FirebaseDatabase.getInstance().getReference("chats"); // "chats" is the Firebase node

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Initialize GenerativeModel and Executor
        generativeModel = new GenerativeModel(
                "gemini-1.5-flash", // Replace with the appropriate model name if different
                BuildConfig.API_KEY // Use the API key from BuildConfig
        );
        executor = Executors.newSingleThreadExecutor();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize message list from database
        messages = databaseHelper.getAllMessages();

        // Debug or log the message list
        for (Message message : messages) {
            Log.d("ChatActivity", "Message: " + message.getMessage() + ", Sent: " + message.isSent());
        }

        // Add initial bot messages if the list is empty
        if (messages.isEmpty()) {
            messages.add(new Message("Hi! How can I assist you?", false, System.currentTimeMillis()));  // Bot message
        }

        // Initialize adapter
        chatAdapter = new ChatAdapter(this, messages);
        recyclerView.setAdapter(chatAdapter);

        // Initialize input field and button
        inputMessage = findViewById(R.id.input_message);
        sendButton = findViewById(R.id.button_send);

        // Set onClickListener for the send button
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMessage = inputMessage.getText().toString().trim();
                if (!userMessage.isEmpty()) {
                    // Add the user message to the message list with the current timestamp
                    long timestamp = System.currentTimeMillis();  // Get the current time
                    Message message = new Message(userMessage, true, timestamp);
                    messages.add(message);

                    // Save message to local SQLite database
                    databaseHelper.addMessage(userMessage, true, timestamp);

                    // Save user message to Firebase
                    chatDatabase.push().setValue(message);

                    // Update the adapter
                    chatAdapter.notifyItemInserted(messages.size() - 1);

                    // Scroll to the last message
                    recyclerView.scrollToPosition(messages.size() - 1);

                    // Clear the input field
                    inputMessage.setText("");

                    // Generate a bot response
                    generateBotResponse(userMessage);
                }
            }
        });
    }

    // Method to concatenate chat history for context
    private String getChatHistory() {
        StringBuilder chatHistory = new StringBuilder();

        // Iterate over the past messages (can limit the number of messages for brevity)
        for (Message message : messages) {
            if (message.isSent()) {
                chatHistory.append("User: ").append(message.getMessage()).append("\n");
            } else {
                chatHistory.append("Bot: ").append(message.getMessage()).append("\n");
            }
        }
        return chatHistory.toString();
    }

    // Generate bot response based on the user query using Gemini API
    private void generateBotResponse(String userMessage) {
        // Append user message to chat history
        String chatHistory = getChatHistory();
        String fullContext = chatHistory + "User: " + userMessage;

        Content content = new Content.Builder()
                .addText(fullContext)  // Send the conversation context to Gemini
                .build();

        ListenableFuture<GenerateContentResponse> response =
                GenerativeModelFutures.from(generativeModel).generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String botResponse = result.getText();

                // Add bot response to message list with the current timestamp
                long timestamp = System.currentTimeMillis();
                Message message = new Message(botResponse, false, timestamp);
                messages.add(message);

                // Save bot response to local SQLite database
                databaseHelper.addMessage(botResponse, false, timestamp);

                // Save bot response to Firebase
                chatDatabase.push().setValue(message);

                // Update the adapter on the main thread
                runOnUiThread(() -> {
                    chatAdapter.notifyItemInserted(messages.size() - 1);
                    recyclerView.scrollToPosition(messages.size() - 1);
                });
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();  // Handle errors
            }
        }, executor);
    }
}
