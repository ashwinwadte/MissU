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
import android.os.Environment;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LostRequestActivity extends AppCompatActivity {

    ImageView ivLostPerson;
    EditText etName, etAge, etCityLastSeen;
    Button bPostRequest;
    ProgressBar pbImageUpload;
    RelativeLayout layout;

    Boolean picSet = false;
    Bitmap mBitmap = null;
    String filePath, fileName;

    Kairos mKairosRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_request);
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

    /*private void takePicture() {
        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File imageFolder = new File(Environment.getExternalStorageDirectory(), Constants.IMAGE_FOLDER);

        if (!imageFolder.exists())
            imageFolder.mkdirs();

        String timeStamp = new SimpleDateFormat(Utils.SIMPLE_DATE_FORMAT, Locale.US).format(new Date());

        //String imageName = "MissU_IMG" + timeStamp + ".png";
        String imageName = "MissU_IMG_" + timeStamp;

        //File imageFile = new File(imageFolder, imageName);

        File imageFile = null;
        try {
            imageFile = File.createTempFile(imageName, ".jpg", imageFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (imageFile != null) {
            takePic.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
            startActivityForResult(takePic, 0);
        }
//        Uri uriSavedImage = Uri.fromFile(imageFile);
//
//        takePic.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        // startActivity(takePic);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK) {
//            Uri selectedImage = data.getData();
//            String[] filePathColumn = {MediaStore.Images.Media.DATA};
//            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
//
//            cursor.moveToFirst();
//
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//
//            //file path of captured image
//            filePath = cursor.getString(columnIndex);
//            File f = new File(filePath);
//            fileName = f.getName();
//
//            cursor.close();
//
//            //Convert filePath into bitmap image
//            Bitmap mBitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
//
//            //set image in image view
//            ivLostPerson.setImageBitmap(mBitmap);
//
//            //save image in app`s folder
//            saveImage(fileName, filePath);

            Bitmap mBitmap = (Bitmap) data.getExtras().get("data");

            ivLostPerson.setImageBitmap(mBitmap);

        }

        if (requestCode == 1 && resultCode == RESULT_OK) {
            mBitmap = (Bitmap) data.getExtras().get("data");

            ivLostPerson.setImageBitmap(mBitmap);
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

//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//            byte[] ImageAsBytes = outputStream.toByteArray();
//
//            bitmap = BitmapFactory.decodeByteArray(ImageAsBytes, 0, ImageAsBytes.length);

            ivLostPerson.setImageBitmap(mBitmap);
            scaleImage(ivLostPerson, 250);
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

    /*private int dpToPx(int dp) {
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }*/

    private void saveImage(String fileName, String filePath) {
        File dir = new File(Environment.getExternalStorageDirectory() + Constants.IMAGE_FOLDER);

        File file = new File(Environment.getExternalStorageDirectory() + Constants.IMAGE_FOLDER + fileName);

        if (!dir.exists())
            dir.mkdirs();

        if (!file.exists()) {
            try {
                file.createNewFile();

                FileChannel source = new FileInputStream(filePath).getChannel();
                FileChannel destination = new FileOutputStream(file).getChannel();

                destination.transferFrom(source, 0, source.size());

                source.close();
                destination.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Initialize the UI widgets. Ex. EditTexts, Buttons, etc.
     */
    private void initializeWidgets() {
        layout = (RelativeLayout) findViewById(R.id.layout_LostRequestActivity);
        ivLostPerson = (ImageView) findViewById(R.id.ivLostPersonImage);
        etName = (EditText) findViewById(R.id.etName);
        etAge = (EditText) findViewById(R.id.etAge);
        etCityLastSeen = (EditText) findViewById(R.id.etCity);
        bPostRequest = (Button) findViewById(R.id.bPostLostRequest);
        pbImageUpload = (ProgressBar) findViewById(R.id.pbImageUploadLost);

        bPostRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Reset errors
                etName.setError(null);
                etAge.setError(null);
                etCityLastSeen.setError(null);

                //EditText values
                String name = etName.getText().toString();
                String age = etAge.getText().toString();
                String city = etCityLastSeen.getText().toString();


                boolean cancel = false;
                View focusView = null;

                if (!picSet) {
                    Toast.makeText(LostRequestActivity.this, "Please set image of lost person", Toast.LENGTH_SHORT).show();
                    cancel = true;
                    focusView = ivLostPerson;
                }

                if (TextUtils.isEmpty(city)) {
                    etCityLastSeen.setError("This field is required");
                    focusView = etCityLastSeen;
                    cancel = true;
                }

                if (TextUtils.isEmpty(age)) {
                    etAge.setError("This field is required");
                    focusView = etAge;
                    cancel = true;
                }

                if (TextUtils.isEmpty(name)) {
                    etName.setError("This field is required");
                    focusView = etName;
                    cancel = true;
                }

                if (cancel) {
                    // There was an error; don't attempt to post request and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {
                    //kick off a background task to post the request.
                    postRequest(name, age, city);

                }
            }
        });
    }

    private void postRequest(String name, String age, String city) {
        // Show a progress spinner
        pbImageUpload.setVisibility(View.VISIBLE);
        if (mBitmap != null) {

//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            mBitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
//            byte[] bytesOfImg = out.toByteArray();
//            Bitmap compressedImg = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));

            //get encoded email of owner from preference
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(LostRequestActivity.this);
            String encodedEmail = sp.getString(Constants.ENCODED_EMAIL, null);

            //set timestamp to server value
            HashMap<String, Object> timestampPosted = new HashMap<>();
            timestampPosted.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

            LostRequestModel lostRequestModel = new LostRequestModel(name, age, city, encodedEmail, timestampPosted);

            //if upload to parse is successful, try to enroll image to Kairos
            uploadImageToParse(lostRequestModel, mBitmap);
        }
    }

    /**
     * upload image into Parse cloud and save it`s object to ParseObject
     *
     * @param lostRequestModel object of {@link LostRequestModel}
     * @param mBitmap          bitmap image to upload
     * @return true if successfully uploaded
     */
    private void uploadImageToParse(final LostRequestModel lostRequestModel, Bitmap mBitmap) {
        //convert bitmap to Base64 format:
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] ImageAsBytes = outputStream.toByteArray();
        String imageFileName = lostRequestModel.getName().replaceAll("\\s+", "") + ".png";

        //Create a ParseFile
        final ParseFile imageFile = new ParseFile(imageFileName, ImageAsBytes);

        //upload into Parse cloud
        imageFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                //there was error while uploading image
                if (e != null)
                    Toast.makeText(LostRequestActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                else {
                    //get the url
                    final String url = imageFile.getUrl();
                    lostRequestModel.setUrl(imageFile.getUrl());

                    //add imageFile to ParseObject
                    final ParseObject imageObject = new ParseObject(Constants.PARSE_CLASS_MISSU_IMAGES);
                    imageObject.put(Constants.PARSE_COLUMN_ENCODED_USER_EMAIL, lostRequestModel.getOwner());
                    imageObject.put(Constants.PARSE_COLUMN_IMAGE, imageFile);

                    imageObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(LostRequestActivity.this, "Object set.", Toast.LENGTH_SHORT).show();
                                //get the imageID
                                final String imageID = imageObject.getObjectId();
                                lostRequestModel.setImageID(imageID);

                                enrollImageToKairosLostGallery(lostRequestModel);
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
     * @param lostRequestModel object of {@link LostRequestModel}
     */
    private void enrollImageToKairosLostGallery(final LostRequestModel lostRequestModel) {
        try {
            mKairosRef.enroll(lostRequestModel.getUrl(), lostRequestModel.getImageID(), Constants.KAIROS_LOST_GALLERY, Constants.KAIROS_SELECTOR,
                    Constants.KAIROS_MULTIPLE_FACES, Constants.KAIROS_MIN_HEAD_SCALE, new KairosListener() {
                        @Override
                        public void onSuccess(String s) {
                            Log.d("rajuS", s);

                            JSONObject rootNode = null;

                            try {
                                rootNode = new JSONObject(s);
                                JSONArray imgArray = rootNode.optJSONArray("images");
                                if (imgArray != null) {
                                    recognizeInFoundGallery(lostRequestModel);
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
     * @param lostRequestModel object of {@link LostRequestModel}
     */

    private void recognizeInFoundGallery(final LostRequestModel lostRequestModel) {
        try {
            mKairosRef.recognize(lostRequestModel.getUrl(), Constants.KAIROS_FOUND_GALLERY, Constants.KAIROS_SELECTOR, Constants.KAIROS_THRESHOLD, Constants.KAIROS_MIN_HEAD_SCALE, Constants.KAIROS_MAX_RESULTS, new KairosListener() {
                @Override
                public void onSuccess(String s) {
                    Log.d("rajuSR", s);

                    JSONObject rootNode = null;
                    try {
                        rootNode = new JSONObject(s);
                        JSONArray imgArray = rootNode.optJSONArray("images");
                        if (imgArray != null) {

                            JSONObject firstImgJSONObject = imgArray.optJSONObject(0);

                            JSONArray candidatesArray = firstImgJSONObject.optJSONArray("candidates"); //getJSONObject("transaction").optString("subject_id");


                            if (candidatesArray != null) {
                                JSONObject firstCandidateJSONObject = candidatesArray.optJSONObject(0);

                                //get first key i.e. imageID in jsonObject
                                Iterator<String> keys = firstCandidateJSONObject.keys();
                                if (keys.hasNext()) {
                                    String imageID = (String) keys.next();
                                    lostRequestModel.setFoundImageID(imageID);
                                    lostRequestModel.setFound(true);
                                }
                            } else {
                                Toast.makeText(LostRequestActivity.this, "No match found", Toast.LENGTH_SHORT).show();
                            }

                            postToFirebase(lostRequestModel);
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

    private void postToFirebase(final LostRequestModel lostRequestModel) {
        final String imageID = lostRequestModel.getImageID();
        String foundImageID = lostRequestModel.getFoundImageID();

        final HashMap<String, Object> updateAllRequests = new HashMap<String, Object>();

        //image was found previously
        if (!TextUtils.isEmpty(foundImageID)) {
            final Firebase foundLocation = new Firebase(Constants.FIREBASE_URL_FOUND).child(foundImageID);

            foundLocation.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    FoundRequestModel foundRequestModel = dataSnapshot.getValue(FoundRequestModel.class);

                    if (foundRequestModel != null) {
                        //get owner of found image
                        String foundBy = foundRequestModel.getOwner();
                        lostRequestModel.setFoundBy(foundBy);

                        //get city in which person was found
                        String foundCity = foundRequestModel.getFoundCity();
                        lostRequestModel.setFoundCity(foundCity);

                        //update foundRequestModel in "found/" node of firebase
                        updateAllRequests.put("/" + Constants.FIREBASE_LOCATION_FOUND + "/" + foundRequestModel.getImageID() + "/name", lostRequestModel.getName());
                        updateAllRequests.put("/" + Constants.FIREBASE_LOCATION_FOUND + "/" + foundRequestModel.getImageID() + "/age", lostRequestModel.getAge());
                        updateAllRequests.put("/" + Constants.FIREBASE_LOCATION_FOUND + "/" + foundRequestModel.getImageID() + "/lostCity", lostRequestModel.getLostCity());
                        updateAllRequests.put("/" + Constants.FIREBASE_LOCATION_FOUND + "/" + foundRequestModel.getImageID() + "/matched", true);
                        updateAllRequests.put("/" + Constants.FIREBASE_LOCATION_FOUND + "/" + foundRequestModel.getImageID() + "/matchedTo", lostRequestModel.getOwner());
                        updateAllRequests.put("/" + Constants.FIREBASE_LOCATION_FOUND + "/" + foundRequestModel.getImageID() + "/matchedImageID", lostRequestModel.getImageID());

                        //update foundRequestModel in "users/found/" node of firebase
                        updateAllRequests.put("/" + Constants.FIREBASE_LOCATION_USERS + "/" + foundRequestModel.getOwner() + "/" + Constants.FIREBASE_LOCATION_FOUND + "/" + foundRequestModel.getImageID() + "/name", lostRequestModel.getName());
                        updateAllRequests.put("/" + Constants.FIREBASE_LOCATION_USERS + "/" + foundRequestModel.getOwner() + "/" + Constants.FIREBASE_LOCATION_FOUND + "/" + foundRequestModel.getImageID() + "/age", lostRequestModel.getAge());
                        updateAllRequests.put("/" + Constants.FIREBASE_LOCATION_USERS + "/" + foundRequestModel.getOwner() + "/" + Constants.FIREBASE_LOCATION_FOUND + "/" + foundRequestModel.getImageID() + "/lostCity", lostRequestModel.getLostCity());
                        updateAllRequests.put("/" + Constants.FIREBASE_LOCATION_USERS + "/" + foundRequestModel.getOwner() + "/" + Constants.FIREBASE_LOCATION_FOUND + "/" + foundRequestModel.getImageID() + "/matched", true);
                        updateAllRequests.put("/" + Constants.FIREBASE_LOCATION_USERS + "/" + foundRequestModel.getOwner() + "/" + Constants.FIREBASE_LOCATION_FOUND + "/" + foundRequestModel.getImageID() + "/matchedTo", lostRequestModel.getOwner());
                        updateAllRequests.put("/" + Constants.FIREBASE_LOCATION_USERS + "/" + foundRequestModel.getOwner() + "/" + Constants.FIREBASE_LOCATION_FOUND + "/" + foundRequestModel.getImageID() + "/matchedImageID", lostRequestModel.getImageID());
                    }

                    //image was found, post the found request

                    //Firebase root reference
                    final Firebase firebaseRef = new Firebase(Constants.FIREBASE_ROOT_URL);

                    //build map of model lostRequestModel
                    HashMap<String, Object> lostRequestMap = (HashMap<String, Object>) new ObjectMapper().convertValue(lostRequestModel, Map.class);

                    //post request to lostLocation
                    updateAllRequests.put("/" + Constants.FIREBASE_LOCATION_LOST + "/" + imageID, lostRequestMap);

                    //post request to user
                    updateAllRequests.put("/" + Constants.FIREBASE_LOCATION_USERS + "/" + lostRequestModel.getOwner() + "/" + Constants.FIREBASE_LOCATION_LOST + "/" + imageID, lostRequestMap);

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

            //build map of model lostRequestModel
            HashMap<String, Object> lostRequestMap = (HashMap<String, Object>) new ObjectMapper().convertValue(lostRequestModel, Map.class);

            //post request to lostLocation
            updateAllRequests.put("/" + Constants.FIREBASE_LOCATION_LOST + "/" + imageID, lostRequestMap);

            //post request to user
            updateAllRequests.put("/" + Constants.FIREBASE_LOCATION_USERS + "/" + lostRequestModel.getOwner() + "/" + Constants.FIREBASE_LOCATION_LOST + "/" + imageID, lostRequestMap);

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
