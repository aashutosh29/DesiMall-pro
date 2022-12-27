package com.aashutosh.desimall_pro.models.java;

public class Store {
    String id;
    String name;
    String lat;
    String lon;
    String url;
    float distance;
    String notificationTopic;
    String gmail;
    String branchCode;

    public Store() {
    }

    public Store(String id, String name, String lat, String lon, String url, String notificationTopic, String gmail, String branchCode) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.url = url;
        this.notificationTopic = notificationTopic;
        this.branchCode = branchCode;
        this.gmail = gmail;
    }

    public String getNotificationTopic() {
        return notificationTopic;
    }

    public void setNotificationTopic(String notificationTopic) {
        this.notificationTopic = notificationTopic;
    }

    public String getId() {
        return id;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
