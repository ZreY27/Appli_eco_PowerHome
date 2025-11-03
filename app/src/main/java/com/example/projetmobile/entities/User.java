package com.example.projetmobile.entities;

import java.util.Date;

public class User {

    private int id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String token;
    private Date expired_at;
    private int habitat_id;

    public User() {
    }

    public User(int id, String firstname, String lastname, String email, String password, String token, Date expired_at) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.token = token;
        this.expired_at = expired_at;
    }

    public String getFirstName(){
        if (firstname == null)
            return "Inconnu";
        return firstname;
    }

    public String getToken() {
        return token;
    }
}
