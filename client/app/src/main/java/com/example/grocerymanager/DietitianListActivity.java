package com.example.grocerymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;


public class DietitianListActivity extends AppCompatActivity {
    final static String TAG = "DietitianListActivity";
    private static final String SERVER_URL = "https://20.104.197.24/";
    private UserAdapter userAdapter;
    private List<UserData> userList = new ArrayList<>();
    private OkHttpClient client;

    //CHAT GPT USAGE: PARTIAL
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dietitian_list);

        ImageButton backButton = findViewById(R.id.backButton_diet);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        NetworkManager networkManager = new NetworkManager(this);
        client = networkManager.getClient();

        RecyclerView userRecyclerView = findViewById(R.id.user_recyclerview_diet);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // Set LayoutManager
        userAdapter = new UserAdapter(this, userList);
        userRecyclerView.setAdapter(userAdapter);

        fetchAvailableUsers();
    }

    //CHAT GPT USAGE: partial
    private void fetchAvailableUsers() {
        DietitianData dietitianData = SharedPrefManager.loadDietitianData(DietitianListActivity.this);
        int did = dietitianData.getDID();
        Log.d(TAG, "DID = " + did);
        String url = SERVER_URL + "get/usersForDietician/"+did; ///

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error fetching available users: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d(TAG, "Available Users: " + responseBody);

                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int uid = jsonObject.getInt("UID");
                            String firstName = jsonObject.getString("FirstName");
                            String lastName = jsonObject.getString("LastName");
                            String email = jsonObject.getString("Email");
                            String profile = jsonObject.getString("ProfileURL");
                            Uri uri = Uri.parse(profile);
                            UserData user = new UserData(firstName, lastName, email, uri, uid);
                            userList.add(user);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                userAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "Error: " + response.body().string());
                }
            }
        });
    }
}
