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
import android.view.ViewGroup;
import android.widget.ListView;

public class FindRecipeByProductFragment extends Fragment {

	View mView;
	List<ParseObject> inFridgeList;
	List<ParseObject> recipeProductList;
	List<ParseObject> recipeList;
	
	ListView mRecipesListView;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_find_recipe_by_product, container,
				false);
		mView = view;

		ParseQuery<ParseObject> inFridgeQuery = new ParseQuery<ParseObject>("InFridge");
		inFridgeQuery.whereExists("objectId");
		try {
			inFridgeList = inFridgeQuery.find();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
		
		for (int i=0; i<inFridgeList.size(); ++i)
		{
			ParseQuery<ParseObject> tmpQuery = new ParseQuery<ParseObject>("RecipeProduct");
			tmpQuery.include("recipe");
			tmpQuery.whereEqualTo("product", inFridgeList.get(i));
			queries.add(tmpQuery);
		}
		
		ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
		
		try {
			recipeProductList = mainQuery.find();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		recipeList = new ArrayList<ParseObject>();
		

		for (int i=0; i<recipeProductList.size(); ++i)
		{
			ParseObject tmpRecipe = recipeProductList.get(i).getParseObject("recipe");
			if (recipeList.contains(tmpRecipe) == false)
			{
				recipeList.add(tmpRecipe);
			}
		}
		

		mRecipesListView = (ListView) mView.findViewById(R.id.FindRecipeByProduct_recipesList);
		RecipeArrayAdapter adapter = new RecipeArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, recipeList);
		mRecipesListView.setAdapter(adapter);
		
		return view;
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
