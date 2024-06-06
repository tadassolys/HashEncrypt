package com.tadas.hashencrypt;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    private EditText mainPasswordEditText, cryptPasswordEditText;
    private Button copyButton;
    private CheckBox showPasswordCheckBox;

    protected static String STATIC_SALT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize STATIC_SALT only if it's not stored in the Keystore
            STATIC_SALT = getStoredStaticSalt();
            if (STATIC_SALT == null) {
                STATIC_SALT = generateStaticSalt();
                storeStaticSalt(STATIC_SALT);
            }


        showPasswordCheckBox = findViewById(R.id.showPasswordCheckBox);
        mainPasswordEditText = findViewById(R.id.mainPasswordEditText);
        cryptPasswordEditText = findViewById(R.id.cryptPasswordEditText);
        copyButton = findViewById(R.id.copyButton);

        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                encryptPassword();
            }
        });

        showPasswordCheckBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            togglePasswordVisibility(isChecked);
        });
    }

    private void encryptPassword() {
        String mainPassword = mainPasswordEditText.getText().toString();

        if (!mainPassword.isEmpty()) {
            String combinedPassword = mainPassword + STATIC_SALT;   // Combine static salt and password for hashing

            String encryptedPassword = hashWithSHA256(combinedPassword);

            cryptPasswordEditText.setText(encryptedPassword);
            copyToClipboard(encryptedPassword);

            Toast.makeText(this, "Password hashed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
        }
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Hash", text);
        clipboard.setPrimaryClip(clip);
    }

    private String hashWithSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.substring(0, 12) + "A_"; // A_ ending for hash to be able to use in various pages when request for password is symbols or letters
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void togglePasswordVisibility(boolean showPassword) {
        int inputType = showPassword ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
        mainPasswordEditText.setInputType(inputType);
        mainPasswordEditText.setSelection(mainPasswordEditText.getText().length());
    }

    // Combine Android ID and model randomly in case user skips SALT input, then it is stored in shared prefs for future use.
    private String generateStaticSalt() {
        @SuppressLint("HardwareIds") String androidId = Settings.Secure.getString(
                LoginActivity.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
                String model = Build.MODEL;

        return randomCombine(androidId, model);
    }

    private static String randomCombine(String str1, String str2) {
        StringBuilder result = new StringBuilder();
        Random random = new Random();

        int length = Math.max(str1.length(), str2.length());

        for (int i = 0; i < length; i++) {
            char char1 = i < str1.length() ? str1.charAt(i) : 0;
            char char2 = i < str2.length() ? str2.charAt(i) : 0;

            char selectedChar = random.nextBoolean() ? char1 : char2;

            result.append(selectedChar);
        }

        return result.toString();
    }
    /* Encryption for storing SALT,
    protected void storeStaticSalt(String salt) {
        try {
            // Generate a symmetric key for encryption
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyGenerator.init(new KeyGenParameterSpec.Builder("static_salt_key",
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setUserAuthenticationRequired(false)
                    .build());
            SecretKey secretKey = keyGenerator.generateKey();

            Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_GCM + "/"
                    + KeyProperties.ENCRYPTION_PADDING_NONE);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedSalt = cipher.doFinal(salt.getBytes(StandardCharsets.UTF_8));
            String encodedSalt = Base64.encodeToString(encryptedSalt, Base64.DEFAULT);

            Context context = getApplicationContext();
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            EncryptedSharedPreferences sharedPreferences = (EncryptedSharedPreferences) EncryptedSharedPreferences.create(
                    "encrypted_prefs",
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            sharedPreferences.edit().putString("static_salt", encodedSalt).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected String getStoredStaticSalt() {
        try {
            Context context = getApplicationContext();
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            EncryptedSharedPreferences sharedPreferences = (EncryptedSharedPreferences) EncryptedSharedPreferences.create(
                    "encrypted_prefs",
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            String encodedSalt = sharedPreferences.getString("static_salt", null);

            if (encodedSalt != null) {
                Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                        + KeyProperties.BLOCK_MODE_GCM + "/"
                        + KeyProperties.ENCRYPTION_PADDING_NONE);
                cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
                byte[] encryptedSalt = Base64.decode(encodedSalt, Base64.DEFAULT);
                byte[] decryptedSalt = cipher.doFinal(encryptedSalt);
                return new String(decryptedSalt, StandardCharsets.UTF_8);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

      private SecretKey getSecretKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        return (SecretKey) keyStore.getKey("static_salt_key", null);
    }
     */
    protected void storeStaticSalt(String salt) {
        SharedPreferences sharedPreferences = getSharedPreferences("regular_prefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("static_salt", salt).apply();
    }

    protected String getStoredStaticSalt() {
        SharedPreferences sharedPreferences = getSharedPreferences("regular_prefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("static_salt", null);
    }


}

