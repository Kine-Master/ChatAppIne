package com.example.shiori;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ConversationListRecyclerViewAdapter extends RecyclerView.Adapter<ConversationListViewHolder>
{
    public List<Conversation> conversations;
    public OnConversationClickListener listener;

    public ConversationListRecyclerViewAdapter(List<Conversation> conversations, OnConversationClickListener listener) {
        this.conversations = conversations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ConversationListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_item, parent, false);
        return new ConversationListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationListViewHolder holder, int position) {

        Conversation conversation = conversations.get(position);

        holder.message.setText(conversation.getMessage());
        holder.contact.setText(conversation.contact.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onConversationClick(conversation);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (conversations == null) {
            return 0;
        }
        return conversations.size();
    }
}