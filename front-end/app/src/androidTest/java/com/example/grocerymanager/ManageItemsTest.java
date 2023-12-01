package com.example.grocerymanager;

import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ManageItemsTest {

    @Rule
    public ActivityScenarioRule<MainActivityMock> activityRule =
            new ActivityScenarioRule<>(MainActivityMock.class);

    @Test
    public void mainTestCase() {
        signInUser();
        addItemToInventory("apple", "2");
        saveItemToInventory("apple", "2");
    }

    @Test
    public void failureTestCase(){
        signInUser();
        addItemToInventory("apple");
    }

    @Test
    public void testAddItemToInventory() {
        signInUser();
        // Add the first item
        addItemToInventory("TestItem1", "5");
    }

    @Test
    public void testAddAndEdit() {
        signInUser();
        // Add the item
        addItemToInventory("TestItem1", "5");

        // Edit the added item
        editItemInInventory("TestItem1", "10");
    }

    @Test
    public void testAddAndDelete() {
        signInUser();
        // Add the item
        addItemToInventory("TestItem1", "5");

        // Delete the added item
        deleteItemInInventory("TestItem1");
    }

    @Test
    public void testAddEditAndDelete() {
        signInUser();
        // Add the item
        addItemToInventory("TestItem1", "5");

        editItemInInventory("TestItem1", "10");

        // Delete the added item
        deleteItemInInventory("TestItem1");

    }

    @Test
    public void testAddAndSave(){
        signInUser();
        // Add the first item
        addItemToInventory("TestItem1", "5");
        saveItemToInventory("TestItem1", "5");
    }

    @Test
    public void testAddSaveAndEdit(){
        signInUser();
        // Add the first item
        addItemToInventory("TestItem1", "5");
        saveItemToInventory("TestItem1", "5");
        editItemInInventory("TestItem1", "10");
    }

    @Test
    public void testAddSaveEditAndDelete(){
        signInUser();
        // Add the first item
        addItemToInventory("TestItem1", "5");
        saveItemToInventory("TestItem1", "5");
        editItemInInventory("TestItem1", "10");
        deleteItemInInventory("TestItem1");
    }


    private void saveItemToInventory(String itemName, String itemQuantity) {
        // Step 9: The user presses the “save to inventory” button
        onView(withId(R.id.add_items_to_inventory)).perform(ViewActions.click());

        // Step 10: The user is redirected to the “Manage Inventory” screen. The new item is listed on the screen
        onView(withText(itemName))
                .check(matches(isDisplayed()));


        String expectedDate = getCurrentDate();

        onView(withText("Expiry Date: " + expectedDate))
                .check(matches(isDisplayed()));

        onView(withText("Quantity: " + itemQuantity))
                .check(matches(isDisplayed()));
    }

    private void addItemToInventory(String itemName) {
        // Step 5: The app shows two text input fields, a button for entering the expiry date, a button to add an item, and a button to save to inventory
        onView(withId(R.id.item_name)).check(matches(isDisplayed()));
        onView(withId(R.id.item_quantity)).check(matches(isDisplayed()));
        onView(withId(R.id.set_expiry_date)).check(matches(isDisplayed()));
        onView(withId(R.id.add_item_to_list)).check(matches(isDisplayed()));
        onView(withId(R.id.add_items_to_inventory)).check(matches(isDisplayed()));

        // Step 7a: The user does not fill out all inputs.
        onView(withId(R.id.item_name)).perform(ViewActions.typeText(itemName));
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.set_expiry_date)).perform(ViewActions.click());

        onView(withText("OK")).perform(ViewActions.click());

        // Step 7: The user presses the “add item” button
        onView(withId(R.id.add_item_to_list)).perform(ViewActions.click());

        // Step 7a1: The app does not add the item to the list.
        onView(withId(R.id.item_name)).check(matches(isDisplayed()));
        onView(withId(R.id.item_quantity)).check(matches(isDisplayed()));
        onView(withId(R.id.set_expiry_date)).check(matches(isDisplayed()));
        onView(withId(R.id.add_item_to_list)).check(matches(isDisplayed()));
        onView(withId(R.id.add_items_to_inventory)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_button)).check(doesNotExist());
        onView(withId(R.id.delete_button)).check(doesNotExist());


    }
    private void addItemToInventory(String itemName, String quantity) {

        // Step 5: The app shows two text input fields, a button for entering the expiry date, a button to add an item, and a button to save to inventory
        onView(withId(R.id.item_name)).check(matches(isDisplayed()));
        onView(withId(R.id.item_quantity)).check(matches(isDisplayed()));
        onView(withId(R.id.set_expiry_date)).check(matches(isDisplayed()));
        onView(withId(R.id.add_item_to_list)).check(matches(isDisplayed()));
        onView(withId(R.id.add_items_to_inventory)).check(matches(isDisplayed()));




        // Step 6: The user inputs the new item and quantity into the two text input fields, and the expiry date via the button
        onView(withId(R.id.item_name)).perform(ViewActions.typeText(itemName));
        onView(withId(R.id.item_quantity)).perform(ViewActions.typeText(quantity));
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.set_expiry_date)).perform(ViewActions.click());

        onView(withText("OK")).perform(ViewActions.click());

        // Step 7: The user presses the “add item” button
        onView(withId(R.id.add_item_to_list)).perform(ViewActions.click());


        // Step 8: The screen refreshes and the new item is listed on the screen
        onView(withText(itemName))
                .check(matches(isDisplayed()));

        String expectedDate = getCurrentDate();

        onView(withText("Expiry Date: " + expectedDate))
                .check(matches(isDisplayed()));

        onView(withText("Quantity: " + quantity))
                .check(matches(isDisplayed()));
        onView(withId(R.id.edit_button)).check(matches(isDisplayed()));
        onView(withId(R.id.delete_button)).check(matches(isDisplayed()));
    }

    private void editItemInInventory(String itemName, String newQuantity) {

        // Check if the item is added to the inventory
        onView(withText(itemName))
                .check(matches(isDisplayed()));

        onView(withId(R.id.edit_button)).perform(ViewActions.click());

        // Edit the quantity
        onView(withId(R.id.edit_quantity))
                .perform(ViewActions.replaceText(newQuantity));

        Espresso.closeSoftKeyboard();



        // Click the "Set Expiry Date" button
        onView(withId(R.id.edit_expiry_date_button)).perform(ViewActions.click());

        // Click the "OK" or "Done" button to select the current date
        onView(withText("OK")).perform(ViewActions.click());

        // Click the "Save" button
        onView(withId(R.id.save_button))
                .perform(ViewActions.click());

        // Verify that the item is updated in the inventory
        onView(withText("Quantity: " + newQuantity))
                .check(matches(isDisplayed()));

        // Get the current date in the desired format (yyyy-MM-dd)
        String expectedDate = getCurrentDate();

        // Check if the displayed date matches the expected date
        onView(withText("Expiry Date: " + expectedDate))
                .check(matches(isDisplayed()));
    }

    private void deleteItemInInventory(String itemName) {
        // Check if the item is added to the inventory
        onView(withText(itemName))
                .check(matches(isDisplayed()));

        onView(withId(R.id.delete_button)).perform(ViewActions.click());

        // Verify that the item is removed from the inventory
        onView(withText(itemName))
                .check(doesNotExist());
    }


    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    private void signInUser() {

        // Step 1: User signs in using google sign in
        onView(withId(R.id.logo_login)).check(matches(withText("Grocery Manager")));
        onView(withId(R.id.user_button)).perform(ViewActions.click());
        onView(withText("Welcome Back!"))
                .check(matches(isDisplayed()));

        // Step 2: User opens the “Scan Groceries” screen
        onView(withId(R.id.scan_groceries_button_home)).perform(ViewActions.click());

        // Step 3: The app shows two options: Scan item UPC code and Manually add items.
        onView(withId(R.id.manual_scanner)).check(matches(isDisplayed()));
        onView(withId(R.id.scanBarcodeButton)).check(matches(isDisplayed()));

        // Step 4: User opens the “Manually Add Items” screen.
        onView(withId(R.id.manual_scanner)).perform(ViewActions.click());
    }
}