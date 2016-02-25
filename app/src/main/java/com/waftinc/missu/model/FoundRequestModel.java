package com.waftinc.missu.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;

/**
 * Created by Vicky Developer on 27-Jan-16.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class FoundRequestModel {
    // private String names;
    private String name;
    private String age;
    private String foundCity; //
    private String url; //
    private String imageID; //
    private boolean matched; //
    private String lostCity;
    private String matchedTo; //
    private String matchedImageID; //
    private String owner; //
    private HashMap<String, Object> timestampPosted; //

    /**
     * Required public constructor
     */
    public FoundRequestModel() {
    }

    /**
     * Use this constructor to create new FoundRequestModel.
     *
     * @param foundCity       where the person was seen
     * @param owner           encoded email of the owner
     * @param timestampPosted server time when request was posted
     */
    public FoundRequestModel(String foundCity, String owner, HashMap<String, Object> timestampPosted) {
        this.foundCity = foundCity;
        this.timestampPosted = timestampPosted;
        this.owner = owner;
        this.matched = false;
//        this.matchedTo = "";
//        this.matchedImageID = "";
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLostCity() {
        return lostCity;
    }

    public void setLostCity(String lostCity) {
        this.lostCity = lostCity;
    }

    public String getFoundCity() {
        return foundCity;
    }

    public String getImageID() {
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public String getMatchedTo() {
        return matchedTo;
    }

    public void setMatchedTo(String matchedTo) {
        this.matchedTo = matchedTo;
    }

    public String getMatchedImageID() {
        return matchedImageID;
    }

    public void setMatchedImageID(String matchedImageID) {
        this.matchedImageID = matchedImageID;
    }

    public String getOwner() {
        return owner;
    }

    public HashMap<String, Object> getTimestampPosted() {
        return timestampPosted;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}
