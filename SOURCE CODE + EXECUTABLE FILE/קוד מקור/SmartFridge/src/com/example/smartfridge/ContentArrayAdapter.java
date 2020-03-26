package com.example.smartfridge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ContentArrayAdapter extends ArrayAdapter<ParseObject> {

	MainActivity mMainActivty;
	private final Context mContext;
	private final List<ParseObject> mValues;
	private final List<Boolean> mCheckedObjs;
	private List<Bitmap> mImages;
	HashMap<ParseObject, Integer> mIdMap = new HashMap<ParseObject, Integer>();
	

//	public ContentArrayAdapter(Context context, int textViewResourceId,
//			List<String> objects) {
//		super(context, textViewResourceId, objects);
//		this.context = context;
//		this.values = objects;
//		for (int i = 0; i < objects.size(); ++i) {
//			mIdMap.put(objects.get(i), i);
//		}
//	}

	public ContentArrayAdapter(Context context, int textViewResourceId,
			List<ParseObject> productList) {
		super(context, textViewResourceId, productList);
		this.mContext = context;
		this.mValues = productList;
		
		mCheckedObjs = new ArrayList<Boolean>();
		mImages = new ArrayList<Bitmap>();
		

		for (int i = 0; i < productList.size(); ++i) {
			mCheckedObjs.add(false);
		}
		
		for (int i = 0; i < productList.size(); ++i) {
			mIdMap.put(this.mValues.get(i), i);
		}
	}

	public Boolean isCheckedAt(int position)
	{
		return mCheckedObjs.get(position);
	}
	
	public ParseObject getParseObjectAt(int position)
	{
		return mValues.get(position);
	}
	
	public int getItemsCount()
	{
		return mValues.size();
	}
	
	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMainActivty= (MainActivity)parent.getContext();
		
		final View rowView;
		String name = mValues.get(position).getString("name");
		String company = mValues.get(position).getString("company");
		String volume = mValues.get(position).getString("volume") + " " + mValues.get(position).getString("unit");
		int quantity = mValues.get(position).getInt("quantity");

		if (name.charAt(0) == '-') {
			rowView = inflater.inflate(R.layout.category_row, parent, false);
			TextView textView = (TextView) rowView
					.findViewById(R.id.categoryName);
			textView.setText(name);
		} else {
			rowView = inflater.inflate(R.layout.content_row, parent, false);
			ImageView productImage = (ImageView) rowView
					.findViewById(R.id.contentIcon);
			TextView textView = (TextView) rowView
					.findViewById(R.id.contentName);
			textView.setText(name);
			textView = (TextView) rowView
					.findViewById(R.id.contentCompany);
			textView.setText(company);
			textView = (TextView) rowView
					.findViewById(R.id.contentCapacity);
			textView.setText(volume);
			textView = (TextView) rowView
					.findViewById(R.id.contentQuantity);
			textView.setText(quantity + "x");
			ImageButton itemBtnPlus = (ImageButton)rowView.findViewById(R.id.contentBtnPlus);
			ImageButton itemBtnMinus = (ImageButton)rowView.findViewById(R.id.contentBtnMinus);
			CheckBox itemCB = (CheckBox)rowView.findViewById(R.id.contentCB);
			final EditText recipeCapacity = (EditText)rowView.findViewById(R.id.contentRecipeCapacity);
			
			ParseFile file = mValues.get(position).getParseFile("image");
			if (file != null)
			{
				try {
					mMainActivty.bytesIntoImageView(file.getData(), productImage);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			//String categoryName = ( (TextView) parent.findViewById(R.id.categoryName) ).getText().toString();
			
			final ParseObject selectedObj = (ParseObject)mValues.get(position);
			switch(mMainActivty.getCurrentInnerFragmentPos())
			{
			case 5:// 5 == RecipeMainFragment

				textView = (TextView) rowView
						.findViewById(R.id.contentQuantity);
				textView.setVisibility(View.VISIBLE);
				textView.setText(mValues.get(position).getString("amount"));
				
				textView = (TextView) rowView
						.findViewById(R.id.contentCapacity);
				textView.setVisibility(View.INVISIBLE);
				textView = (TextView) rowView
						.findViewById(R.id.contentExp);
				textView.setVisibility(View.INVISIBLE);
				itemBtnMinus.setVisibility(View.INVISIBLE);
				itemBtnPlus.setVisibility(View.INVISIBLE);
				break;
			case 9:// 9 == CreateRecipeFragment

				textView = (TextView) rowView
						.findViewById(R.id.contentExp);
				textView.setVisibility(View.INVISIBLE);
				textView = (TextView) rowView
						.findViewById(R.id.contentQuantity);
				textView.setVisibility(View.INVISIBLE);
				textView = (TextView) rowView
						.findViewById(R.id.contentCapacity);
				textView.setVisibility(View.INVISIBLE);
				itemBtnMinus.setVisibility(View.INVISIBLE);
				View v = (View)parent.getParent();
				if (v.getId() == R.id.fragment_add_to_list)
				{
					itemBtnPlus.setVisibility(View.VISIBLE);
					
					itemBtnPlus.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							
							CreateRecipeFragment recipeFragment = (CreateRecipeFragment) mMainActivty.getFragmentManager().findFragmentByTag("CreateRecipeFragment");
							if (recipeFragment.isVisible()) {
								   // add your code here
	
								recipeFragment.addContentToRecipe(selectedObj); 
	
								Toast.makeText(parent.getContext(), "הוספה בוצעה בהצלחה",
										Toast.LENGTH_SHORT).show();
								}
						}
					});
				}
				else if(v.getId() == R.id.fragment_create_recipe)
				{

					itemBtnPlus.setVisibility(View.INVISIBLE);
					textView = (TextView) rowView
							.findViewById(R.id.contentRecipeCapacityTitle);
					textView.setVisibility(View.VISIBLE);
					
					recipeCapacity.setVisibility(View.VISIBLE);
					recipeCapacity.setOnFocusChangeListener(new OnFocusChangeListener() {
						
						@Override
						public void onFocusChange(View v, boolean hasFocus) {
							if (hasFocus == false)
							{
								CreateRecipeFragment recipeFragment = (CreateRecipeFragment) mMainActivty.getFragmentManager().findFragmentByTag("CreateRecipeFragment");
								if (recipeFragment.isVisible()) {
									   // add your code here
		
									recipeFragment.amountChanged(position,recipeCapacity.getText().toString());
								}
							}
							
						}
					});
				}
				
				break;
			case 3:// 3 == AddToListFragment
				
				textView = (TextView) rowView
						.findViewById(R.id.contentQuantity);
				textView.setVisibility(View.INVISIBLE);
				itemBtnMinus.setVisibility(View.INVISIBLE);
				itemBtnPlus.setVisibility(View.VISIBLE);
				
				itemBtnPlus.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
		
						ParseQuery<ParseObject> query = ParseQuery
								.getQuery("ShopList");
						query.whereEqualTo("user",ParseUser.getCurrentUser());
						query.whereEqualTo("product", selectedObj);
						try {
							List<ParseObject> tmpPrdct = query.find();
							if (tmpPrdct == null || tmpPrdct.size() == 0)
							{
								ParseObject contentToAddObject = new ParseObject("ShopList");
								contentToAddObject.put("user", ParseUser.getCurrentUser());
								contentToAddObject.put("product", selectedObj);
								contentToAddObject.put("quantity", 1);
								contentToAddObject.saveInBackground();
							}
							else
							{
								tmpPrdct.get(0).increment("quantity");
								tmpPrdct.get(0).saveInBackground();
							}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Toast.makeText(parent.getContext(), "הוספה בוצעה בהצלחה",
								Toast.LENGTH_SHORT).show();
					}
				});
				
				break;
			case 2:// 2 == ShopListFragment
				
				itemCB.setVisibility(View.VISIBLE);
				itemCB.setChecked(mCheckedObjs.get(position));
				itemCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						mCheckedObjs.set(position, isChecked);
						mValues.get(position).put("isChecked", isChecked);
					}
				});
				
				//--
				itemBtnPlus.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
	
					ParseQuery<ParseObject> query = ParseQuery
							.getQuery("ShopList");
					query.whereEqualTo("user",ParseUser.getCurrentUser());
					query.whereEqualTo("product", selectedObj);
					try {
						List<ParseObject> tmpPrdct = query.find();
						if (tmpPrdct == null || tmpPrdct.size() == 0)
						{
							//empty case
						}
						else
						{
							tmpPrdct.get(0).increment("quantity");
							tmpPrdct.get(0).saveInBackground();
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					TextView textQnnty = (TextView) rowView
							.findViewById(R.id.contentQuantity);
					int quantity = mValues.get(position).getInt("quantity") + 1;
					textQnnty.setText(quantity + "x");
					mValues.get(position).put("quantity",quantity);
					
					Toast.makeText(parent.getContext(), "הוספה בוצעה בהצלחה",
							Toast.LENGTH_SHORT).show();
				}
			});
			
			itemBtnMinus.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
	
					ParseQuery<ParseObject> query = ParseQuery
							.getQuery("ShopList");
					query.whereEqualTo("user",ParseUser.getCurrentUser());
					query.whereEqualTo("product", selectedObj);
					try {
						List<ParseObject> tmpPrdct = query.find();
						if (tmpPrdct == null || tmpPrdct.size() == 0)
						{
							//empty case
						}
						else
						{
							int tmpQntty = tmpPrdct.get(0).getInt("quantity");
							if( tmpQntty == 1 )
							{
								tmpPrdct.get(0).deleteInBackground();
								mValues.remove(position);
								ContentArrayAdapter.this.notifyDataSetChanged();
							}
							else
							{
								tmpQntty--;
								tmpPrdct.get(0).put("quantity", tmpQntty);
								tmpPrdct.get(0).saveInBackground();

								TextView textQnnty = (TextView) rowView
										.findViewById(R.id.contentQuantity);
								textQnnty.setText(tmpQntty + "x");
								mValues.get(position).put("quantity",tmpQntty);
							}

							
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					Toast.makeText(parent.getContext(), "הסרה בוצעה בהצלחה",
							Toast.LENGTH_SHORT).show();
				}
			});
				//--
				
				
				break;
			case 1:// 1 == CurrentContentsFragment

				itemBtnPlus.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
		
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
						// set title
						alertDialogBuilder.setTitle("הוספת מוצר");
						// set dialog message
						alertDialogBuilder.setMessage("האם אתה בטוח שברצונך להוסיף מוצר זה?");
						alertDialogBuilder.setCancelable(true);
						alertDialogBuilder.setPositiveButton("כן",new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int id) {
									
									ParseQuery<ParseObject> query = ParseQuery
											.getQuery("InFridge");
									query.whereEqualTo("user",ParseUser.getCurrentUser());
									query.whereEqualTo("product", selectedObj);
									try {
										List<ParseObject> tmpPrdct = query.find();
										if (tmpPrdct == null || tmpPrdct.size() == 0)
										{
											//empty case
										}
										else
										{
											tmpPrdct.get(0).increment("quantity");
											tmpPrdct.get(0).saveInBackground();
										}
									} catch (ParseException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

									ParseQuery<ParseObject> query2 = ParseQuery
											.getQuery("ProductHistory");
									query2.whereEqualTo("user",ParseUser.getCurrentUser());
									query2.whereEqualTo("product", selectedObj);
									query2.addDescendingOrder("createdAt");
									query2.setLimit(1);
									try {
										List<ParseObject> tmpPrdct2 = query2.find();
										if (tmpPrdct2 == null || tmpPrdct2.size() == 0)
										{
											//empty case
										}
										else
										{
											tmpPrdct2.get(0).increment("wasBuy");
											tmpPrdct2.get(0).saveInBackground();
										}
									} catch (ParseException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
							
									TextView textQnnty = (TextView) rowView
											.findViewById(R.id.contentQuantity);
									int quantity = mValues.get(position).getInt("quantity") + 1;
									textQnnty.setText(quantity + "x");
									mValues.get(position).put("quantity",quantity);
									
									Toast.makeText(parent.getContext(), "הוספה בוצעה בהצלחה",
											Toast.LENGTH_SHORT).show();
								}
						});
						// create alert dialog
						AlertDialog alertDialog = alertDialogBuilder.create();
						// show it
						alertDialog.show();
					}
				});
				
				itemBtnMinus.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
		
						ParseQuery<ParseObject> query = ParseQuery
								.getQuery("InFridge");
						query.whereEqualTo("user",ParseUser.getCurrentUser());
						query.whereEqualTo("product", selectedObj);
						try {
							List<ParseObject> tmpPrdct = query.find();
							if (tmpPrdct == null || tmpPrdct.size() == 0)
							{
								//empty case
							}
							else
							{
								int tmpQntty = tmpPrdct.get(0).getInt("quantity");
								if( tmpQntty == 1 )
								{
									tmpPrdct.get(0).deleteInBackground();
									mValues.remove(position);
									ContentArrayAdapter.this.notifyDataSetChanged();
								}
								else
								{
									tmpQntty--;
									tmpPrdct.get(0).put("quantity", tmpQntty);
									tmpPrdct.get(0).saveInBackground();

									TextView textQnnty = (TextView) rowView
											.findViewById(R.id.contentQuantity);
									textQnnty.setText(tmpQntty + "x");
									mValues.get(position).put("quantity",tmpQntty);
								}

								
							}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						Toast.makeText(parent.getContext(), "הסרה בוצעה בהצלחה",
								Toast.LENGTH_SHORT).show();
					}
				});
				
				
				break;
			}
			
		}
		
		

		return rowView;
	}
	

	
	
	@Override
	public long getItemId(int position) {
		ParseObject item = getItem(position);
		return mIdMap.get(item);
	}
	
	

	@Override
	public boolean hasStableIds() {
		return true;
	}
}
