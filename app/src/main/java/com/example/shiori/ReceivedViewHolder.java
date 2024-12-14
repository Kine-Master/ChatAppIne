package com.example.shiori;

import android.view.View;
import android.widget.TextView;

public class ReceivedViewHolder extends BaseViewHolder {

    TextView Receive;

    public ReceivedViewHolder(View itemView) {
        super(itemView);
        Receive = itemView.findViewById(R.id.Receive);
    }

    @Override
    public void bind(Message message) {
        Receive.setText(message.getContent());
    }
}
