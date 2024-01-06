package com.example.grocerymanager;

import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

public class ManageProfileTest {
    @Rule
    public ActivityScenarioRule<MainActivityMock> activityRule =
            new ActivityScenarioRule<>(MainActivityMock.class);

    @Test
    public void mainTestCase(){
        signInUser();
        requestDietician();
    }
    @Test
    public void failureTestCase(){
        signInUser();
        requestDieticianFailure();
    }

    @Test
    public void viewProfileTest(){
        signInUser();
        viewProfile();
    }


    @Test
    public void testLoginUser(){
        signInUser();
        signOutUser();
    }
    @Test
    public void testLoginDietician(){
        signInDietician();
        signOutDietician();
    }
    @Test
    public void testLoginAdmin(){
        signInAdmin();
        signOutAdmin();
    }

    @Test
    public void testRequestDietician(){
        signInUser();
        requestDietician();
    }

    @Test
    public void testDeleteUser(){
        signInUser();
        deleteUser();
    }
    @Test
    public void testDeleteDietician(){
        signInDietician();
        deleteDietician();
    }

    private void requestDieticianFailure() {
        // Step 2: User opens settings page
        onView(withId(R.id.menu_bar_icon_home)).perform(click());
        onView(withText("Settings")).inRoot(isPlatformPopup()).perform(click());

        // loop to stall for activity to fully load. Necessary work around for preventing misclick.
        for(int i = 0; i < 100; i++){
            // Step 3: The app shows three buttons: Request Dietician View, Sign Out, and Delete Account
            onView(withId(R.id.delete_account_settings_user)).check(matches(isDisplayed()));
            onView(withId(R.id.request_dietitian_settings_user)).check(matches(isDisplayed()));
            onView(withId(R.id.sign_out_settings_user)).check(matches(isDisplayed()));
        }
        // Step 4: User presses Request Dietician View
        onView(withId(R.id.request_dietitian_settings_user)).perform(click());
        // Step 5a: The user does not acknowledge the pop up (via cancel or back)
        onView(withText("Cancel")).perform(click());

        // Step 5a1: The pop up is dismissed and the user can interact with the settings page
        onView(withId(R.id.delete_account_settings_user)).check(matches(isDisplayed()));
        onView(withId(R.id.request_dietitian_settings_user)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_out_settings_user)).check(matches(isDisplayed()));
    }

    private void viewProfile(){
        onView(withId(R.id.menu_bar_icon_home)).perform(click());
        onView(withText("Profile")).inRoot(isPlatformPopup()).perform(click());

        onView(withText("Name: Mock User"))
                .check(matches(isDisplayed()));
        onView(withText("Email: MockUser@gmail.com"))
                .check(matches(isDisplayed()));
        onView(withText("User ID: 1"))
                .check(matches(isDisplayed()));
        onView(withText("Dietary Restrictions:"))
                .check(matches(isDisplayed()));
    }

    private void deleteUser(){
        onView(withId(R.id.menu_bar_icon_home)).perform(click());
        onView(withText("Settings")).inRoot(isPlatformPopup()).perform(click());

        for(int i = 0; i < 100; i++){
            onView(withId(R.id.delete_account_settings_user)).check(matches(isDisplayed()));
            onView(withId(R.id.request_dietitian_settings_user)).check(matches(isDisplayed()));
            onView(withId(R.id.sign_out_settings_user)).check(matches(isDisplayed()));
        }

        onView(withId(R.id.delete_account_settings_user)).perform(click());
        onView(withText("Delete")).perform(click());

        for(int j = 0; j < 150; j++){
            onView(withId(R.id.edit_button)).check(doesNotExist());
            onView(withId(R.id.delete_button)).check(doesNotExist());
        }

        onView(withId(R.id.logo_login)).check(matches(withText("Grocery Manager")));
    }

    private void deleteDietician(){
        onView(withId(R.id.menu_bar_icon_dietitian)).perform(click());
        onView(withText("Settings")).inRoot(isPlatformPopup()).perform(click());


        for(int i = 0; i < 150; i++){
            onView(withId(R.id.delete_account_settings_dietician)).check(matches(isDisplayed()));
            onView(withId(R.id.sign_out_settings_dietician)).check(matches(isDisplayed()));
        }

        onView(withId(R.id.delete_account_settings_dietician)).perform(click());
        onView(withText("Delete")).perform(click());

        for(int j = 0; j < 150; j++){
            onView(withId(R.id.edit_button)).check(doesNotExist());
            onView(withId(R.id.delete_button)).check(doesNotExist());
        }

        onView(withId(R.id.logo_login)).check(matches(withText("Grocery Manager")));
    }
    private void signOutUser() {
        onView(withId(R.id.menu_bar_icon_home)).perform(click());
        onView(withText("Settings")).inRoot(isPlatformPopup()).perform(click());

        for(int i = 0; i < 100; i++){
            onView(withId(R.id.delete_account_settings_user)).check(matches(isDisplayed()));
            onView(withId(R.id.request_dietitian_settings_user)).check(matches(isDisplayed()));
            onView(withId(R.id.sign_out_settings_user)).check(matches(isDisplayed()));
        }

        onView(withId(R.id.sign_out_settings_user)).perform(click());


        onView(withId(R.id.logo_login)).check(matches(withText("Grocery Manager")));
    }

    private void signOutDietician() {
        onView(withId(R.id.menu_bar_icon_dietitian)).perform(click());
        onView(withText("Settings")).inRoot(isPlatformPopup()).perform(click());


        for(int i = 0; i < 150; i++){
            onView(withId(R.id.delete_account_settings_dietician)).check(matches(isDisplayed()));
            onView(withId(R.id.sign_out_settings_dietician)).check(matches(isDisplayed()));
        }

        onView(withId(R.id.sign_out_settings_dietician)).perform(click());

        onView(withId(R.id.logo_login)).check(matches(withText("Grocery Manager")));
    }

    private void signOutAdmin() {
        onView(withId(R.id.menu_bar_icon_admin)).perform(click());
        onView(withText("Sign Out")).inRoot(isPlatformPopup()).perform(click());

        for(int i = 0; i < 300; i++) {
            onView(withId(R.id.sign_out_settings_admin)).check(matches(isDisplayed()));
        }
        onView(withId(R.id.sign_out_settings_admin)).perform(click());


        onView(withId(R.id.logo_login)).check(matches(withText("Grocery Manager")));
    }


    private void signInAdmin() {
        onView(withId(R.id.logo_login)).check(matches(withText("Grocery Manager")));
        onView(withId(R.id.admin_button)).perform(click());
        onView(withText("Dashboard"))
                .check(matches(isDisplayed()));
    }

    private void signInDietician() {
        onView(withId(R.id.logo_login)).check(matches(withText("Grocery Manager")));
        onView(withId(R.id.dietician_button)).perform(click());
        onView(withId(R.id.chatDietitianUserButton)).check(matches(isDisplayed()));
    }

    private void signInUser() {

        // Step 1: User signs in using google sign in
        onView(withId(R.id.logo_login)).check(matches(withText("Grocery Manager")));
        onView(withId(R.id.user_button)).perform(click());
        onView(withText("Welcome Back!"))
                .check(matches(isDisplayed()));
    }

    private void requestDietician() {
        // Step 2: User opens settings page
        onView(withId(R.id.menu_bar_icon_home)).perform(click());
        onView(withText("Settings")).inRoot(isPlatformPopup()).perform(click());

        // loop to stall for activity to fully load. Necessary work around for preventing misclick.
        for(int i = 0; i < 100; i++){
            // Step 3: The app shows three buttons: Request Dietician View, Sign Out, and Delete Account
            onView(withId(R.id.delete_account_settings_user)).check(matches(isDisplayed()));
            onView(withId(R.id.request_dietitian_settings_user)).check(matches(isDisplayed()));
            onView(withId(R.id.sign_out_settings_user)).check(matches(isDisplayed()));
        }
        // Step 4: User presses Request Dietician View
        onView(withId(R.id.request_dietitian_settings_user)).perform(click());
        // Step 5: User acknowledges the pop up message then presses OK
        onView(withText("OK")).perform(click());

        for(int j = 0; j < 150; j++){
            onView(withId(R.id.edit_button)).check(doesNotExist());
            onView(withId(R.id.delete_button)).check(doesNotExist());
        }

        // Step 6: The User is redirected to the login page.
        onView(withId(R.id.logo_login)).check(matches(withText("Grocery Manager")));
    }
}