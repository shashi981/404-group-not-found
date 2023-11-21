package com.example.grocerymanager;

import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

public class EditAndDeleteItemTest {

    @Rule
    public ActivityTestRule<HomeActivity> activityRule = new ActivityTestRule<>(HomeActivity.class);

    @Test
    public void testManualEditSingleItem() {
        // Scenario Step 1: Open "Scan Groceries" screen
        // This step might involve launching the app and navigating to the "Scan Groceries" screen.
        onView(withId(R.id.scan_groceries_button_home)).perform(click());
        // Scenario Step 2: Check buttons are present on the screen
        onView(withId(R.id.manual_scanner)).check(matches(isDisplayed()));
        onView(withId(R.id.scanBarcodeButton)).check(matches(isDisplayed()));

        // Scenario Step 3: Open "Manually Add Items" screen via "Manually Add Items" button
        onView(withId(R.id.manual_scanner)).perform(click());

        // Scenario Steps 4-5: Check components on the "Manually Add Items" screen
        onView(withId(R.id.item_name)).check(matches(isDisplayed()));
        onView(withId(R.id.item_quantity)).check(matches(isDisplayed()));
        onView(withId(R.id.set_expiry_date)).check(matches(isDisplayed()));
        onView(withId(R.id.add_item_to_list)).check(matches(isDisplayed()));
        onView(withId(R.id.add_items_to_inventory)).check(matches(isDisplayed()));

        // Scenario Step 6: Input data and click "Add Item" button
        onView(withId(R.id.item_name)).perform(replaceText("apple"));
        onView(withId(R.id.item_quantity)).perform(replaceText("2"));
        onView(withId(R.id.set_expiry_date)).perform(click());


        // Click the "OK" button or perform any other action to confirm the date selection
        onView(withId(android.R.id.button1)).perform(click());

        // You might need to select a date from the date picker dialog here

        // Click the "Add Item" button
        onView(withId(R.id.add_item_to_list)).perform(click());

        // Scenario Step 6a: User does not fill out all inputs
        // For simplicity, let's assume the app displays a toast message in this case.

        // Comment out the following lines if your app doesn't show a toast for this scenario
        // onView(withText("Please fill out all inputs")).inRoot(withDecorView(not(activityRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));

        // Scenario Step 6a1: Check no additional components are added
//        onView(withId(R.id.item_name)).check(matches(isDisplayed()));
//        onView(withId(R.id.item_quantity)).check(matches(isDisplayed()));
//        onView(withId(R.id.set_expiry_date)).check(matches(isDisplayed()));
//        onView(withId(R.id.add_item_to_list)).check(matches(isDisplayed()));
//        onView(withId(R.id.save_button)).check(matches(isDisplayed()));

        // Scenario Step 7: Check if the new item is listed on the screen
        onView(withText("apple")).check(matches(isDisplayed()));
        onView(withText("Expiry Date: 2023-11-20")).check(matches(isDisplayed()));
        onView(withText("Quantity: 2")).check(matches(isDisplayed()));
        onView(withId(R.id.edit_button)).check(matches(isDisplayed()));
        onView(withId(R.id.delete_button)).check(matches(isDisplayed()));

        onView(withId(R.id.edit_button)).perform(click());

        onView(withId(R.id.edit_quantity)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_expiry_date_button)).check(matches(isDisplayed()));
        onView(withId(R.id.save_button)).check(matches(isDisplayed()));

        onView(withId(R.id.edit_quantity)).perform(replaceText("4"));

        onView(withId(R.id.edit_expiry_date_button)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.save_button)).perform(click());

        onView(withText("apple")).check(matches(isDisplayed()));
        onView(withText("Expiry Date: 2023-11-20")).check(matches(isDisplayed()));
        onView(withText("Quantity: 4")).check(matches(isDisplayed()));
        onView(withId(R.id.edit_button)).check(matches(isDisplayed()));
        onView(withId(R.id.delete_button)).check(matches(isDisplayed()));

        // Scenario Step 8: Click "Save To Inventory" button
//        onView(withId(R.id.save_button)).perform(click());

        // Scenario Step 9: Check if redirected to "Manage Inventory" screen
