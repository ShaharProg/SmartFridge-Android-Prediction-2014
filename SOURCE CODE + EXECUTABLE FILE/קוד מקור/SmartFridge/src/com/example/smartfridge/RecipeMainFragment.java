package com.example.smartfridge;

import java.util.Locale;

import com.parse.ParseObject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RecipeMainFragment extends Fragment {

	View mView;
	FindRecipeFragment mFindRecipeFragment;
	RecipeFragment mRecipeFragment;
	ParseObject mSelectedRecipe;
	int mState;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_recipe_main, container,
				false);
		mView = view;
		mState =1;
		mFindRecipeFragment = new FindRecipeFragment();
		mRecipeFragment = new RecipeFragment();

		
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.replace(R.id.recipeMain_fragment, mFindRecipeFragment, "FindRecipeFragment");
		fragmentTransaction.addToBackStack( "FindRecipeFragment" );
		fragmentTransaction.commit();
		
		
		return view;
	}

	public void showRecipe(ParseObject recipe)
	{
		mState = 2;
		mSelectedRecipe = recipe;
		recipe.increment("views");
		recipe.saveInBackground();
		
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.replace(R.id.recipeMain_fragment, mRecipeFragment, "RecipeFragment");
		fragmentTransaction.addToBackStack( "RecipeFragment" );
		fragmentTransaction.commit();
	}
	
	public void setDefaultLocale(Context context, Locale locale) {
        Locale.setDefault(locale);
        Configuration appConfig = new Configuration();
        appConfig.locale = locale;
        context.getResources().updateConfiguration(appConfig,
                context.getResources().getDisplayMetrics());
        System.out.println("trad" + locale.getLanguage());
    }

	public ParseObject getSelectedRecipe() {
		return mSelectedRecipe;
	} 
	
}
