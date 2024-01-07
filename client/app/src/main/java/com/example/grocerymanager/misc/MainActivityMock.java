package com.example.grocerymanager.misc;

import static com.example.grocerymanager.misc.BackendPathingMock.getRequest;
import static com.example.grocerymanager.misc.BackendPathingMock.postRequest;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.grocerymanager.R;
import com.example.grocerymanager.activities.AdminActivity;
import com.example.grocerymanager.activities.DietitianActivity;
import com.example.grocerymanager.activities.HomeActivity;
import com.example.grocerymanager.components.CallbackListener;
import com.example.grocerymanager.helpers.ActivityLauncher;
import com.example.grocerymanager.helpers.SharedPrefManager;
import com.example.grocerymanager.helpers.TokenManager;
import com.example.grocerymanager.models.DietitianData;
import com.example.grocerymanager.models.UserData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivityMock extends AppCompatActivity {
    final static String TAG = "MainActivityMock"; //identify where log is coming from
    private TokenManager tm;


    //    ChatGPT Usage: Partial.
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String defProfile = "https://www.google.com/url?sa=i&url=https%3A%2F%2Fcommons.wikimedia.org%2Fwiki%2FFile%3AWindows_10_Default_Profile_Picture.svg&psig=AOvVaw2j1Lp-ZpTvx11OnK74KfzH&ust=1698889676555000&source=images&cd=vfe&opi=89978449&ved=0CBIQjRxqFwoTCOCPxq_XoYIDFQAAAAAdAAAAABAJ";
        Uri defProfileUri = Uri.parse(defProfile);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_mock);

        Button mockUserButton = findViewById(R.id.user_button);
        mockUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserType("MockUser@gmail.com", "Mock", "User", defProfileUri);
            }
        });
        Button mockDieticinaButton = findViewById(R.id.dietician_button);
        mockDieticinaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserType("MockDietician@gmail.com", "Mock", "Dietician", defProfileUri);
            }
        });
        Button mockAdminButton = findViewById(R.id.admin_button);
        mockAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserType("MockAdmin@gmail.com", "Mock", "Admin", defProfileUri);
            }
        });

        tm = new TokenManager(MainActivityMock.this);

    }

    private void getUserType(String email, String firstName, String lastName, Uri photoUri) {
        getRequest("/get/users_type?p1=" + email, this, new CallbackListener() {
            @Override
            public void onSuccess(JSONObject result) {
                    String userType = result.optString("Message", "");
                    launchUser(userType.trim(), email,firstName,lastName,photoUri);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "Request failed: " + errorMessage);
                signOut();
            }
        });
    }

    private void launchUser(String userType, String email, String firstName, String lastName, Uri photoUri) {
        switch (userType){
            case "Does not exist":
                launchNewUser(email,firstName,lastName,photoUri);
                break;
            case "User":
                launchExistingUser(email,firstName,lastName,photoUri);
                break;
            case "Dietician":
                launchDietician(email,firstName,lastName,photoUri);
                break;
            case "Admin":
                launchAdminIntent();
                break;
            default:
                Log.d(TAG, "Unsuccessful response: " + userType);
                signOut();
                break;
        }
    }
    private void launchNewUser(String email, String firstName, String lastName, Uri photoUri) {
        String token = tm.getToken();
        JSONObject jsonAccount = new JSONObject();
        try {
            jsonAccount.put("p1", firstName);
            jsonAccount.put("p2", lastName);
            jsonAccount.put("p3", email);
            jsonAccount.put("p4", photoUri);
            jsonAccount.put("p5", token);
            postRequest("/add/users", jsonAccount, this, new CallbackListener() {
                @Override
                public void onSuccess(JSONObject result) {
                    String responseBody = result.toString().trim();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray jsonArray = new JSONArray(responseBody);
                                if (jsonArray.length() > 0) {
                                    String numberAsString = jsonArray.getString(0);
                                    int number = Integer.parseInt(numberAsString);
                                    String defProfile = "https://www.google.com/url?sa=i&url=https%3A%2F%2Fcommons.wikimedia.org%2Fwiki%2FFile%3AWindows_10_Default_Profile_Picture.svg&psig=AOvVaw2j1Lp-ZpTvx11OnK74KfzH&ust=1698889676555000&source=images&cd=vfe&opi=89978449&ved=0CBIQjRxqFwoTCOCPxq_XoYIDFQAAAAAdAAAAABAJ";
                                    Uri defProfileUri = Uri.parse(defProfile);
                                    UserData userData;

                                    if(photoUri == null){

                                        userData = new UserData(firstName, lastName, email, defProfileUri, number);
                                    }
                                    else{
                                        userData = new UserData(firstName, lastName, email, photoUri, number);
                                    }

                                    SharedPrefManager.saveUserData(MainActivityMock.this, userData);
                                    launchHomeIntent();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                signOut();
                            }
                        }
                    });
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e(TAG, "Request failed: " + errorMessage);
                    signOut();
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON: " + e.getMessage());
            signOut();
        }
    }
    private void launchExistingUser(String email, String firstName, String lastName, Uri photoUri) {
        String token = tm.getToken();
        JSONObject jsonAccount = new JSONObject();
        try {
            jsonAccount.put("p1", email);
            jsonAccount.put("p2", token);
            Log.d(TAG, token);
            Log.d(TAG, email);
            postRequest("/get/users", jsonAccount, this, new CallbackListener() {
                @Override
                public void onSuccess(JSONObject result) {
                    String responseBody = result.toString().trim();
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

                                if(photoUri == null){

                                    userData = new UserData(firstName, lastName, email, defProfileUri, UID);
                                }
                                else{
                                    userData = new UserData(firstName, lastName, email, photoUri, UID);
                                }
                                SharedPrefManager.saveUserData(MainActivityMock.this, userData);
                                launchHomeIntent();
                            } catch (JSONException e) {
                                Log.e(TAG, "Failed to extract UID: " + e.getMessage());
                                signOut();
                            }
                        }
                    });
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e(TAG, "Request failed: " + errorMessage);
                    signOut();
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON: " + e.getMessage());
            signOut();
        }
    }
    private void launchDietician(String email, String firstName, String lastName, Uri photoUri) {
        String token = tm.getToken();
        JSONObject jsonAccount = new JSONObject();
        Log.d(TAG, "here");
        try {
            jsonAccount.put("p1", email);
            jsonAccount.put("p2", token);
            postRequest("/get/dietician", jsonAccount, this, new CallbackListener() {
                @Override
                public void onSuccess(JSONObject result) {
                    String responseBody = result.toString().trim();
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

                                if(photoUri == null){
                                    dietitianData = new DietitianData(firstName, lastName, email, defProfileUri, DID);
                                    Log.d(TAG, "here1");
                                }
                                else{
                                    dietitianData = new DietitianData(firstName, lastName, email, photoUri, DID);
                                    Log.d(TAG, "here2");
                                }
                                SharedPrefManager.saveDietitianData(MainActivityMock.this, dietitianData);
                                launchDietitianIntent();
                            } catch (JSONException e) {
                                Log.e(TAG, "Failed to extract DID: " + e.getMessage());
                                signOut();
                            }
                        }
                    });
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e(TAG, "Request failed: " + errorMessage);
                    signOut();
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON: " + e.getMessage());
            signOut();
        }
    }

    //  ChatGPT Usage: No.
    private void launchHomeIntent() {
        ActivityLauncher.launchActivity(MainActivityMock.this, HomeActivity.class);
        finish();
    }

    //  ChatGPT Usage: No.
    private void launchDietitianIntent() {
        ActivityLauncher.launchActivity(MainActivityMock.this, DietitianActivity.class);
        finish();
    }
    private void launchAdminIntent(){
        ActivityLauncher.launchActivity(MainActivityMock.this, AdminActivity.class);
        finish();
    }

    //    ChatGPT Usage: No. Reused from M1 tutorial & assignment
    private void signOut() {
        Log.d(TAG,"Server Unresponsive");
    }

}