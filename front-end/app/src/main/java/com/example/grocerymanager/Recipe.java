package com.example.grocerymanager;



import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private String recipeName;
    private List<Pair<String, String>> ingredients;
    private String instructions;
    private String youTubeLink;
    private int RID;

    public Recipe(String recipeName, String instructions, int RID, String youTubeLink) {
        this.recipeName = recipeName;
        this.instructions = instructions;
        this.youTubeLink = youTubeLink;
        this.RID = RID;
        ingredients = new ArrayList<>();
    }

    public Recipe(int RID){
        this.recipeName = null;
        this.instructions = null;
        this.youTubeLink = null;
        this.RID = RID;
        ingredients = new ArrayList<>();
    }

    public String getYouTubeLink() {
        return youTubeLink;
    }

    public void setYouTubeLink(String youTubeLink) {
        this.youTubeLink = youTubeLink;
    }

    public void setRecipeName(String recipeName){
        this.recipeName = recipeName;
    }
    public void setInstructions(String instructions){
        this.instructions = instructions;
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
    public String getFiveIngredientsAstString(){
        String ingredientsString = new String();
        int i = 0;
        for(Pair<String, String> ingredientPair : ingredients){
            if(i > 4){
                break;
            }
            ingredientsString = ingredientsString + ingredientPair.first + ": " + ingredientPair.second + "\n";
            i++;
        }
        return ingredientsString;
    }
    public int getRID(){
        return RID;
    }
}
