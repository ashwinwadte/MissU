package com.waftinc.missu.utils;

/**
 * Created by Vicky Developer on 21-Jan-16.
 */
public class Constants {

    //Kairos API details
    public static final String KAIROS_BASE_URL = "https://api.kairos.com/media/";
    public static final String KAIROS_API_ID = "your_kairos_api_id";
    public static final String KAIROS_API_KEY = "your_kairos_api_key";
    public static final String KAIROS_LOST_GALLERY = "LostGallery";
    public static final String KAIROS_FOUND_GALLERY = "FoundGallery";

    /**
     * Constants related to locations in Firebase, such as the name of the node
     * where users are stored (ie "users")
     */
    public static final String FIREBASE_LOCATION_USERS = "users";
    public static final String FIREBASE_LOCATION_LOST = "lost";
    public static final String FIREBASE_LOCATION_FOUND = "found";
    public static final String FIREBASE_LOCATION_USER_INFO = "userInfo";

    //Constant Firebase URLS
    public static final String FIREBASE_ROOT_URL = "your_firebase_app_url";
    public static final String FIREBASE_URL_USERS = FIREBASE_ROOT_URL + "/" + FIREBASE_LOCATION_USERS;
    public static final String FIREBASE_URL_LOST = FIREBASE_ROOT_URL + "/" + FIREBASE_LOCATION_LOST;
    public static final String FIREBASE_URL_FOUND = FIREBASE_ROOT_URL + "/" + FIREBASE_LOCATION_FOUND;

    /**
     * Constants for Firebase object properties
     */
    public static final String FIREBASE_PROPERTY_TIMESTAMP = "timestamp";
    public static final String FIREBASE_PROPERTY_IMAGE_ID = "imageID";
    public static final String FIREBASE_PROPERTY_IS_LOST_POST = "isLostPost";


    //Image storage folder
    public static final String IMAGE_FOLDER = "/MissU/Images/";

    //SharedPreference KEYs
    public static final String USER_EMAIL = "userEmail";
    public static final String ENCODED_EMAIL = "encodedEmail";
    public static final String UID = "uid";


    //Kairos properties
    public static final String KAIROS_SELECTOR = "SETPOSE";
    public static final String KAIROS_MULTIPLE_FACES = "false";
    public static final String KAIROS_MIN_HEAD_SCALE = "0.125";
    public static final String KAIROS_MAX_RESULTS = "5";
    public static final String KAIROS_THRESHOLD = "0.70";

    //Parse.com API Keys
    public static final String PARSE_APPLICATION_ID = "your_parse_application_id";
    public static final String PARSE_CLIENT_KEY = "your_parse_client_key";

    //Parse properties
    public static final String PARSE_CLASS_MISSU_IMAGES = "MissUImages";
    public static final String PARSE_COLUMN_ENCODED_USER_EMAIL = "encodedUserEmail";
    public static final String PARSE_COLUMN_IMAGE = "image";

}
