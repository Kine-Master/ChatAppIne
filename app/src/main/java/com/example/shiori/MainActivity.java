package com.example.shiori;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ApiAccess apiAccess;
    private EditText inputStudentNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button button = findViewById(R.id.button);
        inputStudentNum = findViewById(R.id.editTextNumber);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.3:5000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiAccess = retrofit.create(ApiAccess.class);

        SharedPreferences account = getSharedPreferences("UserInfo", MODE_PRIVATE);
        String StudentID = account.getString("StudentID", "");

        if (!StudentID.isEmpty()) {
            CallMessages(StudentID);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String studentNum = inputStudentNum.getText().toString();
                if (studentNum.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Required Student ID.", Toast.LENGTH_SHORT).show();
                } else {
                    Call<Contact> call = apiAccess.getStudentName(studentNum.trim());
                    call.enqueue(new Callback<Contact>() {
                        @Override
                        public void onResponse(Call<Contact> call, Response<Contact> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Contact contact = response.body();

                                SharedPreferences.Editor editor = account.edit();
                                editor.putString("StudentID", contact.StudentID);
                                editor.apply();

                                Intent intent = new Intent(MainActivity.this, Messages.class);
                                intent.putExtra("studentNum", studentNum);
                                intent.putExtra("Name", contact.Name);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onFailure(Call<Contact> call, Throwable t) {
                            startActivity(new Intent(MainActivity.this, Messages.class)
                                    .putExtra("Name", "Error"));
                        }
                    });
                }
            }
        });
    }

    private void CallMessages(String StudentID) {
        Call<Contact> call = apiAccess.getStudentName(StudentID);

        call.enqueue(new Callback<Contact>() {
            @Override
            public void onResponse(Call<Contact> call, Response<Contact> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Contact contact = response.body();

                    startActivity(new Intent(MainActivity.this, Messages.class)
                            .putExtra("Name", contact.Name)
                            .putExtra("StudentID", contact.StudentID)
                    );
                }
            }

            @Override
            public void onFailure(Call<Contact> call, Throwable t) {
                // failure if necessary
            }
        });
    }
}
