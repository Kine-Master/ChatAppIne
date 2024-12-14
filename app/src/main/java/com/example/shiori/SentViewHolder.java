package com.example.shiori;

import android.view.View;
import android.widget.TextView;

public class SentViewHolder extends BaseViewHolder {

    TextView Sent;

    public SentViewHolder(View itemView) {
        super(itemView);
        Sent = itemView.findViewById(R.id.Sent);
    }

    @Override
    public void bind(Message message) {
        Sent.setText(message.getContent());
    }
}

