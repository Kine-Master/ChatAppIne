package com.example.shiori;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Message {

    public String To;
    public String From;
    public String Content;
    public int MessageId;
    public boolean isReceived;
    private boolean isSent;
    private String Timestamp;


    // Constructor with parameters
    public Message(int messageId, String content, boolean isSent, boolean isReceived, String from, String to) {
        this.MessageId = messageId;
        this.Content = content;
        this.isSent = isSent;
        this.isReceived = isReceived;
        this.From = from;
        this.To = to;
    }

    // Getter and Setter methods for better encapsulation
    public int getMessageId() {
        return MessageId;
    }

    public void setMessageId(int messageId) {
        MessageId = messageId;
    }

    public String getFrom(){
        return From.trim();
    }

    public void setFrom(String from){
        From = from;
    }

    public String getTo(){
        return To.trim();
    }

    public void setTo(String to){
        To = to;
    }

    public String getContent(){
        return Content;
    }

    public void setContent(String content){
        Content = content;
    }

    public String setTimestamp(){
        return Timestamp;
    }

    public void setTimestamp(String timestamp){
        Timestamp = timestamp;
    }

    public void setSent(boolean isSent){
        this.isSent = isSent;
    }

    public boolean isSent(){
        return isSent;
    }



}
