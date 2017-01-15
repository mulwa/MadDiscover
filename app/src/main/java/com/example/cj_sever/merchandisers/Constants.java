package com.example.cj_sever.merchandisers;

/**
 * Created by cj-sever on 11/16/16.
 */
public final class Constants {
//  constants used for Intents extras
//    for event intent
    public static final String EVENTNAME ="eventName";
    public static final String EVENTDESCRIPTION ="eventDescription";
    public static final String EVENTIMAGE ="eventImage";
    public static final String EVENT_DAY_FROM = "eventDateFrom";
    public static final String EVENT_DAY_TO = "eventDateTO";
    public static final String EVENTVENUE = "venue";
    public static final String EVENT_START_TIME ="startTime";
    public static final String EVENT_END_TIME="endTime";
    public static final String EVENTORGANIZER ="eventOrganizer";
    public static final String EVENTOWNER ="eventOwner";

    public static final String OWNER_EMAIL ="eventOrganizer";
    public static final String OWNER_MOBILE ="ownerMobile";
    public static final String EVENT_CATEGORY ="category";
    public static final String EVENT_KEY = "eventkey";

//    for firebase reference
    public static final String firebase_reference_Event_image = "image";
    public static final String firebase_reference_Event_title = "title";
    public static final String firebase_reference_Event_description = "description";
    public static final String firebase_reference_Event_venue = "venue";
    public static final String firebase_reference_Event_dateFrom = "dateFrom";
    public static final String firebase_reference_Event_dateTo = "dateTo";
    public static final String firebase_reference_Event_startTime = "startTime";
    public static final String firebase_reference_Event_endTime = "endTime";
    public static final String firebase_reference_Event_organiser = "organiser";
    public static final String firebase_reference_Event_owner = "owner";

    public static final String filter_category = "CATEGORY";

//    firebase database nodes
    public static final String comments_node = "Comments";
    public static final String event_node = "Events";



}
