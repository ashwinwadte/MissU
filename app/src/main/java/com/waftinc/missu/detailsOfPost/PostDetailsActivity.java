package com.waftinc.missu.detailsOfPost;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.waftinc.missu.R;
import com.waftinc.missu.model.FoundRequestModel;
import com.waftinc.missu.model.LostRequestModel;
import com.waftinc.missu.utils.Constants;
import com.waftinc.missu.utils.Utils;

public class PostDetailsActivity extends AppCompatActivity {
    ParseImageView ivYourImage, ivMatchedImage;
    TextView tvName, tvAge, tvLostCity, tvFoundCity, tvContact;
    ProgressBar pbLoadDetails;

    boolean mFlagLostPost;
    String imageID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initWidgets();

        if (this.getIntent() != null) {
            mFlagLostPost = this.getIntent().getBooleanExtra(Constants.FIREBASE_PROPERTY_IS_LOST_POST, true);
            imageID = this.getIntent().getStringExtra(Constants.FIREBASE_PROPERTY_IMAGE_ID);
        }

        showPersonInfo();

    }

    private void initWidgets() {
        ivYourImage = (ParseImageView) findViewById(R.id.ivYourImage);
        ivMatchedImage = (ParseImageView) findViewById(R.id.ivMatchedImage);

        tvName = (TextView) findViewById(R.id.tvNameOfPersonLost);
        tvAge = (TextView) findViewById(R.id.tvAgeOfPersonLost);
        tvLostCity = (TextView) findViewById(R.id.tvCityLost);
        tvFoundCity = (TextView) findViewById(R.id.tvCityFound);
        tvContact = (TextView) findViewById(R.id.tvContact);

        pbLoadDetails = (ProgressBar) findViewById(R.id.pbLoadDetails);
    }

    /**
     * shows the lost or found person`s details in the form
     */
    private void showPersonInfo() {
        //show progress bar
        pbLoadDetails.setVisibility(View.VISIBLE);

        //strings for person details
        String defaultNA = "NA";

        //set the textviews to default value i.e. NA(Not Available)
        tvName.setText(defaultNA);
        tvAge.setText(defaultNA);
        tvLostCity.setText(defaultNA);
        tvFoundCity.setText(defaultNA);
        tvContact.setText(defaultNA);

        //if the post is lost post
        if (mFlagLostPost) {
            Firebase lostPostLocation = new Firebase(Constants.FIREBASE_URL_LOST).child(imageID);

            lostPostLocation.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    LostRequestModel lostRequestModel = dataSnapshot.getValue(LostRequestModel.class);

                    ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(Constants.PARSE_CLASS_MISSU_IMAGES);

                    if (lostRequestModel != null) {
                        String name = lostRequestModel.getName();
                        String age = lostRequestModel.getAge();
                        String lostCity = lostRequestModel.getLostCity();

                        tvName.setText(name);
                        tvAge.setText(age);
                        tvLostCity.setText(lostCity);

                        //set person`s Image
                        try {
                            ParseObject parseObject1 = parseQuery.get(imageID);

                            ParseFile parseFile1 = (ParseFile) parseObject1.get(Constants.PARSE_COLUMN_IMAGE);

                            ivYourImage.setParseFile(parseFile1);
                            ivYourImage.loadInBackground();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        //if the person is found, then only set the following details
                        if (lostRequestModel.isFound()) {
                            String foundCity = lostRequestModel.getFoundCity();
                            String contact = lostRequestModel.getFoundBy();
                            String foundImageID = lostRequestModel.getFoundImageID();

                            tvFoundCity.setText(foundCity);
                            tvContact.setText(Utils.decodeEmail(contact));

                            try {
                                ParseObject parseObject2 = parseQuery.get(foundImageID);

                                ParseFile parseFile2 = (ParseFile) parseObject2.get(Constants.PARSE_COLUMN_IMAGE);

                                ivMatchedImage.setParseFile(parseFile2);
                                ivMatchedImage.loadInBackground();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    //hide progess bar as details are fetched except images
                    pbLoadDetails.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }

        //if the post is found post
        else {
            Firebase foundPostLocation = new Firebase(Constants.FIREBASE_URL_FOUND).child(imageID);

            foundPostLocation.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    FoundRequestModel foundRequestModel = dataSnapshot.getValue(FoundRequestModel.class);

                    ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(Constants.PARSE_CLASS_MISSU_IMAGES);

                    if (foundRequestModel != null) {
                        String foundCity = foundRequestModel.getFoundCity();
                        tvFoundCity.setText(foundCity);

                        //set person`s Image
                        try {
                            ParseObject parseObject1 = parseQuery.get(imageID);

                            ParseFile parseFile1 = (ParseFile) parseObject1.get(Constants.PARSE_COLUMN_IMAGE);

                            ivYourImage.setParseFile(parseFile1);
                            ivYourImage.loadInBackground();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        //if the person is matched, then only set the following details
                        if (foundRequestModel.isMatched()) {
                            String name = foundRequestModel.getName();
                            String age = foundRequestModel.getAge();
                            String lostCity = foundRequestModel.getLostCity();
                            String contact = foundRequestModel.getMatchedTo();
                            String matchedImageID = foundRequestModel.getMatchedImageID();

                            tvName.setText(name);
                            tvAge.setText(age);
                            tvLostCity.setText(lostCity);
                            tvContact.setText(Utils.decodeEmail(contact));

                            try {
                                ParseObject parseObject2 = parseQuery.get(matchedImageID);

                                ParseFile parseFile2 = (ParseFile) parseObject2.get(Constants.PARSE_COLUMN_IMAGE);

                                ivMatchedImage.setParseFile(parseFile2);
                                ivMatchedImage.loadInBackground();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    //hide progess bar as details are fetched except images
                    pbLoadDetails.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }

}
