package com.example.chatapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import io.noties.markwon.Markwon;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private Context context;
    private List<Message> messages;
    private Markwon markwon;

    public ChatAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
        this.markwon = Markwon.create(context);
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isSent() ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_SENT) {
            view = LayoutInflater.from(context).inflate(R.layout.item_message_sent, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_message_received, parent, false);
        }
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = messages.get(position);

        if (message.isSent()) {
            // For sent messages, just set the text directly
            holder.messageTextView.setText(message.getMessage());
        } else {
            // For received messages (bot), use Markwon to render Markdown
            markwon.setMarkdown(holder.messageTextView, message.getMessage());
        }

        // Log the type of message being processed
        Log.d("ChatAdapter", "Binding message at position " + position + ": " + message.getMessage());

        // Set the timestamp
        if (holder.messageTime != null) {
            String time = new SimpleDateFormat("hh:mm a", Locale.getDefault())
                    .format(new Date(message.getTimestamp()));
            holder.messageTime.setText(time);
        }
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView messageTime;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.message_text_view);
            messageTime = itemView.findViewById(R.id.message_time);
        }
    }
}

