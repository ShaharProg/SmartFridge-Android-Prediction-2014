package com.example.smartfridge;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

public class RecipeFragment extends Fragment {

	View mView;
	ImageView mTabContent;
	ImageView mTabPreparation;
	ImageView mTabBgContent;
	ImageView mTabBgPreparation;
	ParseObject mRecipe;
	TextView nameTV;
	TextView authorTV;
	TextView ratingValueTV;
	TextView votedValueTV;
	TextView viewsValueTV;
	TextView preparationTV;
	ListView contentListView;
	RatingBar ratingBar;
	
	List<ParseObject> mRecipeProduct;
	List<ParseObject> mProducts;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_recipe, container,
				false);
		mView = view;

		nameTV = (TextView)mView.findViewById(R.id.recipeName);
		authorTV = (TextView)mView.findViewById(R.id.recipeAuthor);
		ratingValueTV = (TextView)mView.findViewById(R.id.recipeRatingValue);
		votedValueTV = (TextView)mView.findViewById(R.id.recipeVotedValue);
		viewsValueTV = (TextView)mView.findViewById(R.id.recipeViewsValue);
		preparationTV = (TextView)mView.findViewById(R.id.recipePreparation);
		contentListView = (ListView)mView.findViewById(R.id.recipeContentsList);
		ratingBar = (RatingBar)mView.findViewById(R.id.recipeTabRatingBar);

		RecipeMainFragment recipeMainFragment = (RecipeMainFragment) getFragmentManager().findFragmentByTag("RecipeMainFragment");
		if (recipeMainFragment.isVisible()) {
			   // add your code here
			mRecipe = recipeMainFragment.getSelectedRecipe();
			
			nameTV.setText(mRecipe.getString("name"));
			authorTV.setText(mRecipe.getString("author"));
			ratingValueTV.setText(String.valueOf(mRecipe.getDouble("rating")));
			votedValueTV.setText(String.valueOf(mRecipe.getInt("voted")));
			viewsValueTV.setText(String.valueOf(mRecipe.getInt("views")));
			preparationTV.setText(mRecipe.getString("preparation"));
//			ratingBar.setRating((float)mRecipe.getDouble("rating"));
			

			ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("RecipeRating");
			query.whereEqualTo("user", ParseUser.getCurrentUser());
			query.whereEqualTo("recipe", mRecipe);
			try {
				List<ParseObject> preUserRating = query.find();
				if (preUserRating != null && preUserRating.size() > 0)
				{
					ratingBar.setRating((float)preUserRating.get(0).getDouble("rating"));
				}
				else
				{
					ratingBar.setRating(0);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("RecipeRating");
				query.whereEqualTo("user", ParseUser.getCurrentUser());
				query.whereEqualTo("recipe", mRecipe);
				try {
					List<ParseObject> preUserRating = query.find();
					if (preUserRating != null && preUserRating.size() > 0)
					{
						preUserRating.get(0).put("rating", rating);
						preUserRating.get(0).saveInBackground();
					}
					else
					{
						mRecipe.increment("voted");
						mRecipe.saveInBackground();
						ParseObject userRating = new ParseObject("RecipeRating");
						userRating.put("user", ParseUser.getCurrentUser());
						userRating.put("recipe", mRecipe);
						userRating.put("rating", rating);
						userRating.saveInBackground();
					}
					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		mTabContent = (ImageView)mView.findViewById(R.id.recipeTabContent);
		mTabPreparation = (ImageView)mView.findViewById(R.id.recipeTabPreparation);
		mTabBgContent = (ImageView)mView.findViewById(R.id.recipeTabBgContent);
		mTabBgPreparation = (ImageView)mView.findViewById(R.id.recipeTabBgPreparation);
		
		mTabContent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mTabPreparation.setScaleX((float) 0.8);
				mTabPreparation.setScaleY((float) 0.8);
				mTabContent.setScaleX((float) 1);
				mTabContent.setScaleY((float) 1);

				mTabBgPreparation.setScaleX((float) 0.8);
				mTabBgPreparation.setScaleY((float) 0.8);
				mTabBgContent.setScaleX((float) 1);
				mTabBgContent.setScaleY((float) 1);
				
				preparationTV.setVisibility(View.INVISIBLE);
				contentListView.setVisibility(View.VISIBLE);
				
			}
		});

		mTabPreparation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mTabContent.setScaleX((float) 0.8);
				mTabContent.setScaleY((float) 0.8);
				mTabPreparation.setScaleX((float) 1);
				mTabPreparation.setScaleY((float) 1);
				
				mTabBgContent.setScaleX((float) 0.8);
				mTabBgContent.setScaleY((float) 0.8);
				mTabBgPreparation.setScaleX((float) 1);
				mTabBgPreparation.setScaleY((float) 1);
				
				preparationTV.setVisibility(View.VISIBLE);
				contentListView.setVisibility(View.INVISIBLE);
			}
		});
		
		fillContentList();
		
		return view;
	}

	public void fillContentList()
	{	
		mProducts = new ArrayList<ParseObject>();
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("RecipeProduct");
		query.whereEqualTo("recipe", mRecipe);
		query.include("product");
		try {
			mRecipeProduct = query.find();
			for (ParseObject recipeProduct : mRecipeProduct)
			{
				ParseObject tmp = recipeProduct.getParseObject("product");
				tmp.put("amount", recipeProduct.getString("amount"));
				mProducts.add(tmp);
			}
			

			ListView listView = (ListView) mView.findViewById(R.id.recipeContentsList);
			ContentArrayAdapter adapter = new ContentArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, mProducts);
			listView.setAdapter(adapter);
			
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
