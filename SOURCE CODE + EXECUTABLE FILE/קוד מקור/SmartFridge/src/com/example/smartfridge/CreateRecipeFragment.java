package com.example.smartfridge;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

public class CreateRecipeFragment extends Fragment {
	
	View mView;
	ParseObject mRecipe = new ParseObject("Recipe");
	List<ParseObject> mRecipeProduct = new ArrayList<ParseObject>();
	List<ParseObject> mProduct = new ArrayList<ParseObject>();
	int mState;
	
	EditText mRecipeName;
	EditText mRecipeAuthor;
	EditText mRecipePreparation;
	EditText mTextToFind;
	Button mSearchBtn;
	Button mScanBtn;
	Button mAddContentBtn;
	Button mNextBtn;
	TextView mListTitle;
	TextView mScanContent;
	TextView mScanFormat;
	ListView mProductListView;
	Dialog mDialog;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_create_recipe, container,
				false);

		mView = view;
		
		mState = 1;
		mRecipePreparation = (EditText)view.findViewById(R.id.CreateRecipe_preparation);
		mListTitle = (TextView)view.findViewById(R.id.textViewCreateRecipe_ContentListTitle);
		mRecipeName = (EditText)view.findViewById(R.id.textBoxCreateRecipe_name);
		mRecipeAuthor = (EditText)view.findViewById(R.id.textBoxCreateRecipe_authorName);
		mProductListView =(ListView)view.findViewById(R.id.CreateRecipe_contents_list); 
				
		mAddContentBtn = (Button)view.findViewById(R.id.buttonCreateRecipe_AddContent);
		mAddContentBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				// retrieve display dimensions
				Rect displayRectangle = new Rect();
				Window window = CreateRecipeFragment.this.getActivity().getWindow();
				window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

				// inflate and adjust layout
				LayoutInflater inflater = (LayoutInflater)CreateRecipeFragment.this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View layout = inflater.inflate(R.layout.fragment_add_to_list, null);
				layout.setMinimumWidth((int)(displayRectangle.width() * 0.9f));
				layout.setMinimumHeight((int)(displayRectangle.height() * 0.9f));
				
				// custom dialog
				mDialog = new Dialog(CreateRecipeFragment.this.getActivity());
				mDialog.setContentView(layout);
				mDialog.setTitle("הוסף מוצר");
				
				// set the custom dialog components - text, image and button
				mTextToFind = (EditText)mDialog.findViewById(R.id.textBoxAddToList_nameOrCompany);
				mSearchBtn = (Button)mDialog.findViewById(R.id.search_buttonAddToList);
				mScanBtn = (Button)mDialog.findViewById(R.id.scan_buttonAddToList);
				
				mSearchBtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(mSearchBtn.getWindowToken(), 0);
						
						ParseQuery<ParseObject> query = ParseQuery.getQuery("Product");
						query.whereContains("name", mTextToFind.getText().toString());
//						query.whereContains("company", mTextToFind.getText().toString());
						query.findInBackground(new FindCallback<ParseObject>() {
							public void done(List<ParseObject> productList,
									ParseException e) {
								if (e == null) {
									
									ListView listView = (ListView) mDialog.findViewById(R.id.AddToList_contents_list);
									ContentArrayAdapter adapter = new ContentArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, productList);
									listView.setAdapter(adapter);
								} else {
									// Log.d("score", "Error: " +
									// e.getMessage());
								}
							}
						});
					}
				});
				
//				mScanBtn.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//
//						InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//						imm.hideSoftInputFromWindow(mScanBtn.getWindowToken(), 0);
//						
//						// scan
//						
//						Activity activity  = (Activity)(v.getContext());
//						FragmentManager fm = activity.getFragmentManager();
//						Fragment fragment = fm.findFragmentById(R.id.InnerFragment);
//						IntentIntegrator scanIntegrator = new IntentIntegrator(activity, fragment);
//						scanIntegrator.initiateScan();
//					}
//				});
				
				mDialog.show();
			}
		});
		
		mNextBtn  = (Button)view.findViewById(R.id.buttonCreateRecipe_Next);
		mNextBtn.setOnClickListener(new OnClickListener() {
			

			@Override
			public void onClick(View v) {
				if (mState == 1)
				{
					mState = 2;
					mListTitle.setVisibility(View.INVISIBLE);
					mAddContentBtn.setVisibility(View.INVISIBLE);
					mRecipeName.setVisibility(View.INVISIBLE);
					mRecipeAuthor.setVisibility(View.INVISIBLE);
					mProductListView.setVisibility(View.INVISIBLE);
					mRecipePreparation.setVisibility(View.VISIBLE);
					mNextBtn.setText("סיום");
				}
				else if (mState == 2)
				{
					mRecipe.put("name", mRecipeName.getText().toString());
					mRecipe.put("author", mRecipeAuthor.getText().toString());
					mRecipe.put("preparation", mRecipePreparation.getText().toString());
					mRecipe.put("rating", 5);
					mRecipe.put("voted", 1);
					mRecipe.put("views", 1);
					mRecipe.put("user", ParseUser.getCurrentUser());
					mRecipe.saveInBackground();
					
					ParseObject recipeRating = new ParseObject("RecipeRating");
					recipeRating.put("user", ParseUser.getCurrentUser());
					recipeRating.put("recipe", mRecipe);
					recipeRating.put("rating", 5);
					recipeRating.saveInBackground();
					
					for (ParseObject curRecipeProduct : mRecipeProduct)
					{
						curRecipeProduct.saveInBackground();
					}


					Toast.makeText(mView.getContext(), "הוספה בוצעה בהצלחה",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		return view;
	}

	public void addContentToRecipe(ParseObject content)
	{
		ParseObject p = new ParseObject("RecipeProduct");
		p.put("recipe", mRecipe);
		p.put("product", content);
		p.put("amount", "amount");
		mRecipeProduct.add(p);
		
		mProduct.add(content);
		ListView listView = (ListView) mView.findViewById(R.id.CreateRecipe_contents_list);
		ContentArrayAdapter adapter = new ContentArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, mProduct);
		listView.setAdapter(adapter);
		
	}
	
	public void setDefaultLocale(Context context, Locale locale) {
        Locale.setDefault(locale);
        Configuration appConfig = new Configuration();
        appConfig.locale = locale;
        context.getResources().updateConfiguration(appConfig,
                context.getResources().getDisplayMetrics());
        System.out.println("trad" + locale.getLanguage());
    }

	public void amountChanged(int position, String text) {
		mRecipeProduct.get(position).put("amount", text);
	} 
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// super.onActivityResult(requestCode, resultCode, intent);
		// retrieve scan result
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);
		if (scanningResult != null) {
			// we have a result
			
			mScanFormat.setText("FORMAT: " + scanningResult.getFormatName());
			mScanContent.setText("CONTENT: " + scanningResult.getContents());
			

			ParseQuery<ParseObject> query = ParseQuery.getQuery("Product");
			query.whereContains("barcode_format", scanningResult.getFormatName());
			query.whereContains("barcode_content", scanningResult.getContents());
			query.findInBackground(new FindCallback<ParseObject>() {
				public void done(List<ParseObject> productList,
						ParseException e) {
					if (e == null) {

						ListView listView = (ListView) mDialog.findViewById(R.id.AddToList_contents_list);
						ContentArrayAdapter adapter = new ContentArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, productList);
						listView.setAdapter(adapter);
					} else {
						// Log.d("score", "Error: " +
						// e.getMessage());
					}
				}
			});
			
			
		} else {
			Toast toast = Toast.makeText(getActivity().getApplicationContext(),
					"No scan data received!", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
}
