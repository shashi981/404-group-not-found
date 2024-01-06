package com.example.grocerymanager;

import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

public class ChatTest {

    @Rule
    public ActivityScenarioRule<MainActivityMock> activityRule =
            new ActivityScenarioRule<>(MainActivityMock.class);

    @Test
    public void mainTestCase(){
        signInUser();
        selectDietitian();
        sendMessage("Hello");
    }
    @Test
    public void testGetChatList() {
        signInUser();
    }
    @Test
    public void testGetSpecificDietician() {
        signInUser();
        selectDietitian();
    }

    @Test
    public void testSendMessage() {
        signInUser();
        selectDietitian();
        sendMessage("Hello This Is A Test");
    }

    private void signInUser() {
        // Step 1: User signs in using google sign in
        onView(withId(R.id.logo_login)).check(matches(withText("Grocery Manager")));

        onView(withId(R.id.user_button)).perform(ViewActions.click());
        onView(withText("Welcome Back!"))
                .check(matches(isDisplayed()));

        // Step 2: User opens the “Chat With a Dietician” screen
        onView(withId(R.id.chat_icon_home)).perform(ViewActions.click());
        for(int i = 0; i < 100; i++){
            onView(withId(R.id.menu_bar_icon_chat)).check(matches(isDisplayed()));
        }
    }

    private void selectDietitian() {
        for(int j = 0; j < 150; j++){
            onView(withId(R.id.edit_button)).check(doesNotExist());
            onView(withId(R.id.delete_button)).check(doesNotExist());
        }

        // Step 3: The app shows a list of certified dieticians
        onView(withId(R.id.dietician_recyclerview)).check(matches(isDisplayed()));


        // Step 4: The User selects one of the dieticians
        onView(withId(R.id.dietician_recyclerview))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));

        for(int j = 0; j < 150; j++){
            onView(withId(R.id.edit_button)).check(doesNotExist());
            onView(withId(R.id.delete_button)).check(doesNotExist());
        }
    }

    private void sendMessage(String message) {

        // Step 5: The app shows a chat interface with previous messages, a text input field and a icon button
        onView(withId(R.id.inputMessage)).check(matches(isDisplayed()));
        onView(withId(R.id.sendButton)).check(matches(isDisplayed()));

        // Step 6: The user inputs a text message in the text input field
        onView(withId(R.id.inputMessage)).perform(ViewActions.typeText(message));
        Espresso.closeSoftKeyboard();

        // Step 7: The user presses the send icon
        onView(withId(R.id.sendButton)).perform(ViewActions.click());
        for(int j = 0; j < 150; j++){
            onView(withId(R.id.edit_button)).check(doesNotExist());
            onView(withId(R.id.delete_button)).check(doesNotExist());
        }
    }

}