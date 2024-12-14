package com.example.shiori;

import java.util.ArrayList;
import java.util.List;

public class Conversation
{
    public int Conversation;
    public Contact contact;
    public List<Message> messages;
    public String time;

    public Conversation()
    {
        messages = new ArrayList<>();
    }

    public String getMessage()
    {
        return  messages.get(0).Content.trim();
    }
}
