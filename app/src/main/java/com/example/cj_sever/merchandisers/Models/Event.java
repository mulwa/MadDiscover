package com.example.cj_sever.merchandisers.Models;

/**
 * Created by cj-sever on 10/22/16.
 */
public class Event {
    private String eventKey;
    private String image;
    private String title;
    private String category;
    private String description;
    private String venue;
    private String dateFrom;
    private String dateTo;
    private String startTime;
    private String EndTime;
    private String organiser;
    private String owner;
    private String ownerMobile;

    public Event() {
    }

    public Event(String eventKey, String image, String title, String category, String description,
                 String venue, String dateFrom, String dateTo, String startTime, String endTime,
                 String organiser, String owner, String ownerMobile) {
        this.eventKey = eventKey;
        this.image = image;
        this.title = title;
        this.category = category;
        this.description = description;
        this.venue = venue;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.startTime = startTime;
        EndTime = endTime;
        this.organiser = organiser;
        this.owner = owner;
        this.ownerMobile = ownerMobile;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getOrganiser() {
        return organiser;
    }

    public void setOrganiser(String organiser) {
        this.organiser = organiser;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOwnerMobile() {
        return ownerMobile;
    }

    public void setOwnerMobile(String ownerMobile) {
        this.ownerMobile = ownerMobile;
    }
}
