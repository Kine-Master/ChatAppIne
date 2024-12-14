package com.example.shiori;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Messages extends AppCompatActivity implements OnConversationClickListener {

    Button button;
    Button logoutButton; //adi an bago
    RecyclerView listReceiveMessages;
    TextView textViewUserName;
    String studentId;

    String studentID;
    String name;
    EditText editTextNumber;
    List<Contact> contactList;
    List<Conversation> conversations = new ArrayList<>();
    ApiAccess apiAccess;
    ImageView imageView;
    ConversationListRecyclerViewAdapter adapter;

    //SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_messages);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        studentID = getIntent().getStringExtra("studentNum");
        Log.d("Student Number", "ID: " + studentID);

        name = getIntent().getStringExtra("Name");

        textViewUserName = findViewById(R.id.textViewUserName);

        textViewUserName.setText(getIntent().getStringExtra("Name"));

        listReceiveMessages = findViewById(R.id.recyclerViewMessages);
        listReceiveMessages.setLayoutManager(new LinearLayoutManager(this));

//        RecyclerView.LayoutManager lm = new LinearLayoutManager(getApplicationContext());
//        lm.setItemPrefetchEnabled(true);
//        listReceiveMessages.setLayoutManager(lm);

        adapter = new ConversationListRecyclerViewAdapter(conversations, Messages.this);

        adapter.conversations = new ArrayList<>();
        //adapter.conversations = GetConversations();

        listReceiveMessages.setAdapter(adapter);

        button = findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String studentNum = getIntent().getStringExtra("studentNum");
                //startActivity(new Intent(Messages.this, ContactActivity.class));
                Intent intent = new Intent(Messages.this, ContactActivity.class);
                intent.putExtra("studentNum", studentNum);
                startActivity(intent);
            }
        });
        logoutButton = findViewById(R.id.logout_btn); 
        SharedPreferences account = getSharedPreferences("UserInfo", MODE_PRIVATE);
        studentId = account.getString("studentNum", "");

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear session data
                SharedPreferences.Editor editor = account.edit();
                editor.remove("StudentID");
                editor.apply();

                // Redirect to LoginActivity
                Intent intent = new Intent(Messages.this, MainActivity.class);
                startActivity(intent);

                // Finish the current activity to prevent back navigation
                finish();
            }
        });

        setupRetrofit();
        loadMessages();

    }
    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.3:5000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiAccess = retrofit.create(ApiAccess.class);
    }

    private void loadMessages() {
        Call<List<Contact>> call = apiAccess.getContacts(studentID);
        call.enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    contactList = response.body();

                    for (Contact contact : contactList) {
                        fetchMessages(contact);
                    }
                } else {
                    Toast.makeText(Messages.this, "No contacts available.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                Toast.makeText(Messages.this, "No contacts available.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchMessages(Contact contact) {
        Call<List<Message>> call = apiAccess.getMessages(studentID, contact.getStudentID().trim());
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null){
                    List<Message> MessageFromSender = response.body();

                    Call<List<Message>> call1 = apiAccess.getMessages(contact.getStudentID().trim(), studentID);
                    call1.enqueue(new Callback<List<Message>>() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                            if (response.isSuccessful() && response.body() != null){
                                List<Message> MessageFromReceiver = response.body();

                                List<Message> combinedMessages = new ArrayList<>();
                                combinedMessages.addAll(MessageFromSender);
                                combinedMessages.addAll(MessageFromReceiver);

                                //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                //    combinedMessages.sort((m, m1) -> Integer.compare(m.getMessageId(), m1.getMessageId()));
                                //}

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    combinedMessages.sort(  new Comparator<Message>() {
                                        @Override
                                        public int compare(Message m, Message m1) {
                                            return Integer.compare(m.getMessageId(), m1.getMessageId());
                                        }
                                    });
                                }

                                if (!combinedMessages.isEmpty()) {
                                    Message latestMessage = combinedMessages.get(0);
                                    addConversation(contact, latestMessage);
                                }
                            } else {
                                Toast.makeText(Messages.this, "Error fetching receiver messages: ", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Message>> call, Throwable t) {
                            Toast.makeText(Messages.this, "Network Error: ", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Toast.makeText(Messages.this, "error nanaman", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addConversation(Contact contact, Message message) {
        Conversation conversation = new Conversation();
        conversation.contact = contact;
        conversation.messages = new ArrayList<>();
        conversation.messages.add(message);

        conversations.add(conversation);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            conversations.sort((m1, m2) -> {
                Message lastMessage = m1.messages.get(m1.messages.size() - 1);
                Message lastMessage1 = m2.messages.get(m2.messages.size() - 1);

                return Integer.compare(lastMessage.getMessageId(), lastMessage1.getMessageId());
            });
        }

        adapter.conversations = conversations;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onConversationClick(Conversation conversation) {
        Intent intent = new Intent(this, ActivityChat.class);

        intent.putExtra("ReceiverName", conversation.contact.getName());
        intent.putExtra("receiverID", conversation.contact.getStudentID());
        intent.putExtra("senderID", studentId);
        //intent.putExtra("setAdapter",adapter.toString());
        startActivity(intent);
    }


//    private List<Conversation> GetConversations()
//    {
//        List<Conversation> conversations = new ArrayList<>();
//
//        Message message;
//        Contact contact;
//        Conversation conversation;
//
//        contact = new Contact();
//        contact.Name = "Helene Potot";
//        contact.StudentID = "223172";
//
//        message = new Message();
//        message.message = "kaon na";
//        message.time = new GregorianCalendar(2024, 10, 11);
//        message.messageId = 1;
//
//        conversation = new Conversation();
//        conversation.contact = contact;
//        conversation.messages.add(message);
//
//        conversations.add(conversation);
//
//        contact = new Contact();
//        contact.Name = "Mary Picardal";
//        contact.StudentID = "223131";
//
//        message = new Message();
//        message.message = "Hain ka shi?";
//        message.time = new GregorianCalendar(2024, 12, 25);
//        message.messageId = 1;
//
//        conversation = new Conversation();
//        conversation.contact = contact;
//        conversation.messages.add(message);
//
//        conversations.add(conversation);
//
//        return conversations;
//    }
}