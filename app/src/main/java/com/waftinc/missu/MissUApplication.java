package com.waftinc.missu;

import android.app.Application;

import com.firebase.client.Firebase;
import com.kairos.Kairos;
import com.parse.Parse;
import com.waftinc.missu.utils.Constants;

/**
 * Created by Vicky Developer on 12-Jan-16.
 */
public class MissUApplication extends Application {

    /**
     * A reference to the Firebase
     */
    public static Firebase mFirebaseRef;


    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
        //start firebase debug mode
        //Firebase.getDefaultConfig().setLogLevel(Logger.Level.DEBUG);

        //cache the data for offline capabilities
        Firebase.getDefaultConfig().setPersistenceEnabled(true);

        /* Create the Firebase ref that is used for all authentication with Firebase */
        mFirebaseRef = new Firebase(Constants.FIREBASE_ROOT_URL);

        // instantiate a new kairos instance
        Kairos myKairos = new Kairos();

        // set authentication for Kairos
        myKairos.setAuthentication(this, Constants.KAIROS_API_ID, Constants.KAIROS_API_KEY);

        //Parse authentication

        // [Optional] Power your app with Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, Constants.PARSE_APPLICATION_ID, Constants.PARSE_CLIENT_KEY);
    }
}
