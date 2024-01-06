package com.example.grocerymanager;

import android.content.Context;
import android.util.Log;

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

public class BackendPathing {

    final static String TAG = "Pathing";
    private final static String serverURL = "https://20.104.197.24";

    public static void postRequest(String endpoint, JSONObject jsonData, Context context, final CallbackListener callbackListener) {
        NetworkManager networkManager = new NetworkManager(context);
        OkHttpClient client = networkManager.getClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonData.toString());

        Request requestCheck = new Request.Builder()
                .url(serverURL + endpoint)
                .post(body)
                .build();

        client.newCall(requestCheck).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle network request failure
                Log.e(TAG, "Request failed: " + e.getMessage());
                callbackListener.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if (!response.isSuccessful()) {
                        // Handle unsuccessful response (e.g., non-200 status code)
                        Log.e(TAG, "Unsuccessful response: " + response.code());
                        callbackListener.onFailure("Unsuccessful response: " + response.code());
                        return;
                    }

                    String responseBody = response.body().string();
                    Log.d(TAG, "Response: " + responseBody);

                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        callbackListener.onSuccess(jsonResponse);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                        callbackListener.onFailure("Error parsing JSON");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "IOException during response: " + e.getMessage());
                    callbackListener.onFailure("IOException during response");
                } finally {
                    if (response.body() != null) {
                        response.body().close();
                    }
                }
            }
        });
    }

    public static void getRequest(String endpoint, Context context, final CallbackListener callbackListener) {
        NetworkManager networkManager = new NetworkManager(context);
        OkHttpClient client = networkManager.getClient();

        Request requestCheck = new Request.Builder()
                .url(serverURL + endpoint)
                .build();

        client.newCall(requestCheck).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle network request failure
                Log.e(TAG, "Request failed: " + e.getMessage());
                callbackListener.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if (!response.isSuccessful()) {
                        // Handle unsuccessful response (e.g., non-200 status code)
                        Log.e(TAG, "Unsuccessful response: " + response.code());
                        callbackListener.onFailure("Unsuccessful response: " + response.code());
                        return;
                    }

                    String responseBody = response.body().string();
                    Log.d(TAG, "Response: " + responseBody);

                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        callbackListener.onSuccess(jsonResponse);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                        callbackListener.onFailure("Error parsing JSON");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "IOException during response: " + e.getMessage());
                    callbackListener.onFailure("IOException during response");
                } finally {
                    if (response.body() != null) {
                        response.body().close();
                    }
                }
            }
        });
    }

    public static void deleteRequest(String endpoint, Context context, final CallbackListener callbackListener) {
        NetworkManager networkManager = new NetworkManager(context);
        OkHttpClient client = networkManager.getClient();

        Request requestCheck = new Request.Builder()
                .url(serverURL + endpoint)
                .build();

        client.newCall(requestCheck).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle network request failure
                Log.e(TAG, "Request failed: " + e.getMessage());
                callbackListener.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if (!response.isSuccessful()) {
                        // Handle unsuccessful response (e.g., non-200 status code)
                        Log.e(TAG, "Unsuccessful response: " + response.code());
                        callbackListener.onFailure("Unsuccessful response: " + response.code());
                        return;
                    }

                    String responseBody = response.body().string();
                    Log.d(TAG, "Response: " + responseBody);

                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        callbackListener.onSuccess(jsonResponse);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                        callbackListener.onFailure("Error parsing JSON");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "IOException during response: " + e.getMessage());
                    callbackListener.onFailure("IOException during response");
                } finally {
                    if (response.body() != null) {
                        response.body().close();
                    }
                }
            }
        });
    }

    public static void putRequest(String endpoint, JSONObject jsonData, Context context, final CallbackListener callbackListener) {
        NetworkManager networkManager = new NetworkManager(context);
        OkHttpClient client = networkManager.getClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonData.toString());

        Request requestCheck = new Request.Builder()
                .url(serverURL + endpoint)
                .post(body)
                .build();

        client.newCall(requestCheck).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle network request failure
                Log.e(TAG, "Request failed: " + e.getMessage());
                callbackListener.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if (!response.isSuccessful()) {
                        // Handle unsuccessful response (e.g., non-200 status code)
                        Log.e(TAG, "Unsuccessful response: " + response.code());
                        callbackListener.onFailure("Unsuccessful response: " + response.code());
                        return;
                    }

                    String responseBody = response.body().string();
                    Log.d(TAG, "Response: " + responseBody);

                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        callbackListener.onSuccess(jsonResponse);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                        callbackListener.onFailure("Error parsing JSON");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "IOException during response: " + e.getMessage());
                    callbackListener.onFailure("IOException during response");
                } finally {
                    if (response.body() != null) {
                        response.body().close();
                    }
                }
            }
        });
    }
    public static void patchRequest(String endpoint, JSONObject jsonData, Context context, final CallbackListener callbackListener) {
        NetworkManager networkManager = new NetworkManager(context);
        OkHttpClient client = networkManager.getClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonData.toString());

        Request requestCheck = new Request.Builder()
                .url(serverURL + endpoint)
                .post(body)
                .build();

        client.newCall(requestCheck).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle network request failure
                Log.e(TAG, "Request failed: " + e.getMessage());
                callbackListener.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if (!response.isSuccessful()) {
                        // Handle unsuccessful response (e.g., non-200 status code)
                        Log.e(TAG, "Unsuccessful response: " + response.code());
                        callbackListener.onFailure("Unsuccessful response: " + response.code());
                        return;
                    }

                    String responseBody = response.body().string();
                    Log.d(TAG, "Response: " + responseBody);

                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        callbackListener.onSuccess(jsonResponse);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                        callbackListener.onFailure("Error parsing JSON");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "IOException during response: " + e.getMessage());
                    callbackListener.onFailure("IOException during response");
                } finally {
                    if (response.body() != null) {
                        response.body().close();
                    }
                }
            }
        });
    }

}
