package com.waftinc.missu.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;

/**
 * Created by Vicky Developer on 27-Jan-16.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class LostRequestModel {
    // private String names;
    private String name;
    private String age;
    private String lostCity;
    private String url;
    private String imageID;
    private boolean found;
    private String foundCity;
    private String foundBy;
    private String foundImageID;
    private String owner;
    private HashMap<String, Object> timestampPosted;
    private HashMap<String, Object> timestampJoined;


    /**
     * Required public constructor
     */
    public LostRequestModel() {
    }

    /**
     * Use this constructor to create new LostRequestModel.
     *
     * @param name            name of lost person
     * @param age             age of lost person
     * @param lostCity        lostCity in which the lost person was last seen
     * @param owner           encoded email of the owner
     * @param timestampPosted server time when request was posted
     */
    public LostRequestModel(String name, String age, String lostCity, String owner, HashMap<String, Object> timestampPosted) {
        this.name = name;
        this.age = age;
        this.lostCity = lostCity;
        this.owner = owner;
        this.timestampPosted = timestampPosted;
        this.found = false;
//        this.foundBy = "";
//        this.foundImageID = "";

    }

    public String getFoundCity() {
        return foundCity;
    }

    public void setFoundCity(String foundCity) {
        this.foundCity = foundCity;
    }

    public String getAge() {
        return age;
    }

    public String getLostCity() {
        return lostCity;
    }

    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public String getFoundBy() {
        return foundBy;
    }

    public void setFoundBy(String foundBy) {
        this.foundBy = foundBy;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public HashMap<String, Object> getTimestampJoined() {
        return timestampPosted;
    }

    public void setTimestampJoined(HashMap<String, Object> timestampJoined) {
        this.timestampJoined = timestampJoined;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageID() {
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public String getFoundImageID() {
        return foundImageID;
    }

    public void setFoundImageID(String foundImageID) {
        this.foundImageID = foundImageID;
    }

    public HashMap<String, Object> getTimestampPosted() {
        return timestampPosted;
    }
}
