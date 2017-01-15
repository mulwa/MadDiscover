package com.example.cj_sever.merchandisers.Models;

/**
 * Created by cj-sever on 10/20/16.
 */
public class Advert {
    private String image;
    private String category;
    private String title;
    private String description;
    private String amount;
    private String mobile;
    private String username;

    public Advert() {
    }

    public Advert(String image, String category, String title, String description, String amount, String mobile, String username) {
        this.image = image;
        this.category = category;
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.mobile = mobile;
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
