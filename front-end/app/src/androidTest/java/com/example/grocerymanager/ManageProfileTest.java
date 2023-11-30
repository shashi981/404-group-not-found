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



    private void deleteUser(){
        onView(withId(R.id.menu_bar_icon_home)).perform(click());
        onView(withText("Settings")).inRoot(isPlatformPopup()).perform(click());

        for(int i = 0; i < 100; i++){
            onView(withId(R.id.delete_account_settings_user)).check(matches(isDisplayed()));
            onView(withId(R.id.request_dietitian_settings_user)).check(matches(isDisplayed()));
            onView(withId(R.id.sign_out_settings_user)).check(matches(isDisplayed()));
        }

        onView(withId(R.id.delete_account_settings_user)).perform(click());
        Espresso.onView(ViewMatchers.withText("Delete")).perform(ViewActions.click());


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
        Espresso.onView(ViewMatchers.withText("Delete")).perform(ViewActions.click());

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
        Espresso.onView(ViewMatchers.withId(R.id.admin_button)).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withText("Dashboard"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    private void signInDietician() {
        onView(withId(R.id.logo_login)).check(matches(withText("Grocery Manager")));
        Espresso.onView(ViewMatchers.withId(R.id.dietician_button)).perform(ViewActions.click());
        onView(withId(R.id.chatDietitianUserButton)).check(matches(isDisplayed()));
    }

    private void signInUser() {
        onView(withId(R.id.logo_login)).check(matches(withText("Grocery Manager")));
        Espresso.onView(ViewMatchers.withId(R.id.user_button)).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withText("Welcome Back!"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    private void requestDietician() {
        onView(withId(R.id.menu_bar_icon_home)).perform(click());
        onView(withText("Settings")).inRoot(isPlatformPopup()).perform(click());

        for(int i = 0; i < 100; i++){
            onView(withId(R.id.delete_account_settings_user)).check(matches(isDisplayed()));
            onView(withId(R.id.request_dietitian_settings_user)).check(matches(isDisplayed()));
            onView(withId(R.id.sign_out_settings_user)).check(matches(isDisplayed()));
        }

        onView(withId(R.id.request_dietitian_settings_user)).perform(click());
        Espresso.onView(ViewMatchers.withText("OK")).perform(ViewActions.click());

//        onView(withId(R.id.logo_login)).check(matches(withText("Grocery Manager")));
    }
}
