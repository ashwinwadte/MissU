package com.waftinc.missu.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.waftinc.missu.MainActivity;
import com.waftinc.missu.MissUApplication;
import com.waftinc.missu.R;

public class ResetPasswordActivity extends AppCompatActivity {
    private static final String TAG = ResetPasswordActivity.class.getSimpleName();
    /**
     * Data from the authenticated user
     */
    public static AuthData mAuthData;
    //UI references
    EditText etNewPassword, etConfirmPassword;
    Button bChangePassword;
    String email;
    String password;
    private View mProgressView;
    private View mLoginFormView;
    /**
     * Listener for Firebase session changes
     */
    private Firebase.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");

        etNewPassword = (EditText) findViewById(R.id.etNewPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);

        bChangePassword = (Button) findViewById(R.id.bChangePassword);

        bChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        mProgressView = findViewById(R.id.login_progress);
        mLoginFormView = findViewById(R.id.login_form);

        mAuthStateListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                setAuthenticatedUser(authData);
            }
        };
    }

    /**
     * Once a user is logged in, take the mAuthData provided from Firebase and "use" it.
     */
    public void setAuthenticatedUser(AuthData authData) {

        this.mAuthData = authData;
    }


    private void resetPassword() {
        // Reset errors.
        etNewPassword.setError(null);
        etConfirmPassword.setError(null);

        // Store values at the time of the login attempt.
        final String newPassword = etNewPassword.getText().toString();
        final String confirmPassword = etConfirmPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(newPassword) && !isPasswordValid(newPassword)) {
            etNewPassword.setError(getString(R.string.error_invalid_password));
            focusView = etNewPassword;
            cancel = true;
        }

        //check for password mismatch
        if (!doPasswordsMatch(newPassword, confirmPassword)) {
            etConfirmPassword.setError(getString(R.string.error_password_mismatch));
            focusView = etConfirmPassword;
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

            MissUApplication.mFirebaseRef.changePassword(email, password, newPassword, new Firebase.ResultHandler() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getApplicationContext(), "Password changed successfully!", Toast.LENGTH_SHORT).show();
                    showProgress(false);

                    String userName = MissUApplication.mFirebaseRef.getAuth().getProviderData().get("email").toString();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                    intent.putExtra("userEmail", userName);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    showErrorDialog("Error: " + firebaseError.getMessage());
                }
            });
        }
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

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    private boolean doPasswordsMatch(String newPassword, String confirmPassword) {
        return newPassword.equals(confirmPassword);
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
}
