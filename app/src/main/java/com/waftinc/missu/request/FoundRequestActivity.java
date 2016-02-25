package com.waftinc.missu.request;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.kairos.Kairos;
import com.kairos.KairosListener;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.waftinc.missu.R;
import com.waftinc.missu.model.FoundRequestModel;
import com.waftinc.missu.model.LostRequestModel;
import com.waftinc.missu.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FoundRequestActivity extends AppCompatActivity {

    ImageView ivFoundPerson;
    EditText etCityLastSeen;
    Button bPostRequest;
    ProgressBar pbImageUpload;
    RelativeLayout layout;

    Boolean picSet = false;
    Bitmap mBitmap = null;

    Kairos mKairosRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_request);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mKairosRef = new Kairos();
        mKairosRef.setAuthentication(this, Constants.KAIROS_API_ID, Constants.KAIROS_API_KEY);

        initializeWidgets();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeAndSetImage();
            }
        });

        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePhotoFromGallery();
            }
        });

    }

    private void choosePhotoFromGallery() {

        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(pickPhoto, 2);
    }

    private void takeAndSetImage() {
        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePic, 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            mBitmap = (Bitmap) data.getExtras().get("data");

            ivFoundPerson.setImageBitmap(mBitmap);
            picSet = true;
        }

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            //this string contains path of selected image
            String imagePath = cursor.getString(columnIndex);
            cursor.close();

            mBitmap = BitmapFactory.decodeFile(imagePath);

