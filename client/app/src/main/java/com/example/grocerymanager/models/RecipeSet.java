package com.example.grocerymanager.models;

import java.util.ArrayList;
import java.util.List;

public class RecipeSet {
    private List<Recipe> recipes;

    private static RecipeSet instance;

    public RecipeSet(){
        this.recipes = new ArrayList<Recipe>();
    }

    public static synchronized RecipeSet getInstance() {
        if (instance == null) {
            instance = new RecipeSet();
        }
        return instance;
    }

    public static boolean checkInstance(){
        if(instance == null){
            return false;
        }
        return true;
    }

    public static void clearInstance() {
        instance = null;
    }

    public void addRecipe(Recipe recipe){
        recipes.add(recipe);
    }

    public void clearRecipes(){
        recipes.clear();
    }

    public List<Recipe> getRecipes(){
        return recipes;
    }
}
