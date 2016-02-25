package com.waftinc.missu.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;
import com.waftinc.missu.R;
import com.waftinc.missu.model.LostRequestModel;
import com.waftinc.missu.utils.Constants;
import com.waftinc.missu.utils.Utils;

/**
 * Created by Vicky Developer on 28-Jan-16.
 */
public class LostListAdapter extends FirebaseListAdapter<LostRequestModel> {
    private String mEncodedEmail;
    private Context context;

    public LostListAdapter(Activity activity, Class<LostRequestModel> modelClass, int modelLayout, Firebase ref, String encodedEmail) {
        super(activity, modelClass, modelLayout, ref);

        this.mEncodedEmail = encodedEmail;
        this.context = activity;
    }

    @Override
    protected void populateView(View v, LostRequestModel lostRequestModel) {
        super.populateView(v, lostRequestModel);

        //Grab the views
        ParseImageView parseImageView = (ParseImageView) v.findViewById(R.id.ivListLostPerson);
        TextView tvPersonName = (TextView) v.findViewById(R.id.tvListLostPersonName);
        TextView tvAge = (TextView) v.findViewById(R.id.tvListAge);
        TextView tvCity = (TextView) v.findViewById(R.id.tvListLostCity);
        TextView tvIsFound = (TextView) v.findViewById(R.id.tvListIsFound);
        TextView tvFoundBy = (TextView) v.findViewById(R.id.tvListFoundBy);
        TextView tvRelativeTime = (TextView) v.findViewById(R.id.tvListLostTime);

        //Grab the strings from model
        String name = lostRequestModel.getName();
        String age = lostRequestModel.getAge();
        String city = lostRequestModel.getLostCity();
        boolean isFound = lostRequestModel.isFound();
        String imageID = lostRequestModel.getImageID();
        String timePosted = lostRequestModel.getTimestampPosted().get(Constants.FIREBASE_PROPERTY_TIMESTAMP).toString();

        //convert time to relative time
        String relativeTime = String.valueOf(DateUtils.getRelativeTimeSpanString(Long.parseLong(timePosted)));

        //set the views
        tvPersonName.setText(name);
        tvAge.setText(age + " years old");
        tvCity.setText("Lost in " + city);
        tvRelativeTime.setText(relativeTime);

        //set image
        try {
            ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(Constants.PARSE_CLASS_MISSU_IMAGES);

            ParseObject parseObject = parseQuery.get(imageID);

//            parseObject.pinInBackground();
//
//            parseObject.fetchFromLocalDatastore();

            ParseFile parseFile = (ParseFile) parseObject.get(Constants.PARSE_COLUMN_IMAGE);

            //load and cache image with picasso
//            Picasso.with(context).load(parseFile.getUrl()).into(parseImageView);

            parseImageView.setParseFile(parseFile);
            parseImageView.loadInBackground();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (isFound) {
            tvIsFound.setText("Found");

            String foundBy = lostRequestModel.getFoundBy();
            if (foundBy.equals(mEncodedEmail))
                foundBy = "you";
            else
                foundBy = Utils.decodeEmail(foundBy);

            tvFoundBy.setVisibility(View.VISIBLE);
            tvFoundBy.setText("By " + foundBy);
        }
    }
}