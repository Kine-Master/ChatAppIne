package com.example.shiori;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ContactActivity extends AppCompatActivity implements OnContactClickListener{
    RecyclerView listOfContact;
    List<Contact> contactList;
    String studentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        studentID = getIntent().getStringExtra("studentNum");
        listOfContact = findViewById(R.id.ViewContact);
        listOfContact.setLayoutManager(new LinearLayoutManager(this));

         //String studentNum = getIntent().getStringExtra("studentNum");

         //getContacts("223306");

         if (studentID != null){
             getContacts(studentID);
         }else {
             Toast.makeText(this,"error", Toast.LENGTH_SHORT).show();
         }

    }
    private void getContacts(String studentID) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.3:5000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiAccess apiAccess = retrofit.create(ApiAccess.class);

        Call<List<Contact>> call = apiAccess.getContacts(studentID);

        call.enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                Log.d("API Response", response.body().toString());

                if (response.isSuccessful() && response.body() != null){
                    contactList = response.body();

                    ContactListRecyclerViewAdapter adapter = new ContactListRecyclerViewAdapter(contactList,
                            ContactActivity.this, ContactActivity.this);
                    listOfContact.setAdapter(adapter);
                }else {
                    Log.d("API Response", "Response body is null");
                }
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                Toast.makeText(ContactActivity.this, "ERROR" + t.getMessage(),Toast.LENGTH_SHORT).show();
                Log.e("API Error", "onFailure: " + t.getMessage());
            }
        });
    }

    @Override
    public void onItemClick(Contact contact) {
        Intent intent = new Intent(this, ActivityChat.class);
        //intent.putExtra("contactName", data.getName());


        intent.putExtra("ReceiverName", contact.getName());
        intent.putExtra("receiverID", contact.getStudentID());
        intent.putExtra("senderID", studentID);
        startActivity(intent);
    }
}