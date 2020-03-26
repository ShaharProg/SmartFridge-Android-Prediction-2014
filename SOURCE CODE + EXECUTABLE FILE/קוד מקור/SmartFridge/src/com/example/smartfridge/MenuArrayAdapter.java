package com.example.smartfridge;

import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.graphics.Color;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuArrayAdapter extends ArrayAdapter<Pair<String,Integer>> {

	private final Context mContext;
	private final List<Pair<String,Integer>> mValues;
	HashMap<Pair<String,Integer>, Integer> mIdMap = new HashMap<Pair<String,Integer>, Integer>();

	public MenuArrayAdapter(Context context, int textViewResourceId,
			List<Pair<String,Integer>> objects) {
		super(context, textViewResourceId, objects);
		this.mContext = context;
		this.mValues = objects;
		for (int i = 0; i < objects.size(); ++i) {
			mIdMap.put(objects.get(i), i);
		}
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View rowView;
		String label = mValues.get(position).first;
		Integer icon = mValues.get(position).second;
		
		if (label.charAt(0) == '-') {
			rowView = inflater.inflate(R.layout.category_row, parent, false);
			TextView textView = (TextView) rowView
					.findViewById(R.id.categoryName);
			textView.setText(label);
			textView.setTextColor(Color.BLACK);
			rowView.setBackgroundColor(Color.rgb(220, 244, 250));
		}
		else
		{
			rowView = inflater.inflate(R.layout.menu_row, parent, false);
			TextView textView = (TextView) rowView.findViewById(R.id.label);
			ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
			textView.setText(label);
			imageView.setImageResource(icon);
	//		String s = values.get(position);
		}
		return rowView;
	}

	@Override
	public long getItemId(int position) {
		Pair<String,Integer> item = getItem(position);
		return mIdMap.get(item);
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}
}
