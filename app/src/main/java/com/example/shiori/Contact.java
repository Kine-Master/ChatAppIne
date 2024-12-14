package com.example.shiori;

import com.google.gson.annotations.SerializedName;

public class Contact {
    public String StudentID;
    public String Name;
    public String Contacts;
    public String StudentId;

    public String getStudentID() {
        return StudentID;
    }
    public String getName() {
        return Name.trim();
    }

    public void setName(String name) {
        Name = name;
    }

    public void setStudentId(String StudentId){
        this.Name = Name.trim();
    }

    public void setContacts(String contacts){
        this.Contacts = contacts;
    }
}