//        onView(withText("apple")).check(matches(isDisplayed()));
//        onView(withText("Expiry Date: 2023-11-09")).check(matches(isDisplayed()));
//        onView(withText("Quantity: 2")).check(matches(isDisplayed()));
//        onView(withId(R.id.edit_button)).check(matches(isDisplayed()));
//        onView(withId(R.id.delete_button)).check(matches(isDisplayed()));
//        onView(withId(R.id.add_items_inventory)).check(matches(isDisplayed()));
    }
    @Test
    public void testManualDeleteSingleItem() {
        // Scenario Step 1: Open "Scan Groceries" screen
        // This step might involve launching the app and navigating to the "Scan Groceries" screen.
        onView(withId(R.id.scan_groceries_button_home)).perform(click());
        // Scenario Step 2: Check buttons are present on the screen
        onView(withId(R.id.manual_scanner)).check(matches(isDisplayed()));
        onView(withId(R.id.scanBarcodeButton)).check(matches(isDisplayed()));

        // Scenario Step 3: Open "Manually Add Items" screen via "Manually Add Items" button
        onView(withId(R.id.manual_scanner)).perform(click());

        // Scenario Steps 4-5: Check components on the "Manually Add Items" screen
        onView(withId(R.id.item_name)).check(matches(isDisplayed()));
        onView(withId(R.id.item_quantity)).check(matches(isDisplayed()));
        onView(withId(R.id.set_expiry_date)).check(matches(isDisplayed()));
        onView(withId(R.id.add_item_to_list)).check(matches(isDisplayed()));
        onView(withId(R.id.add_items_to_inventory)).check(matches(isDisplayed()));

        // Scenario Step 6: Input data and click "Add Item" button
        onView(withId(R.id.item_name)).perform(replaceText("apple"));
        onView(withId(R.id.item_quantity)).perform(replaceText("2"));
        onView(withId(R.id.set_expiry_date)).perform(click());


        // Click the "OK" button or perform any other action to confirm the date selection
        onView(withId(android.R.id.button1)).perform(click());

        // You might need to select a date from the date picker dialog here

        // Click the "Add Item" button
        onView(withId(R.id.add_item_to_list)).perform(click());

        // Scenario Step 6a: User does not fill out all inputs
        // For simplicity, let's assume the app displays a toast message in this case.

        // Comment out the following lines if your app doesn't show a toast for this scenario
        // onView(withText("Please fill out all inputs")).inRoot(withDecorView(not(activityRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));

        // Scenario Step 6a1: Check no additional components are added
//        onView(withId(R.id.item_name)).check(matches(isDisplayed()));
//        onView(withId(R.id.item_quantity)).check(matches(isDisplayed()));
//        onView(withId(R.id.set_expiry_date)).check(matches(isDisplayed()));
//        onView(withId(R.id.add_item_to_list)).check(matches(isDisplayed()));
//        onView(withId(R.id.save_button)).check(matches(isDisplayed()));

        // Scenario Step 7: Check if the new item is listed on the screen
        onView(withText("apple")).check(matches(isDisplayed()));
        onView(withText("Expiry Date: 2023-11-20")).check(matches(isDisplayed()));
        onView(withText("Quantity: 2")).check(matches(isDisplayed()));
        onView(withId(R.id.edit_button)).check(matches(isDisplayed()));
        onView(withId(R.id.delete_button)).check(matches(isDisplayed()));

        onView(withId(R.id.delete_button)).perform(click());

        onView(withId(R.id.item_name)).check(matches(isDisplayed()));
        onView(withId(R.id.item_quantity)).check(matches(isDisplayed()));
        onView(withId(R.id.set_expiry_date)).check(matches(isDisplayed()));
        onView(withId(R.id.add_item_to_list)).check(matches(isDisplayed()));
        onView(withId(R.id.add_items_to_inventory)).check(matches(isDisplayed()));

        // Scenario Step 8: Click "Save To Inventory" button
//        onView(withId(R.id.save_button)).perform(click());

        // Scenario Step 9: Check if redirected to "Manage Inventory" screen
//        onView(withText("apple")).check(matches(isDisplayed()));
//        onView(withText("Expiry Date: 2023-11-09")).check(matches(isDisplayed()));
//        onView(withText("Quantity: 2")).check(matches(isDisplayed()));
//        onView(withId(R.id.edit_button)).check(matches(isDisplayed()));
//        onView(withId(R.id.delete_button)).check(matches(isDisplayed()));
//        onView(withId(R.id.add_items_inventory)).check(matches(isDisplayed()));
    }
    @Test
    public void testManualEditAndDeleteSingleItem() {
        // Scenario Step 1: Open "Scan Groceries" screen
        // This step might involve launching the app and navigating to the "Scan Groceries" screen.
        onView(withId(R.id.scan_groceries_button_home)).perform(click());
        // Scenario Step 2: Check buttons are present on the screen
        onView(withId(R.id.manual_scanner)).check(matches(isDisplayed()));
        onView(withId(R.id.scanBarcodeButton)).check(matches(isDisplayed()));

        // Scenario Step 3: Open "Manually Add Items" screen via "Manually Add Items" button
        onView(withId(R.id.manual_scanner)).perform(click());

        // Scenario Steps 4-5: Check components on the "Manually Add Items" screen
        onView(withId(R.id.item_name)).check(matches(isDisplayed()));
        onView(withId(R.id.item_quantity)).check(matches(isDisplayed()));
        onView(withId(R.id.set_expiry_date)).check(matches(isDisplayed()));
        onView(withId(R.id.add_item_to_list)).check(matches(isDisplayed()));
        onView(withId(R.id.add_items_to_inventory)).check(matches(isDisplayed()));

        // Scenario Step 6: Input data and click "Add Item" button
        onView(withId(R.id.item_name)).perform(replaceText("apple"));
        onView(withId(R.id.item_quantity)).perform(replaceText("2"));
        onView(withId(R.id.set_expiry_date)).perform(click());


        // Click the "OK" button or perform any other action to confirm the date selection
        onView(withId(android.R.id.button1)).perform(click());

        // You might need to select a date from the date picker dialog here

        // Click the "Add Item" button
        onView(withId(R.id.add_item_to_list)).perform(click());

        // Scenario Step 6a: User does not fill out all inputs
        // For simplicity, let's assume the app displays a toast message in this case.

        // Comment out the following lines if your app doesn't show a toast for this scenario
        // onView(withText("Please fill out all inputs")).inRoot(withDecorView(not(activityRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));

        // Scenario Step 6a1: Check no additional components are added
//        onView(withId(R.id.item_name)).check(matches(isDisplayed()));
//        onView(withId(R.id.item_quantity)).check(matches(isDisplayed()));
//        onView(withId(R.id.set_expiry_date)).check(matches(isDisplayed()));
//        onView(withId(R.id.add_item_to_list)).check(matches(isDisplayed()));
//        onView(withId(R.id.save_button)).check(matches(isDisplayed()));

        // Scenario Step 7: Check if the new item is listed on the screen
        onView(withText("apple")).check(matches(isDisplayed()));
        onView(withText("Expiry Date: 2023-11-20")).check(matches(isDisplayed()));
        onView(withText("Quantity: 2")).check(matches(isDisplayed()));
        onView(withId(R.id.edit_button)).check(matches(isDisplayed()));
        onView(withId(R.id.delete_button)).check(matches(isDisplayed()));

        onView(withId(R.id.edit_button)).perform(click());

        onView(withId(R.id.edit_quantity)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_expiry_date_button)).check(matches(isDisplayed()));
        onView(withId(R.id.save_button)).check(matches(isDisplayed()));

        onView(withId(R.id.edit_quantity)).perform(replaceText("4"));

        onView(withId(R.id.edit_expiry_date_button)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.save_button)).perform(click());

        onView(withText("apple")).check(matches(isDisplayed()));
        onView(withText("Expiry Date: 2023-11-20")).check(matches(isDisplayed()));
        onView(withText("Quantity: 4")).check(matches(isDisplayed()));
        onView(withId(R.id.edit_button)).check(matches(isDisplayed()));
        onView(withId(R.id.delete_button)).check(matches(isDisplayed()));

        onView(withId(R.id.delete_button)).perform(click());

        onView(withId(R.id.item_name)).check(matches(isDisplayed()));
        onView(withId(R.id.item_quantity)).check(matches(isDisplayed()));
        onView(withId(R.id.set_expiry_date)).check(matches(isDisplayed()));
        onView(withId(R.id.add_item_to_list)).check(matches(isDisplayed()));
        onView(withId(R.id.add_items_to_inventory)).check(matches(isDisplayed()));

        // Scenario Step 8: Click "Save To Inventory" button
//        onView(withId(R.id.save_button)).perform(click());

        // Scenario Step 9: Check if redirected to "Manage Inventory" screen
//        onView(withText("apple")).check(matches(isDisplayed()));
//        onView(withText("Expiry Date: 2023-11-09")).check(matches(isDisplayed()));
//        onView(withText("Quantity: 2")).check(matches(isDisplayed()));
//        onView(withId(R.id.edit_button)).check(matches(isDisplayed()));
//        onView(withId(R.id.delete_button)).check(matches(isDisplayed()));
//        onView(withId(R.id.add_items_inventory)).check(matches(isDisplayed()));
    }
}
