package com.example.shiori;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ContactListViewHolder extends RecyclerView.ViewHolder {
    TextView contacts;

    public ContactListViewHolder(@NonNull View itemView){
        super(itemView);
        contacts = itemView.findViewById(R.id.textView8);
    }
}
