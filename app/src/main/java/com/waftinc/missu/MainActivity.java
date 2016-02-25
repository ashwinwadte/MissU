package com.waftinc.missu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.firebase.client.Firebase;
import com.waftinc.missu.adapter.FoundListAdapter;
import com.waftinc.missu.adapter.LostListAdapter;
import com.waftinc.missu.detailsOfPost.PostDetailsActivity;
import com.waftinc.missu.login.LoginActivity;
import com.waftinc.missu.model.FoundRequestModel;
import com.waftinc.missu.model.LostRequestModel;
import com.waftinc.missu.request.FoundRequestActivity;
import com.waftinc.missu.request.LostRequestActivity;
import com.waftinc.missu.utils.Constants;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    public static String userEmail = "email";

    //Firebase reference
    Firebase mFirebaseRef;
    FloatingActionButton fab;
    int currentPagePosition = 0;

    String mEncodedEmail;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(this);
        mViewPager.setOffscreenPageLimit(3);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (currentPagePosition) {
                    //click for new lost request
                    case 0: //my lost request tab
                    case 2: //lost tab
                        Intent intent1 = new Intent(MainActivity.this, LostRequestActivity.class);
                        startActivity(intent1);
                        break;

                    //click for new found request
                    case 1: //my found request tab
                    case 3: //found tab
                        Intent intent2 = new Intent(MainActivity.this, FoundRequestActivity.class);
                        startActivity(intent2);
                        break;
                }
            }
        });

        mFirebaseRef = new Firebase(Constants.FIREBASE_ROOT_URL);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        userEmail = sp.getString(Constants.USER_EMAIL, "User");

        mEncodedEmail = sp.getString(Constants.ENCODED_EMAIL, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_email).setTitle(userEmail);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_logout:
                logout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager.removeOnPageChangeListener(this);
    }

    /**
     * Unauthenticate from Firebase and from providers where necessary.
     */
    private void logout() {
        //if (LoginActivity.mAuthData != null) {
            /* logout of Firebase */
        mFirebaseRef.unauth();

        /* Update authenticated user and show login buttons */
//        LoginActivity loginActivity = new LoginActivity();
//        loginActivity.setAuthenticatedUser(null);

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
        //}
    }

    //onPageChangeListener method
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPagePosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_ENCODED_EMAIL = "encodedEmail";
        private String mEncodedEmail;
        private int tabPosition = 0;
        private LostListAdapter mLostListAdapter;
        private FoundListAdapter mFoundListAdapter;
        private ListView mListView;
        private ProgressBar pbLoadList;

        //flag to check which adapter was initialized
        private boolean mFlagLostAdapter = true;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, String encodedEmail) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(ARG_ENCODED_EMAIL, encodedEmail);
            fragment.setArguments(args);
            return fragment;
        }

        /**
         * Initialize instance variables with data from bundle
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                mEncodedEmail = getArguments().getString(ARG_ENCODED_EMAIL);
                tabPosition = getArguments().getInt(ARG_SECTION_NUMBER);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            /**
             * Initialize UI elements
             */
            initializeScreen(rootView);

            //set onclicklistener
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getContext(), PostDetailsActivity.class);

                    /* Get the list ID using the adapter's get ref method to get the Firebase
                     * ref and then grab the key.
                     */
                    String imageID;
                    boolean mFlagLostPost;

                    if (mFlagLostAdapter) {
                        imageID = mLostListAdapter.getRef(position).getKey();
                        mFlagLostPost = true;
                    } else {
                        imageID = mFoundListAdapter.getRef(position).getKey();
                        mFlagLostPost = false;
                    }

                    intent.putExtra(Constants.FIREBASE_PROPERTY_IMAGE_ID, imageID);
                    intent.putExtra(Constants.FIREBASE_PROPERTY_IS_LOST_POST, mFlagLostPost);
                    //start an activity for clicked post
                    startActivity(intent);
                }
            });

            return rootView;
        }

        /**
         * Updates the order of mListView onResume to handle sortOrderChanges properly
         */
        @Override
        public void onResume() {
            super.onResume();

            switch (tabPosition) {
                //My Lost Requests
                case 0:
                    Firebase firebaseMyLostRef = new Firebase(Constants.FIREBASE_URL_USERS).child(mEncodedEmail).child(Constants.FIREBASE_LOCATION_LOST);

                    //keep the list synced even if the user is offline
                    firebaseMyLostRef.keepSynced(true);

                    /**
                     * Create the adapter
                     */
                    mLostListAdapter = new LostListAdapter(getActivity(), LostRequestModel.class,
                            R.layout.single_lost_request_list, firebaseMyLostRef,
                            mEncodedEmail);


                    /**
                     * Set the adapter to the mListView
                     */
                    mListView.setAdapter(mLostListAdapter);
                    mFlagLostAdapter = true;
                    break;

                //My Found Requests
                case 1:
                    Firebase firebaseMyFoundRef = new Firebase(Constants.FIREBASE_URL_USERS).child(mEncodedEmail).child(Constants.FIREBASE_LOCATION_FOUND);

                    //keep the list synced even if the user is offline
                    firebaseMyFoundRef.keepSynced(true);

                    /**
                     * Create the adapter
                     */
                    mFoundListAdapter = new FoundListAdapter(getActivity(), FoundRequestModel.class,
                            R.layout.single_found_request_list, firebaseMyFoundRef,
                            mEncodedEmail);

                    /**
                     * Set the adapter to the mListView
                     */
                    mListView.setAdapter(mFoundListAdapter);
                    mFlagLostAdapter = false;
                    break;

                //All Lost Requests
                case 2:
                    Firebase firebaseLostRef = new Firebase(Constants.FIREBASE_URL_LOST);

                    //keep the list synced even if the user is offline
                    firebaseLostRef.keepSynced(true);

                    /**
                     * Create the adapter
                     */
                    mLostListAdapter = new LostListAdapter(getActivity(), LostRequestModel.class,
                            R.layout.single_lost_request_list, firebaseLostRef,
                            mEncodedEmail);

                    /**
                     * Set the adapter to the mListView
                     */
                    mListView.setAdapter(mLostListAdapter);
                    mFlagLostAdapter = true;
                    break;

                //All Found Requests
                case 3:
                    Firebase firebaseFoundRef = new Firebase(Constants.FIREBASE_URL_FOUND);

                    //keep the list synced even if the user is offline
                    firebaseFoundRef.keepSynced(true);

                    /**
                     * Create the adapter
                     */
                    mFoundListAdapter = new FoundListAdapter(getActivity(), FoundRequestModel.class,
                            R.layout.single_found_request_list, firebaseFoundRef,
                            mEncodedEmail);

                    /**
                     * Set the adapter to the mListView
                     */
                    mListView.setAdapter(mFoundListAdapter);
                    mFlagLostAdapter = false;
                    break;
            }
        }

        /**
         * Cleanup the adapters when activity is paused.
         */
        @Override
        public void onPause() {
            super.onPause();
            if (mFlagLostAdapter)
                mLostListAdapter.cleanup();
            else
                mFoundListAdapter.cleanup();
        }

        /**
         * Link listview and progressbar from XML
         */
        private void initializeScreen(View rootView) {
            mListView = (ListView) rootView.findViewById(R.id.list_all_requests);
            pbLoadList = (ProgressBar) rootView.findViewById(R.id.pbLoadList);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position, mEncodedEmail);

            return PlaceholderFragment.newInstance(position, mEncodedEmail);
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "My Lost Requests";
                case 1:
                    return "My Found Requests";
                case 2:
                    return "Lost";
                case 3:
                    return "Found";
            }
            return null;
        }
    }

}
