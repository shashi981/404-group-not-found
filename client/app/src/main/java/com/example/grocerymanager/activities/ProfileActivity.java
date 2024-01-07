package com.example.grocerymanager.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.grocerymanager.R;
import com.example.grocerymanager.helpers.ActivityLauncher;
import com.example.grocerymanager.helpers.SharedPrefManager;
import com.example.grocerymanager.models.NetworkManager;
import com.example.grocerymanager.models.UserData;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileActivity extends AppCompatActivity {

    final static String TAG = "ProfileActivity"; //identify where log is coming from
    private List<String> savePreferences;
    private List<String> loadPreferences;
    private UserData userData;
    private OkHttpClient client;



    //    ChatGPT Usage: No
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        loadPreferences = new ArrayList<>();

        NetworkManager networkManager = new NetworkManager(this);
        client = networkManager.getClient();

        String serverURL = "https://20.104.197.24/";
        userData = SharedPrefManager.loadUserData(ProfileActivity.this);
        int userID = userData.getUID();
        Request requestName = new Request.Builder()
                .url(serverURL + "get/pref?p1=" + userID)
                .build();
        client.newCall(requestName).enqueue(new Callback() {
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

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray jsonArray = new JSONArray(responseBody);

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String preference = jsonObject.getString("Pref");
                                    loadPreferences.add(preference);
                                }
                                setPreferences();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    // Handle unsuccessful response (e.g., non-200 status code)
                    Log.e(TAG, "Unsuccessful response: " + response.code());
                }
            }
        });



        ImageButton backIcon = findViewById(R.id.back_icon_profile);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });

        Button editPreferenceButton = findViewById(R.id.edit_dietary_restrictions);
        editPreferenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String serverURL = "https://20.104.197.24/";
                userData = SharedPrefManager.loadUserData(ProfileActivity.this);
                int userID = userData.getUID();
                Request requestName = new Request.Builder()
                        .url(serverURL + "delete/pref?p1=" + userID)
                        .build();
                client.newCall(requestName).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        // Handle network request failure
                        Log.e(TAG, "Request failed: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                           Log.d(TAG, "Successful response: " + response.code());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    displayPreferencePopup();
                                }
                            });

                        } else {
                            // Handle unsuccessful response (e.g., non-200 status code)
                            Log.e(TAG, "Unsuccessful response: " + response.code());
                        }
                    }
                });

            }
        });

        setDetails();


    }

    //    ChatGPT Usage: No
    private void setPreferences() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout preferencesContainer = findViewById(R.id.preference_container_profile);

                for (String preference : loadPreferences) {
                    TextView preferenceTextView = new TextView(ProfileActivity.this);
                    preferenceTextView.setText(preference);
                    preferenceTextView.setTextColor(getResources().getColor(R.color.dark_blue));
                    preferencesContainer.addView(preferenceTextView);
                }
            }
        });
    }

    //    ChatGPT Usage: No. Adapted/reused from a similar method from a different Activity.
    private void displayPreferencePopup() {
        Dialog dialog = new Dialog(ProfileActivity.this);
        dialog.setContentView(R.layout.restriction_template);
        savePreferences = new ArrayList<>();

        ToggleButton veganToggle = dialog.findViewById(R.id.toggle_vegan);
        ToggleButton vegetarianToggle = dialog.findViewById(R.id.toggle_vegetarian);
        ToggleButton nonDairyToggle = dialog.findViewById(R.id.toggle_non_dairy);
        veganToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    savePreferences.add("Vegan");
                }
                else{
                    savePreferences.remove("Vegan");
                }
            }
        });

        vegetarianToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    savePreferences.add("Vegetarian");
                }
                else{
                    savePreferences.remove("Vegetarian");
                }
            }
        });

        nonDairyToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    savePreferences.add("Non-dairy");
                }
                else{
                    savePreferences.remove("Non-dairy");
                }
            }
        });

        Button saveButton = dialog.findViewById(R.id.save_preferences_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (savePreferences.isEmpty()) {
                    dialog.dismiss();
                    ActivityLauncher.launchActivity(ProfileActivity.this, ProfileActivity.class);
                    finish();
                } else {


                    String serverURL = "https://20.104.197.24/";
                    userData = SharedPrefManager.loadUserData(ProfileActivity.this);
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    JSONObject postData = new JSONObject();
                    try {
                        postData.put("p1", userData.getUID());
                        postData.put("p2", new JSONArray(savePreferences));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    RequestBody body = RequestBody.create(JSON, postData.toString());

                    Request request = new Request.Builder()
                            .url(serverURL + "add/pref")
                            .post(body)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            // Handle failure
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                // Handle successful response
                                String responseData = response.body().string();
                                Log.d(TAG, "Response: " + responseData);
                                dialog.dismiss();
                                ActivityLauncher.launchActivity(ProfileActivity.this, ProfileActivity.class);
                                finish();
                            } else {
                                // Handle unsuccessful response
                                Log.e(TAG, "Unsuccessful response " + response.code());
                            }
                        }
                    });
                }
            }
        });


        dialog.show();
    }

    //    ChatGPT Usage: No.
    private void setDetails(){
        UserData userData = SharedPrefManager.loadUserData(ProfileActivity.this);
        TextView name = findViewById(R.id.google_name_profile);
        name.setText("Name: " + userData.getFirstName() + " " + userData.getLastName());
        TextView email = findViewById(R.id.google_email_profile);
        email.setText("Email: " + userData.getUserEmail());
        TextView userId = findViewById(R.id.user_id_profile);
        userId.setText("User ID: " + userData.getUID());
        ImageView profilePicture = findViewById(R.id.google_image_profile);
        Picasso.get().load(userData.getUserProfilePictureUrl()).into(profilePicture);

    }
}