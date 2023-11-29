package com.example.grocerymanager;

import android.net.Uri;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class MockGoogleSignInAccount {

    public static GoogleSignInAccount createMockAccount() {
        // Replace these values with the desired mock account information
        String displayName = "John Doe";
        String email = "john.doe@example.com";
        String givenName = "John";
        String familyName = "Doe";
        String photoUrl = "https://example.com/photo.jpg";

        try {
            // Use reflection to access the private constructor of GoogleSignInAccount
            Constructor<GoogleSignInAccount> constructor =
                    GoogleSignInAccount.class.getDeclaredConstructor(
                            String.class, String.class, String.class, String.class, Uri.class);
            constructor.setAccessible(true);

            // Create a mock GoogleSignInAccount instance
            GoogleSignInAccount mockAccount = constructor.newInstance(
                    displayName, email, givenName, familyName, Uri.parse(photoUrl));

            return mockAccount;
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            // Handle the exception as needed
            return null;
        }
    }
}
