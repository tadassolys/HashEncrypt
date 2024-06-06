package com.tadas.hashencrypt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SaltInputActivity extends LoginActivity {
    private EditText manualSalt;
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String FIRST_RUN_FLAG = "firstRunFlag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (isFirstRun()) {
            setContentView(R.layout.activity_salt_input);

            manualSalt = findViewById(R.id.manualSaltEditText);
            Button inputSaltButton = findViewById(R.id.inputSaltButton);
            Button skipButton = findViewById(R.id.skipButton);

            inputSaltButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String enteredSalt = manualSalt.getText().toString();
                    storeStaticSalt(enteredSalt);
                    navigateToLoginActivity(true);
                }
            });

            skipButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navigateToLoginActivity(true);
                }
            });
            setFirstRunFlag(false);
        } else {
            //it's not the first run, navigate to the login activity, Skip manual input
            navigateToLoginActivity(false);
        }
    }

    private void navigateToLoginActivity(boolean manualInput) {
        Intent loginIntent = new Intent(SaltInputActivity.this, LoginActivity.class);
        loginIntent.putExtra("manualInput", manualInput);
        startActivity(loginIntent);
        finish();
    }

    private boolean isFirstRun() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(FIRST_RUN_FLAG, true);
    }

    private void setFirstRunFlag(boolean value) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(FIRST_RUN_FLAG, value);
        editor.apply();
    }
}