//            Bitmap bitmap = mBitmap;
//
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//            byte[] ImageAsBytes = outputStream.toByteArray();
//
//            bitmap = BitmapFactory.decodeByteArray(ImageAsBytes, 0, ImageAsBytes.length);
            ivFoundPerson.setImageBitmap(mBitmap);
            scaleImage(ivFoundPerson, 250);
            picSet = true;

        } else {
            Toast.makeText(getApplicationContext(), "Please select image.", Toast.LENGTH_LONG).show();
        }
    }

    private void scaleImage(ImageView view, int boundBoxInDp) {
        // Get the ImageView and its bitmap
        Drawable drawing = view.getDrawable();
        Bitmap bitmap = ((BitmapDrawable) drawing).getBitmap();

        // Get current dimensions
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        float xScale = ((float) boundBoxInDp) / width;
        float yScale = ((float) boundBoxInDp) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

        //Assign the scaled bitmap to global variable mBitmap to upload it
        mBitmap = scaledBitmap;

        BitmapDrawable result = new BitmapDrawable(scaledBitmap);
        width = scaledBitmap.getWidth();
        height = scaledBitmap.getHeight();

        // Apply the scaled bitmap
        view.setImageDrawable(result);

        // Now change ImageView's dimensions to match the scaled image
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }

    /**
     * Initialize the UI widgets. Ex. EditTexts, Buttons, etc.
     */
    private void initializeWidgets() {
        layout = (RelativeLayout) findViewById(R.id.layout_FoundRequestActivity);
        ivFoundPerson = (ImageView) findViewById(R.id.ivFoundPersonImage);
        etCityLastSeen = (EditText) findViewById(R.id.etCity);
        bPostRequest = (Button) findViewById(R.id.bPostLostRequest);
        pbImageUpload = (ProgressBar) findViewById(R.id.pbImageUploadFound);

        bPostRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Reset errors
                etCityLastSeen.setError(null);

                //EditText values
                String city = etCityLastSeen.getText().toString();


                boolean cancel = false;
                View focusView = null;

                if (!picSet) {
                    Toast.makeText(FoundRequestActivity.this, "Please set image of lost person", Toast.LENGTH_SHORT).show();
                    cancel = true;
                    focusView = ivFoundPerson;
                }

                if (TextUtils.isEmpty(city)) {
                    etCityLastSeen.setError("This field is required");
                    focusView = etCityLastSeen;
                    cancel = true;
                }

                if (cancel) {
                    // There was an error; don't attempt to post request and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {
                    //kick off a background task to post the request.
                    postRequest(city);

                }
            }
        });
    }

    private void postRequest(String city) {
        //Show a progress spinner
        pbImageUpload.setVisibility(View.VISIBLE);
        if (mBitmap != null) {
            //get encoded email of owner from preference
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(FoundRequestActivity.this);
            String encodedEmail = sp.getString(Constants.ENCODED_EMAIL, null);

            //set timestamp to server value
            HashMap<String, Object> timestampPosted = new HashMap<>();
            timestampPosted.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

            FoundRequestModel foundRequestModel = new FoundRequestModel(city, encodedEmail, timestampPosted);

            //if upload to parse is successful, try to enroll image to Kairos
            uploadImageToParse(foundRequestModel, mBitmap);
        }
    }

    /**
     * upload image into Parse cloud and save it`s object to ParseObject
     *
     * @param foundRequestModel object of {@link FoundRequestModel}
     * @param mBitmap           bitmap image to upload
     * @return true if successfully uploaded
     */
    private void uploadImageToParse(final FoundRequestModel foundRequestModel, Bitmap mBitmap) {
        //convert bitmap to Base64 format:
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] ImageAsBytes = outputStream.toByteArray();
        String imageFileName = "foundIn" + foundRequestModel.getFoundCity().replaceAll("\\s+", "") + ".png";

        //Create a ParseFile
        final ParseFile imageFile = new ParseFile(imageFileName, ImageAsBytes);

        //upload into Parse cloud
        imageFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                //there was error while uploading image
                if (e != null)
                    Toast.makeText(FoundRequestActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                else {
                    //get the url
                    final String url = imageFile.getUrl();
                    foundRequestModel.setUrl(imageFile.getUrl());

                    //add imageFile to ParseObject
                    final ParseObject imageObject = new ParseObject(Constants.PARSE_CLASS_MISSU_IMAGES);
                    imageObject.put(Constants.PARSE_COLUMN_ENCODED_USER_EMAIL, foundRequestModel.getOwner());
                    imageObject.put(Constants.PARSE_COLUMN_IMAGE, imageFile);

                    imageObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(FoundRequestActivity.this, "Object set.", Toast.LENGTH_SHORT).show();
                                //get the imageID
                                final String imageID = imageObject.getObjectId();
                                foundRequestModel.setImageID(imageID);

                                enrollImageToKairosFoundGallery(foundRequestModel);
                            }
                        }
                    });
                }
            }
        });

    }

    /**
     * Try to enroll image with url to Kairos Lost Gallery.
     *
     * @param foundRequestModel object of {@link FoundRequestModel}
     */
    private void enrollImageToKairosFoundGallery(final FoundRequestModel foundRequestModel) {
        try {
            mKairosRef.enroll(foundRequestModel.getUrl(), foundRequestModel.getImageID(), Constants.KAIROS_FOUND_GALLERY, Constants.KAIROS_SELECTOR,
                    Constants.KAIROS_MULTIPLE_FACES, Constants.KAIROS_MIN_HEAD_SCALE, new KairosListener() {
                        @Override
                        public void onSuccess(String s) {
                            Log.d("rajuS", s);

                            JSONObject rootNode = null;

                            try {
                                rootNode = new JSONObject(s);
                                JSONArray imgArray = rootNode.optJSONArray("images");
                                if (imgArray != null) {
                                    recognizeInLostGallery(foundRequestModel);
                                } else {
                                    //Error - No face found in image.
                                    pbImageUpload.setVisibility(View.GONE);
                                    Snackbar.make(layout, "No face detected. Take proper image", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            recreate();
                                        }
                                    }).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFail(String s) {
                            Log.d("rajuE", s);
                        }
                    }

            );
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Try to recognize just enrolled image in Found Gallery.
     *
     * @param foundRequestModel object of {@link FoundRequestModel}
     */

    private void recognizeInLostGallery(final FoundRequestModel foundRequestModel) {
        try {
            mKairosRef.recognize(foundRequestModel.getUrl(), Constants.KAIROS_LOST_GALLERY, Constants.KAIROS_SELECTOR, Constants.KAIROS_THRESHOLD, Constants.KAIROS_MIN_HEAD_SCALE, Constants.KAIROS_MAX_RESULTS, new KairosListener() {
                @Override
                public void onSuccess(String s) {
                    Log.d("rajuSR", s);

                    JSONObject rootNode = null;
                    try {
                        rootNode = new JSONObject(s);
                        JSONArray imgArray = rootNode.optJSONArray("images");
                        if (imgArray != null) {

                            JSONObject firstImgJSONObject = imgArray.optJSONObject(0);

                            JSONArray candidatesArray = firstImgJSONObject.optJSONArray("candidates");

                            if (candidatesArray != null) {
                                JSONObject firstCandidateJSONObject = candidatesArray.optJSONObject(0);

                                //get first key i.e. imageID in jsonObject
                                Iterator<String> keys = firstCandidateJSONObject.keys();
                                if (keys.hasNext()) {
                                    String imageID = (String) keys.next();
                                    foundRequestModel.setMatchedImageID(imageID);
                                    foundRequestModel.setMatched(true);
                                }
                            } else {
                                Toast.makeText(FoundRequestActivity.this, "No match found", Toast.LENGTH_SHORT).show();
                            }

                            postToFirebase(foundRequestModel);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFail(String s) {
                    Log.d("rajuSR", s);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void postToFirebase(final FoundRequestModel foundRequestModel) {
        final String imageID = foundRequestModel.getImageID();
        String matchedImageID = foundRequestModel.getMatchedImageID();

        final HashMap<String, Object> updateAllRequests = new HashMap<String, Object>();

        //image was matched previously
        if (!TextUtils.isEmpty(matchedImageID)) {
            final Firebase matchedLocation = new Firebase(Constants.FIREBASE_URL_LOST).child(matchedImageID);

            matchedLocation.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    LostRequestModel lostRequestModel = dataSnapshot.getValue(LostRequestModel.class);

                    if (lostRequestModel != null) {
                        //get owner of found image
                        String matchedTo = lostRequestModel.getOwner();
                        foundRequestModel.setMatchedTo(matchedTo);

                        String name = lostRequestModel.getName();
                        foundRequestModel.setName(name);

                        String age = lostRequestModel.getAge();
                        foundRequestModel.setAge(age);

                        String lostCity = lostRequestModel.getLostCity();
                        foundRequestModel.setLostCity(lostCity);

                        //update lostRequestModel in "lost/" node of firebase
                        updateAllRequests.put("/" + Constants.FIREBASE_LOCATION_LOST + "/" + lostRequestModel.getImageID() + "/found", true);
                        updateAllRequests.put("/" + Constants.FIREBASE_LOCATION_LOST + "/" + lostRequestModel.getImageID() + "/foundCity", foundRequestModel.getFoundCity());
                        updateAllRequests.put("/" + Constants.FIREBASE_LOCATION_LOST + "/" + lostRequestModel.getImageID() + "/foundBy", foundRequestModel.getOwner());
                        updateAllRequests.put("/" + Constants.FIREBASE_LOCATION_LOST + "/" + lostRequestModel.getImageID() + "/foundImageID", foundRequestModel.getImageID());

                        //update lostRequestModel in "users/lost/" node of firebase
                        updateAllRequests.put("/" + Constants.FIREBASE_LOCATION_USERS + "/" + lostRequestModel.getOwner() + "/" + Constants.FIREBASE_LOCATION_LOST + "/" + lostRequestModel.getImageID() + "/found", true);
                        updateAllRequests.put("/" + Constants.FIREBASE_LOCATION_USERS + "/" + lostRequestModel.getOwner() + "/" + Constants.FIREBASE_LOCATION_LOST + "/" + lostRequestModel.getImageID() + "/foundCity", foundRequestModel.getFoundCity());
                        updateAllRequests.put("/" + Constants.FIREBASE_LOCATION_USERS + "/" + lostRequestModel.getOwner() + "/" + Constants.FIREBASE_LOCATION_LOST + "/" + lostRequestModel.getImageID() + "/foundBy", foundRequestModel.getOwner());
                        updateAllRequests.put("/" + Constants.FIREBASE_LOCATION_USERS + "/" + lostRequestModel.getOwner() + "/" + Constants.FIREBASE_LOCATION_LOST + "/" + lostRequestModel.getImageID() + "/foundImageID", foundRequestModel.getImageID());
                    }

                    //image was found, post the found request

                    //Firebase root reference
                    final Firebase firebaseRef = new Firebase(Constants.FIREBASE_ROOT_URL);

                    //build map of model foundRequestModel
                    HashMap<String, Object> foundRequestMap = (HashMap<String, Object>) new ObjectMapper().convertValue(foundRequestModel, Map.class);

                    //post request to foundLocation
                    updateAllRequests.put("/" + Constants.FIREBASE_LOCATION_FOUND + "/" + imageID, foundRequestMap);

                    //post request to user
                    updateAllRequests.put("/" + Constants.FIREBASE_LOCATION_USERS + "/" + foundRequestModel.getOwner() + "/" + Constants.FIREBASE_LOCATION_FOUND + "/" + imageID, foundRequestMap);

                    firebaseRef.updateChildren(updateAllRequests, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            //finish the activity
                            finish();
                        }
                    });
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });
        } else {
            //image was not found, post the found request

            //Firebase root reference
            final Firebase firebaseRef = new Firebase(Constants.FIREBASE_ROOT_URL);

            //build map of model foundRequestModel
            HashMap<String, Object> foundRequestMap = (HashMap<String, Object>) new ObjectMapper().convertValue(foundRequestModel, Map.class);

            //post request to foundLocation
            updateAllRequests.put("/" + Constants.FIREBASE_LOCATION_FOUND + "/" + imageID, foundRequestMap);

            //post request to user
            updateAllRequests.put("/" + Constants.FIREBASE_LOCATION_USERS + "/" + foundRequestModel.getOwner() + "/" + Constants.FIREBASE_LOCATION_FOUND + "/" + imageID, foundRequestMap);

            firebaseRef.updateChildren(updateAllRequests, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    //finish the activity
                    finish();
                }
            });
        }
    }

}
