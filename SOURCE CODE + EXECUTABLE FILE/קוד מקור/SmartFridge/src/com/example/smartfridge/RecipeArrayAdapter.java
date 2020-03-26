package com.example.smartfridge;

import java.util.HashMap;
import java.util.List;

import com.parse.ParseObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RecipeArrayAdapter extends ArrayAdapter<ParseObject> {

	private final Context mContext;
	private final List<ParseObject> mValues;
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

	public RecipeArrayAdapter(Context context, int textViewResourceId,
			List<ParseObject> recipeList) {
		super(context, textViewResourceId, recipeList);
		this.mContext = context;
		this.mValues = recipeList;
		
		for (int i = 0; i < recipeList.size(); ++i) {
			mIdMap.put(this.mValues.get(i), i);
		}
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.recipe_row, parent, false);
		
		String name = mValues.get(position).getString("name");
		String author = mValues.get(position).getString("author");
		Double rating = mValues.get(position).getDouble("rating");
		Integer views = mValues.get(position).getInt("views");
		Integer voted = mValues.get(position).getInt("voted");
		
		TextView recipeName = (TextView)rowView.findViewById(R.id.recipeRowName);
		recipeName.setText(name);
		TextView recipeAuthor = (TextView)rowView.findViewById(R.id.recipeRowAuthor);
		recipeAuthor.setText(author);
		TextView recipeRating = (TextView)rowView.findViewById(R.id.recipeRowRatingValue);
		recipeRating.setText(rating.toString());
		TextView recipeViews = (TextView)rowView.findViewById(R.id.recipeRowViewsValue);
		recipeViews.setText(views.toString());
		TextView recipeVoted = (TextView)rowView.findViewById(R.id.recipeRowVotedValue);
		recipeVoted.setText(voted.toString());

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
