package com.example.grocerymanager;



import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Recipe {
    private String recipeName;
    private List<Pair<String, String>> ingredients;
    private String instructions;

    public Recipe(String recipeName, String instructions) {
        this.recipeName = recipeName;
        this.instructions = instructions;
        ingredients = new ArrayList<>();
    }

    public String getRecipeName() {
        return recipeName;
    }
    public String getInstructions(){
        return instructions;
    }
    public List<Pair<String, String>> getIngredients(){
        return ingredients;
    }
    public void addIngredient(String ingredient, String quantity){
        Pair<String, String> ingredientPair = new Pair<>(ingredient, quantity);
        ingredients.add(ingredientPair);
    }
    public String getIngredientsAsString(){
        String ingredientsString = new String();
        for(Pair<String, String> ingredientPair : ingredients){
            ingredientsString = ingredientsString + ingredientPair.first + ": " + ingredientPair.second + "\n";
        }
        return ingredientsString;
    }
}
