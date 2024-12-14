package com.example.shiori;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiAccess
{
    @GET("/getStudentName")
    public Call<Contact> getStudentName(@Query("value") String Value);

    @GET("/getContacts")
    public Call<List<Contact>> getContacts(@Query("studentId") String value);

    @GET("/sendMessage")
    public Call<Message> sendMessage(
      @Query("from") String from,
      @Query("to") String to,
      @Query("message")String message
    );

    @GET("/getMessages")
    public Call<List<Message>> getMessages(
      @Query("from") String from,
      @Query("to") String to
    );
}
