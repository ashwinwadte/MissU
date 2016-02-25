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
import com.waftinc.missu.model.FoundRequestModel;
import com.waftinc.missu.utils.Constants;
import com.waftinc.missu.utils.Utils;

/**
 * Created by Vicky Developer on 28-Jan-16.
 */
public class FoundListAdapter extends FirebaseListAdapter<FoundRequestModel> {
    private String mEncodedEmail;
    private Context context;

    public FoundListAdapter(Activity activity, Class<FoundRequestModel> modelClass, int modelLayout, Firebase ref, String encodedEmail) {
        super(activity, modelClass, modelLayout, ref);

        this.mEncodedEmail = encodedEmail;
        this.context = activity;
    }

    @Override
    protected void populateView(View v, FoundRequestModel foundRequestModel) {
        super.populateView(v, foundRequestModel);

        //Grab the views
        ParseImageView parseImageView = (ParseImageView) v.findViewById(R.id.ivListFoundPerson);
        TextView tvCity = (TextView) v.findViewById(R.id.tvListFoundInCity);
        TextView tvIsMatched = (TextView) v.findViewById(R.id.tvListIsMatched);
        TextView tvMatchedTo = (TextView) v.findViewById(R.id.tvListMatchedTo);
        TextView tvRelativeTime = (TextView) v.findViewById(R.id.tvListFoundTime);

        //Grab the strings from model
        String city = foundRequestModel.getFoundCity();
        boolean isMatched = foundRequestModel.isMatched();
        String imageID = foundRequestModel.getImageID();
        String timePosted = foundRequestModel.getTimestampPosted().get(Constants.FIREBASE_PROPERTY_TIMESTAMP).toString();

        //convert time to relative time
        String relativeTime = String.valueOf(DateUtils.getRelativeTimeSpanString(Long.parseLong(timePosted)));

        //set the views
        tvCity.setText("Last seen in " + city);
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


        if (isMatched) {
            tvIsMatched.setText("Matched");

            String matchedTo = foundRequestModel.getMatchedTo();
            if (matchedTo.equals(mEncodedEmail))
                matchedTo = "you";
            else
                matchedTo = Utils.decodeEmail(matchedTo);

            tvMatchedTo.setVisibility(View.VISIBLE);
            tvMatchedTo.setText("To " + matchedTo);
        }
    }
}
