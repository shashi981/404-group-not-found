package com.example.grocerymanager;

import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.core.StringContains.containsString;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

public class ManageProfileTest {
    @Rule
    public ActivityScenarioRule<MainActivityMock> activityRule =
            new ActivityScenarioRule<>(MainActivityMock.class);


    @Test
    public void testSignInMock(){
        signInUser();
        signOutUser();
    }

    @Test
    public void testSignInMock2(){
        signInUser();
        requestDietician();
    }

    @Test
    public void testSignInMock3(){
        signInUser();
        deleteUser();
    }

    @Test
    public void testDieticianSignInMock(){
        signInDietician();
    }

    @Test
    public void testAdminSignInMock(){
        signInAdmin();
    }

    @Test
    public void testButtons(){
        signOutUser();
    }
    @Test
    public void testButtons2(){
        requestDietician();
    }
    @Test
    public void testButtons3(){
        deleteUser();
    }

    private void signInAdmin() {
        onView(withId(R.id.logo_login)).check(matches(withText("Grocery Manager")));
        Espresso.onView(ViewMatchers.withId(R.id.admin_button)).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withText("Dashboard"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    private void signInDietician() {
        Espresso.onView(ViewMatchers.withId(R.id.dietician_button)).perform(ViewActions.click());
        // Verify that the we are currently in Home Page
        Espresso.onView(ViewMatchers.withText("Welcome Back!"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    private void signInUser() {
        Espresso.onView(ViewMatchers.withId(R.id.user_button)).perform(ViewActions.click());
        // Verify that the we are currently in Home Page
        Espresso.onView(ViewMatchers.withText("Welcome Back!"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    private void signOutUser() {
        // Scenario Step 1: Open "Scan Groceries" screen
        // This step might involve launching the app and navigating to the "Scan Groceries" screen.
        onView(withId(R.id.menu_bar_icon_home)).perform(click());
        onView(withText("Settings")).inRoot(isPlatformPopup()).perform(click());



        // Scenario Step 2: Check buttons are present on the screen
        onView(withId(R.id.delete_account_settings_user)).check(matches(isDisplayed()));
        onView(withId(R.id.request_dietitian_settings_user)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_out_settings_user)).check(matches(isDisplayed()));

        // Scenario Step 3: Open "Manually Add Items" screen via "Manually Add Items" button
        onView(withId(R.id.sign_out_settings_user)).check(matches(isClickable())).perform(click());

        onView(withId(R.id.logo_login)).check(matches(withText("Grocery Manager")));



    }

    private void deleteUser() {
        // Scenario Step 1: Open "Scan Groceries" screen
        // This step might involve launching the app and navigating to the "Scan Groceries" screen.
//        onView(withId(R.id.menu_bar_icon_home)).perform(click());
//        onView(withText("Settings")).inRoot(isPlatformPopup()).perform(click());



        // Scenario Step 2: Check buttons are present on the screen
        onView(withId(R.id.delete_account_settings_user)).check(matches(isDisplayed()));
        onView(withId(R.id.request_dietitian_settings_user)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_out_settings_user)).check(matches(isDisplayed()));

        // Scenario Step 3: Open "Manually Add Items" screen via "Manually Add Items" button
        onView(withId(R.id.delete_account_settings_user)).perform(click());

        onView(withId(R.id.logo_login)).check(matches(withText("Grocery Manager")));


    }
    private void requestDietician() {
        // Scenario Step 1: Open "Scan Groceries" screen
        // This step might involve launching the app and navigating to the "Scan Groceries" screen.
//        onView(withId(R.id.menu_bar_icon_home)).perform(click());
//        onView(withText("Settings")).inRoot(isPlatformPopup()).perform(click());

        // Scenario Step 2: Check buttons are present on the screen
        onView(withId(R.id.delete_account_settings_user)).check(matches(isDisplayed()));
        onView(withId(R.id.request_dietitian_settings_user)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_out_settings_user)).check(matches(isDisplayed()));

        // Scenario Step 3: Open "Manually Add Items" screen via "Manually Add Items" button
        onView(withId(R.id.request_dietitian_settings_user)).perform(click());

        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.logo_login)).check(matches(withText("Grocery Manager")));

    }
}
