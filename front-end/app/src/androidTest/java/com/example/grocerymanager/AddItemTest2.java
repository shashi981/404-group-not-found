package com.example.grocerymanager;

import static org.hamcrest.Matchers.allOf;

import android.content.Context;
import android.net.Uri;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddItemTest2 {

    @Rule
    public ActivityScenarioRule<AddItemsActivity> activityRule =
            new ActivityScenarioRule<>(AddItemsActivity.class);

    @Test
    public void testAddMultipleItemsToInventory() {
        // Add the first item
        addItemToInventory("TestItem1", "5");
    }

    @Test
    public void testAddAndEdit() {
        // Add the item
        addItemToInventory("TestItem1", "5");

        // Edit the added item
        editItemInInventory("TestItem1", "10");
    }

    @Test
    public void testAddAndDelete() {
        // Add the item
        addItemToInventory("TestItem1", "5");

        // Delete the added item
        deleteItemInInventory("TestItem1");
    }

    @Test
    public void testAddEditAndDelete() {
        // Add the item
        addItemToInventory("TestItem1", "5");

        editItemInInventory("TestItem1", "10");

        // Delete the added item
        deleteItemInInventory("TestItem1");
    }

    private void addItemToInventory(String itemName, String quantity) {
        // Type text into item name and quantity
        Espresso.onView(ViewMatchers.withId(R.id.item_name)).perform(ViewActions.typeText(itemName));
        Espresso.onView(ViewMatchers.withId(R.id.item_quantity)).perform(ViewActions.typeText(quantity));

        // Click the "Set Expiry Date" button
        Espresso.onView(ViewMatchers.withId(R.id.set_expiry_date)).perform(ViewActions.click());

        // Click the "OK" or "Done" button to select the current date
        Espresso.onView(ViewMatchers.withText("OK")).perform(ViewActions.click());

        // Click the "Add Item" button
        Espresso.onView(ViewMatchers.withId(R.id.add_item_to_list)).perform(ViewActions.click());

        // Check if the item is added to the inventory
        Espresso.onView(ViewMatchers.withText(itemName))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Get the current date in the desired format (yyyy-MM-dd)
        String expectedDate = getCurrentDate();

        // Check if the displayed date matches the expected date
        Espresso.onView(ViewMatchers.withText("Expiry Date: " + expectedDate))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withText("Quantity: " + quantity))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    private void editItemInInventory(String itemName, String newQuantity) {

        // Check if the item is added to the inventory
        Espresso.onView(ViewMatchers.withText(itemName))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withId(R.id.edit_button)).perform(ViewActions.click());

        // Edit the quantity
        Espresso.onView(ViewMatchers.withId(R.id.edit_quantity))
                .perform(ViewActions.replaceText(newQuantity));

        // Click the "Set Expiry Date" button
        Espresso.onView(ViewMatchers.withId(R.id.edit_expiry_date_button)).perform(ViewActions.click());

        // Click the "OK" or "Done" button to select the current date
        Espresso.onView(ViewMatchers.withText("OK")).perform(ViewActions.click());

        // Click the "Save" button
        Espresso.onView(ViewMatchers.withId(R.id.save_button))
                .perform(ViewActions.click());

        // Verify that the item is updated in the inventory
        Espresso.onView(ViewMatchers.withText("Quantity: " + newQuantity))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Get the current date in the desired format (yyyy-MM-dd)
        String expectedDate = getCurrentDate();

        // Check if the displayed date matches the expected date
        Espresso.onView(ViewMatchers.withText("Expiry Date: " + expectedDate))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    private void deleteItemInInventory(String itemName) {
        // Check if the item is added to the inventory
        Espresso.onView(ViewMatchers.withText(itemName))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withId(R.id.delete_button)).perform(ViewActions.click());

        // Verify that the item is removed from the inventory
        Espresso.onView(ViewMatchers.withText(itemName))
                .check(ViewAssertions.doesNotExist());
    }


    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    private void mockUser(){
        String defProfile = "https://www.google.com/url?sa=i&url=https%3A%2F%2Fcommons.wikimedia.org%2Fwiki%2FFile%3AWindows_10_Default_Profile_Picture.svg&psig=AOvVaw2j1Lp-ZpTvx11OnK74KfzH&ust=1698889676555000&source=images&cd=vfe&opi=89978449&ved=0CBIQjRxqFwoTCOCPxq_XoYIDFQAAAAAdAAAAABAJ";
        Uri defProfileUri = Uri.parse(defProfile);
        UserData userData = new UserData("Louie", "Tang", "louietang2013@gmail.com", defProfileUri, 2);
        SharedPrefManager.saveUserData(this, userData);
    }
}
