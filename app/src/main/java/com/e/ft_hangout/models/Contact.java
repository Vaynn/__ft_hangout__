package com.e.ft_hangout.models;

public class Contact {
    //fields
    private String name;
    private String firstname;
    private String phoneNumber;
    private String email;
    private String pics;
    private String phoneRing;
    private String id;

    //constructor
    public Contact(String id, String name, String firstname, String phoneNumber, String email, String pics, String phoneRing){
        this.id = id;
        this.name = name;
        this.firstname = firstname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.pics = pics;
        this.phoneRing = phoneRing;
    }

    //methods
    public  String getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneRing() {
        return phoneRing;
    }

    public String getPics() {
        return pics;
    }
}
