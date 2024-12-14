package com.example.shiori;

import static java.util.Collection.*;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityChat extends AppCompatActivity {

    private RecyclerView recyclerViewInchtact;
    private TextView textView9;
    private EditText msgTXT;
    private Button sendBTN;
    private Arrays Collections;

    List<Message> messages;
    DisConRecyclerView adapter;

    private String receivername;
    private String sender;
    private String receiver;
    private String name;
    private ApiAccess apiAccess;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textView9 = findViewById(R.id.textView9);
        recyclerViewInchtact = findViewById(R.id.recyclerViewInchtact);
        msgTXT = findViewById(R.id.msgTXT);
        sendBTN = findViewById(R.id.sendBTN);

        messages = new ArrayList<>();
        adapter = new DisConRecyclerView(messages, ActivityChat.this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerViewInchtact.setLayoutManager(layoutManager);
        recyclerViewInchtact.setAdapter(adapter);

        //recyclerViewInchtact = getIntent().getStringExtra("studentNum").trim();
        receivername = getIntent().getStringExtra("ReceiverName").trim();
        sender = getIntent().getStringExtra("senderID").trim();
        receiver = getIntent().getStringExtra("receiverID").trim();
        //name = getIntent().getStringExtra("Name").trim();

        textView9.setText(receivername);

        setupRetrofit();

        loadMessagesFromServer();

        sendbtn();


        //String contactName = getIntent().getStringExtra("contactName");
        //if (contactName != null){
        //    textView9.setText("to:" + contactName);
        //}
    }
    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.3:5000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
       apiAccess = retrofit.create(ApiAccess.class);
    }

    private void loadMessagesFromServer() {
        Call<List<Message>> call = apiAccess.getMessages(sender, receiver);
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null){
                    List<Message> MessageFromSender = response.body();

                    Call<List<Message>> call1 = apiAccess.getMessages(receiver, sender);
                    call1.enqueue(new Callback<List<Message>>() {
                        @Override
                        public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                            if (response.isSuccessful() && response.body() != null){
                                List<Message> MessageFromReceiver = response.body();

                                messages.clear();
                                messages.addAll(MessageFromSender);
                                messages.addAll(MessageFromReceiver);

                                //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                //    messages.sort((m, m1) -> Integer.compare(m.getMessageId(), m1.getMessageId()));
                                //}
////                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
//                                    messages.sort((m1, m2) -> Integer.compare(m1.getMessageId(), m2.getMessageId()));
                                messages.sort((m1, m2) -> Integer.compare(m1.getMessageId(), m2.getMessageId()));

                                for (Message message : messages){
                                    //message.setisSent(sender.equals(message.getFrom)));
                                    message.setSent(sender.equals(message.getFrom()));
                                }

                                Log.d("Response", "response" + sender + receiver + messages);

                                adapter.notifyDataSetChanged();
                                recyclerViewInchtact.scrollToPosition(messages.size() - 1);
                            } else {
                                showError("Error fetching receiver messages: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Message>> call, Throwable t) {
                            Toast.makeText(ActivityChat.this, "basta error kay tanga ka", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Toast.makeText(ActivityChat.this, "error nanaman", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendbtn() {
        sendBTN.setOnClickListener(view -> {
            String messageCONTENT = msgTXT.getText().toString().trim();

            if (TextUtils.isEmpty(messageCONTENT)){
                Toast.makeText(this, "Message empty", Toast.LENGTH_SHORT).show();
                return;
            }

            sendBTN.setEnabled(false);
            Message newMessage = new Message(0, messageCONTENT, true, true, sender, receiver);
            messages.add(newMessage);
            adapter.notifyItemInserted(messages.size() - 1);
            recyclerViewInchtact.scrollToPosition(messages.size() - 1);

           msgTXT.setText("");
           sendMessageToServer(messageCONTENT);
        });
    }

    private void sendMessageToServer(String messageCONTENT) {
        Call<Message> call = apiAccess.sendMessage(sender, receiver, messageCONTENT);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                sendBTN.setEnabled(true);
                if (response.isSuccessful() && response.body() != null){
                    Toast.makeText(ActivityChat.this, "Message Successful", Toast.LENGTH_SHORT).show();
                }else {
                    showError("failed to sent");
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                sendBTN.setEnabled(true);
                showError("No internet" + t.getMessage());
            }
        });
    }

    private void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }
}