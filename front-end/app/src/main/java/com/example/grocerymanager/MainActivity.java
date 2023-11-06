package com.example.grocerymanager;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button signInButton;
    final static String TAG = "MainActivity"; //identify where log is coming from

    private GoogleSignInClient mGoogleSignInClient;

    private GoogleSignInAccount googleAccount;
    private int RC_SIGN_IN = 1;
    private NetworkManager networkManager;
    private OkHttpClient client;
    private TokenManager tm;



    //    ChatGPT Usage: Partial.
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        googleAccount = GoogleSignIn.getLastSignedInAccount(this);

        if (googleAccount != null) {
            updateUI(googleAccount);
        }

        signInButton = findViewById(R.id.sign_in_button_login);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

    }

    //    ChatGPT Usage: No.
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //    ChatGPT Usage: No. Reused from M1 tutorial & assignment
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }
    }

    //    ChatGPT Usage: No. Reused from M1 tutorial & assignment
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    //    ChatGPT Usage: No. Reused from M1 tutorial & assignment
    private void updateUI(GoogleSignInAccount account) {
        if(account == null){
            Log.d(TAG, "There is no user signed in!");
        }
        else{
            Log.d(TAG, "Pref Name: " + account.getDisplayName());
            Log.d(TAG, "Email: " + account.getEmail());
            Log.d(TAG, "Given Name: " + account.getGivenName());
            Log.d(TAG, "Family Name: " + account.getFamilyName());
            Log.d(TAG, "Display URI: " + account.getPhotoUrl());


            tm = new TokenManager(MainActivity.this);

            networkManager = new NetworkManager(this);
            client = networkManager.getClient();


            String serverURL = "https://20.104.197.24/";
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            Request requestCheck = new Request.Builder()
                    .url(serverURL + "get/users_type?p1=" + account.getEmail())
                    .build();
            client.newCall(requestCheck).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // Handle network request failure
                    Log.e(TAG, "Request failed: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        // Get the response body as a string
                        String responseBody = response.body().string();

                        // Process the response on the main UI thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Update the UI or perform any other necessary actions with the response
                                Log.d(TAG, "Response: " + responseBody);
                                if(responseBody.trim().equals("Does not exist")){
                                    String token = tm.getToken();
                                    JSONObject postData = new JSONObject();
                                    try {
                                        postData.put("p1", account.getGivenName());
                                        postData.put("p2", account.getFamilyName());
                                        postData.put("p3", account.getEmail());
                                        postData.put("p4", account.getPhotoUrl());
                                        postData.put("p5", token);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    RequestBody body = RequestBody.create(JSON, postData.toString());

                                    Request request = new Request.Builder()
                                            .url(serverURL + "add/users")
                                            .post(body)
                                            .build();

                                    client.newCall(request).enqueue(new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            // Handle network request failure
                                            Log.e(TAG, "Request failed: " + e.getMessage());
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            if (response.isSuccessful()) {
                                                // Get the response body as a string
                                                String responseBody = response.body().string();

                                                // Process the response on the main UI thread
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        // Update the UI or perform any other necessary actions with the response
                                                        Log.d(TAG, "Response: " + responseBody);
                                                        try {
                                                            JSONArray jsonArray = new JSONArray(responseBody);
                                                            if (jsonArray.length() > 0) {
                                                                String numberAsString = jsonArray.getString(0);
                                                                int number = Integer.parseInt(numberAsString);
                                                                String defProfile = "https://www.google.com/url?sa=i&url=https%3A%2F%2Fcommons.wikimedia.org%2Fwiki%2FFile%3AWindows_10_Default_Profile_Picture.svg&psig=AOvVaw2j1Lp-ZpTvx11OnK74KfzH&ust=1698889676555000&source=images&cd=vfe&opi=89978449&ved=0CBIQjRxqFwoTCOCPxq_XoYIDFQAAAAAdAAAAABAJ";
                                                                Uri defProfileUri = Uri.parse(defProfile);
                                                                UserData userData;

                                                                if(account.getPhotoUrl() == null){

                                                                    userData = new UserData(account.getGivenName(), account.getFamilyName(), account.getEmail(), defProfileUri, number);
                                                                }
                                                                else{
                                                                    userData = new UserData(account.getGivenName(), account.getFamilyName(), account.getEmail(), account.getPhotoUrl(), number);
                                                                }

                                                                SharedPrefManager.saveUserData(MainActivity.this, userData);
                                                                launchHomeIntent();

                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                            } else {
                                                // Handle unsuccessful response (e.g., non-200 status code)
                                                Log.e(TAG, "Unsuccessful response: " + response.code());
                                                signOut();

                                            }
                                        }
                                    });
                                }
                                else if(responseBody.trim().equals("User")){
                                    Log.d(TAG, "User Exists, Launching Home Intent");
                                    Request request = new Request.Builder()
                                            .url(serverURL + "get/users?p1=" + account.getEmail() + "&p2=" + tm.getToken())
                                            .build();

                                    client.newCall(request).enqueue(new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            // Handle network request failure
                                            Log.e(TAG, "Request failed: " + e.getMessage());
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            if (response.isSuccessful()) {
                                                // Get the response body as a string
                                                String responseBody = response.body().string();

                                                // Process the response on the main UI thread
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        // Update the UI or perform any other necessary actions with the response
                                                        Log.d(TAG, "Response: " + responseBody);
                                                        try {
                                                            JSONObject responseJson = new JSONObject(responseBody);
                                                            int UID = responseJson.getInt("UID");
                                                            // Now you have the UID as an integer
                                                            Log.d(TAG, "Extracted UID: " + UID);
                                                            String defProfile = "https://www.google.com/url?sa=i&url=https%3A%2F%2Fcommons.wikimedia.org%2Fwiki%2FFile%3AWindows_10_Default_Profile_Picture.svg&psig=AOvVaw2j1Lp-ZpTvx11OnK74KfzH&ust=1698889676555000&source=images&cd=vfe&opi=89978449&ved=0CBIQjRxqFwoTCOCPxq_XoYIDFQAAAAAdAAAAABAJ";
                                                            Uri defProfileUri = Uri.parse(defProfile);
                                                            UserData userData;

                                                            if(account.getPhotoUrl() == null){

                                                                userData = new UserData(account.getGivenName(), account.getFamilyName(), account.getEmail(), defProfileUri, UID);
                                                            }
                                                            else{
                                                                userData = new UserData(account.getGivenName(), account.getFamilyName(), account.getEmail(), account.getPhotoUrl(), UID);
                                                            }
                                                            SharedPrefManager.saveUserData(MainActivity.this, userData);
                                                            launchHomeIntent();
                                                        } catch (JSONException e) {
                                                            Log.e(TAG, "Failed to extract UID: " + e.getMessage());
                                                        }
                                                    }
                                                });
                                            } else {
                                                // Handle unsuccessful response (e.g., non-200 status code)
                                                Log.e(TAG, "Unsuccessful response: " + response.code());
                                                signOut();

                                            }
                                        }
                                    });
                                }
                                else if(responseBody.trim().equals("Dietician")){
                                    Log.d(TAG, "Dietician Exists, Launching Home Intent");
                                    Request request = new Request.Builder()
                                            .url(serverURL + "get/dietician?p1=" + account.getEmail() + "&p2=" + tm.getToken())
                                            .build();

                                    client.newCall(request).enqueue(new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            // Handle network request failure
                                            Log.e(TAG, "Request failed: " + e.getMessage());
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            if (response.isSuccessful()) {
                                                // Get the response body as a string
                                                String responseBody = response.body().string();

                                                // Process the response on the main UI thread
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        // Update the UI or perform any other necessary actions with the response
                                                        Log.d(TAG, "Response: " + responseBody);
                                                        try {
                                                            JSONObject responseJson = new JSONObject(responseBody);
                                                            int DID = responseJson.getInt("DID");
                                                            // Now you have the UID as an integer
                                                            Log.d(TAG, "Extracted DID: " + DID);
                                                            String defProfile = "https://www.google.com/url?sa=i&url=https%3A%2F%2Fcommons.wikimedia.org%2Fwiki%2FFile%3AWindows_10_Default_Profile_Picture.svg&psig=AOvVaw2j1Lp-ZpTvx11OnK74KfzH&ust=1698889676555000&source=images&cd=vfe&opi=89978449&ved=0CBIQjRxqFwoTCOCPxq_XoYIDFQAAAAAdAAAAABAJ";
                                                            Uri defProfileUri = Uri.parse(defProfile);
                                                            DietitianData dietitianData;

                                                            if(account.getPhotoUrl() == null){
                                                                dietitianData = new DietitianData(account.getGivenName(), account.getFamilyName(), account.getEmail(), defProfileUri, DID);
                                                                Log.d(TAG, "here1");
                                                            }
                                                            else{
                                                                dietitianData = new DietitianData(account.getGivenName(), account.getFamilyName(), account.getEmail(), account.getPhotoUrl(), DID);
                                                                Log.d(TAG, "here2");
                                                            }
                                                            SharedPrefManager.saveDietitianData(MainActivity.this, dietitianData);
                                                            launchDietitianIntent();
                                                        } catch (JSONException e) {
                                                            Log.e(TAG, "Failed to extract DID: " + e.getMessage());
                                                        }
                                                    }
                                                });
                                            } else {
                                                // Handle unsuccessful response (e.g., non-200 status code)
                                                Log.e(TAG, "Unsuccessful response: " + response.code());
                                                signOut();

                                            }
                                        }
                                    });
                                }
                                else if(responseBody.trim().equals("Admin")){
                                    ActivityLauncher.launchActivity(MainActivity.this, AdminActivity.class);
                                    finish();
                                }
                                else{
                                    Log.d(TAG, "User not created");
                                    signOut();

                                }
                            }
                        });
                    } else {
                        // Handle unsuccessful response (e.g., non-200 status code)
                        Log.e(TAG, "Unsuccessful response: " + response.code());
                        signOut();

                    }
                }
            });
        }
    }

    //  ChatGPT Usage: No.
    private void launchHomeIntent() {
        ActivityLauncher.launchActivity(MainActivity.this, HomeActivity.class);
        finish();
    }

    //  ChatGPT Usage: No.
    private void launchDietitianIntent() {
        ActivityLauncher.launchActivity(MainActivity.this, DietitianActivity.class);
        finish();
    }

    //    ChatGPT Usage: No. Reused from M1 tutorial & assignment
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        Log.d(TAG, "Log out successful");
                        googleAccount = null;
                    }
                });
    }

}