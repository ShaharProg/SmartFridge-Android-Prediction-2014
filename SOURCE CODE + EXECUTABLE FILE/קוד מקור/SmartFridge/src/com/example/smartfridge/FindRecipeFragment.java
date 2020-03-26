package com.example.smartfridge;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class FindRecipeFragment extends Fragment {
	
	View mView;
	View mViewRecipe;
	Button mSearchBtn;
	EditText mTextToSearch;
	List<ParseObject> mRecipes;
	ListView mRecipesListView;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_find_recipe, container,
				false);
		mView = view;

		
		mRecipes = new ArrayList<ParseObject>();
		
		fillListByRatingAndViews("");
		
		mTextToSearch = (EditText)mView.findViewById(R.id.FindRecipe_textToSearchET);
		mSearchBtn = (Button)mView.findViewById(R.id.FindRecipe_searchBtn);
		mSearchBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				fillListByRatingAndViews(mTextToSearch.getText().toString());
			}
		});
		
		mRecipesListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {


				RecipeMainFragment recipeMainFragment = (RecipeMainFragment) getFragmentManager().findFragmentByTag("RecipeMainFragment");
				if (recipeMainFragment.isVisible()) {
					   // add your code here
					recipeMainFragment.showRecipe(mRecipes.get(position));
				}
				
			}
		});
		return view;
	}

	public void fillListByRatingAndViews(String text)
	{
		ParseQuery<ParseObject> query= new ParseQuery<ParseObject>("Recipe");
		query.whereContains("name", text);
		query.addDescendingOrder("rating");
		query.addDescendingOrder("views");
		query.setLimit(50);
		try {
			mRecipes = query.find();

			mRecipesListView = (ListView) mView.findViewById(R.id.FindRecipe_recipesList);
			RecipeArrayAdapter adapter = new RecipeArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, mRecipes);
			mRecipesListView.setAdapter(adapter);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
	}
	
	public void setDefaultLocale(Context context, Locale locale) {
        Locale.setDefault(locale);
        Configuration appConfig = new Configuration();
        appConfig.locale = locale;
        context.getResources().updateConfiguration(appConfig,
                context.getResources().getDisplayMetrics());
        System.out.println("trad" + locale.getLanguage());
    } 
	
}
