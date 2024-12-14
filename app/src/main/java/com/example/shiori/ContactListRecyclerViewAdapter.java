package com.example.shiori;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactListRecyclerViewAdapter extends RecyclerView.Adapter<ContactListViewHolder> {

    private List<Contact> contactList;
    private Context context;
    OnContactClickListener listener;

    public ContactListRecyclerViewAdapter(List<Contact> contactList, Context context, OnContactClickListener listener){
        this.contactList = contactList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_item, parent, false);
        return new ContactListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactListViewHolder holder, int position) {
        Contact data = contactList.get(position);
        holder.contacts.setText(data.getName());

        holder.itemView.setOnClickListener(view -> {
            listener.onItemClick(data);
        });
    }

    @Override
    public int getItemCount(){
        return contactList.size();
    }
}
