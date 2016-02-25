package com.waftinc.missu;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.waftinc.missu.login.LoginActivity;
import com.waftinc.missu.utils.Constants;

public class SecureLoginActivity extends AppCompatActivity {
    EditText etToken;
    Button bGenerateToken, bTokenLogin;
    ProgressBar pbToken;

    Firebase firebaseRef;
    private AuthData mAuthData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secure_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseRef = new Firebase(Constants.FIREBASE_ROOT_URL);

        etToken = (EditText) findViewById(R.id.etToken);
        bGenerateToken = (Button) findViewById(R.id.bGenerateToken);
        bTokenLogin = (Button) findViewById(R.id.bTokenLogin);
        pbToken = (ProgressBar) findViewById(R.id.pbToken);

        bGenerateToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pbToken.setVisibility(View.VISIBLE);

                firebaseRef.resetPassword("admin@example.com", new Firebase.ResultHandler() {
                    @Override
                    public void onSuccess() {
                        pbToken.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Ask Admin for token", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        pbToken.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Error: Please try again", Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

        bTokenLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbToken.setVisibility(View.VISIBLE);

                String token = etToken.getText().toString();
                if (!TextUtils.isEmpty(token)) {
                    firebaseRef.authWithPassword("admin@example.com", token, new Firebase.AuthResultHandler() {
                        @Override
                        public void onAuthenticated(AuthData authData) {

                            pbToken.setVisibility(View.GONE);

                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            pbToken.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Error: Please try again", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    pbToken.setVisibility(View.GONE);
                    etToken.requestFocus();
                    etToken.setHint("field required");
                    etToken.setHintTextColor(Color.RED);
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuthData = firebaseRef.getAuth();
        if (mAuthData != null) {

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
