package com.example.grocerymanager;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

public class ChatTest {

    @Rule
    public ActivityScenarioRule<MainActivityMock> activityRule =
            new ActivityScenarioRule<>(MainActivityMock.class);

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
        onView(withId(R.id.logo_login)).check(matches(withText("Grocery Manager")));
        Espresso.onView(ViewMatchers.withId(R.id.user_button)).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withText("Welcome Back!"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withId(R.id.chat_icon_home)).perform(ViewActions.click());
        for(int i = 0; i < 10000; i++){
            onView(withId(R.id.menu_bar_icon_chat)).check(matches(isDisplayed()));
        }
    }

    private void selectDietitian() {
        // Scroll to and click on the dietitian in the RecyclerView
        onView(withId(R.id.dietician_recyclerview))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));

    }

    private void sendMessage(String message) {
        Espresso.onView(ViewMatchers.withId(R.id.inputMessage))
                .perform(ViewActions.replaceText(message));
        Espresso.onView(ViewMatchers.withId(R.id.sendButton)).perform(ViewActions.click());
    }

}
