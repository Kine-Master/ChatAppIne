package com.example.shiori;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DisConRecyclerView extends RecyclerView.Adapter<BaseViewHolder> {

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    private List<Message> messages;
    Context context;

    public DisConRecyclerView(List<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_sent_message, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_received_message, parent, false);
            return new ReceivedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        Message message = messages.get(position);
//        if (message != null) {
//            holder.bind(message);
//        }
        holder.bind(message);
    }

    public int getItemViewType(int position) {
        Message message = messages.get(position);

        return message.isSent() ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessage(Message newMessage){
        messages.add(newMessage);
        notifyItemInserted(messages.size() - 1);
    }
}
