package com.example.grocerymanager.models;



import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private String recipeName;
    private List<Pair<String, String>> ingredients;
    private String instructions;
    private String youTubeLink;
    private int RID;

    //    ChatGPT Usage: No
    public Recipe(String recipeName, String instructions, int RID, String youTubeLink) {
        this.recipeName = recipeName;
        this.instructions = instructions;
        this.youTubeLink = youTubeLink;
        this.RID = RID;
        ingredients = new ArrayList<>();
    }

    //    ChatGPT Usage: No
    public Recipe(int RID){
        this.recipeName = null;
        this.instructions = null;
        this.youTubeLink = null;
        this.RID = RID;
        ingredients = new ArrayList<>();
    }

    //    ChatGPT Usage: No
    public String getYouTubeLink() {
        return youTubeLink;
    }

    //    ChatGPT Usage: No
    public void setYouTubeLink(String youTubeLink) {
        this.youTubeLink = youTubeLink;
    }

    //    ChatGPT Usage: No
    public void setRecipeName(String recipeName){
        this.recipeName = recipeName;
    }

    //    ChatGPT Usage: No
    public void setInstructions(String instructions){
        this.instructions = instructions;
    }

    //    ChatGPT Usage: No
    public String getRecipeName() {
        return recipeName;
    }

    //    ChatGPT Usage: No
    public String getInstructions(){
        return instructions;
    }

    //    ChatGPT Usage: No
    public List<Pair<String, String>> getIngredients(){
        return ingredients;
    }

    //    ChatGPT Usage: No
    public void addIngredient(String ingredient, String quantity){
        Pair<String, String> ingredientPair = new Pair<>(ingredient, quantity);
        ingredients.add(ingredientPair);
    }

    //    ChatGPT Usage: No
    public String getIngredientsAsString(){
        String ingredientsString = "";
        for(Pair<String, String> ingredientPair : ingredients){
            ingredientsString = ingredientsString + ingredientPair.first + ": " + ingredientPair.second + "\n";
        }
        return ingredientsString;
    }

    //    ChatGPT Usage: No
    public String getFiveIngredientsAsString(){
        String ingredientsString = "";
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

    //    ChatGPT Usage: No
    public int getRID(){
        return RID;
    }
}
