package com.example.cj_sever.merchandisers.Models;
/**
 * Created by cj-sever on 1/25/17.
 */
public class Like {
    private String email;

    public Like(String email) {
        this.email = email;
    }

    public Like() {
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
