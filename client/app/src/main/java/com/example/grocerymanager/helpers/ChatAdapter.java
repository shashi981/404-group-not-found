package com.example.grocerymanager.helpers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grocerymanager.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatMessage> chatMessages;
    final static String TAG = "ChatAdapter";


    //CHAT GPT USAGE: NO
    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    //CHAT GPT USAGE: NO
    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        if (chatMessage.getFromUserFlag() == 1) {
            return 1; // User message
        } else {
            return 0; // Dietician message
        }
    }

    //CHAT GPT USAGE: FULL
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) { // User message
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_message, parent, false);
            return new UserViewHolder(view);
        } else { // Dietician message
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dietician_message, parent, false);
            return new DieticianViewHolder(view);
        }
    }

    //CHAT GPT USAGE: PARTIAL
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        if (holder.getItemViewType() == 1) { // User message
            ((UserViewHolder) holder).bind(chatMessage);
        } else { // Dietician message
            ((DieticianViewHolder) holder).bind(chatMessage);
        }
    }

    //CHAT GPT USAGE: NO
    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    // ViewHolder for user messages
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView userMessageText;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userMessageText = itemView.findViewById(R.id.tvUserMessage);
        }

        public void bind(ChatMessage chatMessage) {
            userMessageText.setText(chatMessage.getMessage());
        }
    }

    // ViewHolder for dietician messages
    //CHAT GPT USAGE: NO
    public static class DieticianViewHolder extends RecyclerView.ViewHolder {
        private TextView dieticianMessageText;

        public DieticianViewHolder(@NonNull View itemView) {
            super(itemView);
            dieticianMessageText = itemView.findViewById(R.id.tvDieticianMessage);
        }

        public void bind(ChatMessage chatMessage) {
            dieticianMessageText.setText(chatMessage.getMessage());
        }
    }
}
