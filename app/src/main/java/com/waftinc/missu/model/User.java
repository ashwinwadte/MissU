package com.waftinc.missu.model;

import java.util.HashMap;

/**
 * Created by Vicky Developer on 26-Jan-16.
 */
public class User {
    // private String name;
    private String encodeEmail;
    private HashMap<String, Object> timestampJoined;


    /**
     * Required public constructor
     */
    public User() {
    }

    /**
     * Use this constructor to create new User.
     * Takes user name, encodedEmail and timestampJoined as params
     *
     * @param encodedEmail
     * @param timestampJoined
     */
    public User(String encodedEmail, HashMap<String, Object> timestampJoined) {
        this.encodeEmail = encodedEmail;
        this.timestampJoined = timestampJoined;
    }


    public String getEncodeEmail() {
        return encodeEmail;
    }

    public HashMap<String, Object> getTimestampJoined() {
        return timestampJoined;
    }

}
