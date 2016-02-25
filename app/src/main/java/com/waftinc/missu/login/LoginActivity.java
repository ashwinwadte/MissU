package com.waftinc.missu.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.waftinc.missu.MainActivity;
import com.waftinc.missu.R;
import com.waftinc.missu.model.User;
import com.waftinc.missu.utils.Constants;
import com.waftinc.missu.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();


    /**
     * Data from the authenticated user
     */
    public static AuthData mAuthData;
    int forgotPassword = -1;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button mEmailSignInButton;
    private TextView mForgotPassword;
    /**
     * Listener for Firebase session changes
     */
    private Firebase.AuthStateListener mAuthStateListener;
    private Firebase mFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        /* Create the Firebase ref that is used for all authentication with Firebase */
        mFirebaseRef = new Firebase(Constants.FIREBASE_ROOT_URL);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (forgotPassword == 0) {
                    sendResetEmail();
                } else if (forgotPassword == 1) {
                    loginWithTempToken();
                } else
                    attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mAuthStateListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                setAuthenticatedUser(authData);
            }
        };

        /* Check if the user is authenticated with Firebase already. If this is the case we can set the authenticated
         * user and hide any login buttons */
        mFirebaseRef.addAuthStateListener(mAuthStateListener);

        mForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);
        mForgotPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPasswordView.setVisibility(View.GONE);
                mEmailSignInButton.setText("Submit");
                mForgotPassword.setVisibility(View.GONE);
                forgotPassword = 0;
            }
        });
    }

    private void loginWithTempToken() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            mFirebaseRef.authWithPassword(email, password, new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    Intent intent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("password", password);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    showErrorDialog("Error: " + firebaseError.getMessage());
                }
            });
        }
    }

    private void sendResetEmail() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String email = mEmailView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // send reset email.
            showProgress(true);
            mFirebaseRef.resetPassword(email, new Firebase.ResultHandler() {
                @Override
                public void onSuccess() {
                    showProgress(false);
                    mEmailView.setText(email);
                    mEmailSignInButton.setText("Sign in");
                    mPasswordView.setVisibility(View.VISIBLE);
                    mPasswordView.setHint(getString(R.string.temp_token));
                    mPasswordView.requestFocus();
                    mForgotPassword.setVisibility(View.GONE);
                    forgotPassword = 1;
                    showErrorDialog("Reset Password", "Check your email and sign in with the temporary code given in the email.");
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    showErrorDialog("Error: " + firebaseError.getMessage());
                }
            });
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        mAuthData = mFirebaseRef.getAuth();
        if (mAuthData != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Once a user is logged in, take the mAuthData provided from Firebase and "use" it.
     */
    public void setAuthenticatedUser(AuthData authData) {
        String name = null;

        if (authData != null) {
            if (authData.getProvider().equals("password")) {
                name = authData.getProviderData().get("email").toString();
            } else {
                Log.e(TAG, "Invalid provider: " + authData.getProvider());
            }
        }

        this.mAuthData = authData;
        /* invalidate options menu to hide/show the logout button */
        // supportInvalidateOptionsMenu();
    }

    /**
     * Show errors to users
     */
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(R.drawable.ic_error_outline_24px)
                .show();
    }

    /**
     * Show message to users
     */
    private void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(R.drawable.ic_error_outline_24px)
                .show();
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);

        }
    }

    /* ************************************
     *              PASSWORD              *
     **************************************
     */
    public boolean loginWithPassword(final String email, final String password) {
        final boolean[] success = new boolean[1];

        mFirebaseRef.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                //success[0] = true;
                showProgress(false);
                String userEmail = authData.getProviderData().get("email").toString();
                String uid = authData.getUid();

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                SharedPreferences.Editor spe = sp.edit();

                spe.putString(Constants.USER_EMAIL, userEmail);
                spe.putString(Constants.ENCODED_EMAIL, Utils.encodeEmail(userEmail));
                spe.putString(Constants.UID, uid);
                spe.apply();

                //Save user info to Firebase
                String encodedEmail = Utils.encodeEmail(userEmail);

                Firebase userInfo = new Firebase(Constants.FIREBASE_URL_USERS).child(encodedEmail).child(Constants.FIREBASE_LOCATION_USER_INFO);

                /* Set raw version of date to the ServerValue.TIMESTAMP value and save into dateCreatedMap */
                HashMap<String, Object> timestampJoined = new HashMap<>();
                timestampJoined.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

                User newUser = new User(encodedEmail, timestampJoined);

                HashMap<String, Object> newUserMap = (HashMap<String, Object>) new ObjectMapper().convertValue(newUser, Map.class);
                userInfo.updateChildren(newUserMap);

                //start new activity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                if (firebaseError.getCode() == FirebaseError.USER_DOES_NOT_EXIST) {
                    mFirebaseRef.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                        @Override
                        public void onSuccess(Map<String, Object> result) {
                            Toast.makeText(getApplicationContext(), "Successfully created user account with uid: " + result.get("uid"), Toast.LENGTH_SHORT).show();
                            loginWithPassword(email, password);
                        }

                        @Override
                        public void onError(FirebaseError firebaseError) {
                            // there was an error
                            showProgress(false);
                            mPasswordView.requestFocus();
                            showErrorDialog("Error: " + firebaseError.getMessage());
                            //success[0] = false;
                        }
                    });
                } else {
                    showProgress(false);
                    mPasswordView.requestFocus();
                    showErrorDialog("Error: " + firebaseError.getMessage());

                    //success[0] = false;
                }

            }
        });

        return success[0];
    }


    private boolean isEmailValid(String email) {

        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                // Simulate network access.
                Thread.sleep(2000);
                return loginWithPassword(mEmail, mPassword);

            } catch (InterruptedException e) {
                return false;
            }

//            for (String credential : DUMMY_CREDENTIALS) {
//                String[] pieces = credential.split(":");
//                if (pieces[0].equals(mEmail)) {
//                    // Account exists, return true if the password matches.
//                    return pieces[1].equals(mPassword);
//                }
//            }

//            return true;

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            //showProgress(false);

//            if (success) {
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                intent.putExtra(BUNDLE_KEY, bundle);
//                startActivity(intent);
//                finish();
//            } else {
//                }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

