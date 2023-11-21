package com.example.grocerymanager;

import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

public class ManageProfileTest {
    @Rule
    public ActivityTestRule<HomeActivity> activityRule = new ActivityTestRule<>(HomeActivity.class);

    @Test
    public void testSignOut() {
        // Scenario Step 1: Open "Scan Groceries" screen
        // This step might involve launching the app and navigating to the "Scan Groceries" screen.
        onView(withId(R.id.menu_bar_icon_home)).perform(click());
        onView(withText("Settings")).inRoot(isPlatformPopup()).perform(click());



        // Scenario Step 2: Check buttons are present on the screen
        onView(withId(R.id.delete_account_settings)).check(matches(isDisplayed()));
        onView(withId(R.id.request_dietitian_settings)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_out_settings)).check(matches(isDisplayed()));

        // Scenario Step 3: Open "Manually Add Items" screen via "Manually Add Items" button
        onView(withId(R.id.sign_out_settings)).perform(click());

        onView(withId(R.id.sign_in_button_login)).check(matches(isDisplayed()));
        onView(withText("grocerymanager")).check(matches(isDisplayed()));

    }

    @Test
    public void testDeleteAccount() {
        // Scenario Step 1: Open "Scan Groceries" screen
        // This step might involve launching the app and navigating to the "Scan Groceries" screen.
        onView(withId(R.id.menu_bar_icon_home)).perform(click());
        onView(withText("Settings")).inRoot(isPlatformPopup()).perform(click());



        // Scenario Step 2: Check buttons are present on the screen
        onView(withId(R.id.delete_account_settings)).check(matches(isDisplayed()));
        onView(withId(R.id.request_dietitian_settings)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_out_settings)).check(matches(isDisplayed()));

        // Scenario Step 3: Open "Manually Add Items" screen via "Manually Add Items" button
        onView(withId(R.id.delete_account_settings)).perform(click());

        onView(withId(R.id.sign_in_button_login)).check(matches(isDisplayed()));
        onView(withText("grocerymanager")).check(matches(isDisplayed()));

    }
    @Test
    public void testRequestDietitian() {
        // Scenario Step 1: Open "Scan Groceries" screen
        // This step might involve launching the app and navigating to the "Scan Groceries" screen.
        onView(withId(R.id.menu_bar_icon_home)).perform(click());
        onView(withText("Settings")).inRoot(isPlatformPopup()).perform(click());

        // Scenario Step 2: Check buttons are present on the screen
        onView(withId(R.id.delete_account_settings)).check(matches(isDisplayed()));
        onView(withId(R.id.request_dietitian_settings)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_out_settings)).check(matches(isDisplayed()));

        // Scenario Step 3: Open "Manually Add Items" screen via "Manually Add Items" button
        onView(withId(R.id.request_dietitian_settings)).perform(click());

        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.sign_in_button_login)).check(matches(isDisplayed()));
        onView(withText("grocerymanager")).check(matches(isDisplayed()));

    }
}
