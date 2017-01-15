package com.example.cj_sever.merchandisers.Models;

import android.net.Uri;

/**
 * Created by cj-sever on 12/14/16.
 */
public class Comment {
    private String userPhoto;
    private String comment;
    private String userName;
    private String time;

    public Comment() {
    }

    public Comment(String userName, String comment, String userPhoto, String time ) {
        this.userPhoto = userPhoto;
        this.comment = comment;
        this.userName = userName;
        this.time = time;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public String getComment() {
        return comment;
    }

    public String getUserName() {
        return userName;
    }

    public String getTime() {
        return time;
    }
}
