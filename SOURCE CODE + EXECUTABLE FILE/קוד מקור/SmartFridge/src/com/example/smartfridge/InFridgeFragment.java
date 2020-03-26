package com.example.smartfridge;

import java.util.ArrayList;
import java.util.List;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

public class InFridgeFragment extends Fragment {

	ListView mListView;
	List<ParseObject> mCategoryList;
	List<ParseObject> mProductList = new ArrayList<ParseObject>();

	boolean mIsFirstTime = true;

	private SharedPreferences mPref;
	private Editor mEditor;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_current_contents,
				container, false);

		view.getContext();
		mPref = view
				.getContext()
				.getApplicationContext()
				.getSharedPreferences("SmartFridgePref",
						Context.MODE_PRIVATE);
		mEditor = mPref.edit();

		mListView = (ListView) view.findViewById(R.id.contents_list);

		// ---------------------------------------------------------------------------

		// Set a listener to be invoked when the list should be refreshed.
		((PullToRefreshListView) mListView)
				.setOnRefreshListener(new OnRefreshListener() {
					@Override
					public void onRefresh() {
						// Do work to refresh the list here.

						new GetDataTask().execute();
					}
				});

		// ---------------------------------------------------------------------------

		if (mIsFirstTime) {
//			loadListFromPref();

			mListView.findViewById(R.id.pull_to_refresh_header).callOnClick();
			mIsFirstTime = false;
		}
		

		ContentArrayAdapter adapter = new ContentArrayAdapter(
				getActivity(), android.R.layout.simple_list_item_1,
				mProductList);
		mListView.setAdapter(adapter);
		
		return view;
	}

	public void setListByCategory() {

		final List<List<ParseObject>> productByCatagoryLists = new ArrayList<List<ParseObject>>();

		List<Thread> threads = new ArrayList<Thread>();

		for (int i = 0; i < mCategoryList.size(); ++i) {

			final int tmpIndex = i;
			productByCatagoryLists.add(new ArrayList<ParseObject>());
			Thread tmpThread = new Thread(new Runnable() {
				@Override
				public void run() {

					ParseObject tmpCategoryName = new ParseObject(
							"category_title");
					tmpCategoryName.put("name", "-"
							+ mCategoryList.get(tmpIndex).get("category_name")
									.toString() + "-");
					productByCatagoryLists.get(tmpIndex).add(tmpCategoryName);

					ParseQuery<ParseObject> innerQuery2 = ParseQuery
							.getQuery("Product");
					innerQuery2.whereEqualTo("category",
							mCategoryList.get(tmpIndex));
					ParseQuery<ParseObject> query2 = ParseQuery
							.getQuery("InFridge");
					query2.whereEqualTo("user", ParseUser.getCurrentUser());
					query2.include("product");
					query2.whereMatchesQuery("product", innerQuery2);
					List<ParseObject> inFridgeList;
					try {
						inFridgeList = query2.find();
						for (ParseObject inFridge : inFridgeList) {
							ParseObject tmpProdct = inFridge
									.getParseObject("product");
							tmpProdct.put("quantity", inFridge.getInt("quantity"));
							productByCatagoryLists.get(tmpIndex).add(tmpProdct);
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});

			tmpThread.start();
			threads.add(tmpThread);

		}

		try {
			for (int i = 0; i < threads.size(); ++i) {
				threads.get(i).join();

				for (int j = 0; j < productByCatagoryLists.get(i).size(); ++j) {
					mProductList.add(productByCatagoryLists.get(i).get(j));
				}

			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		saveListToPref();

		// listView.setVisibility(View.VISIBLE);
	}

	// @Override
	// public void onDestroyView() {
	//
	//
	// listView = null;
	// categoryList = null;
	// productList = null;
	// loadingList = null;
	//
	// super.onDestroyView();
	// }

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {
		@Override
		protected void onPostExecute(String[] result) {
			// mListItems.addFirst("Added after refresh...");

			ContentArrayAdapter adapter = new ContentArrayAdapter(
					getActivity(), android.R.layout.simple_list_item_1,
					mProductList);
			mListView.setAdapter(adapter);

			// Call onRefreshComplete when the list has been refreshed.
			((PullToRefreshListView) mListView).onRefreshComplete();
			super.onPostExecute(result);
		}

		@Override
		protected String[] doInBackground(Void... arg0) {

			mCategoryList = null;
			mProductList = new ArrayList<ParseObject>();

			ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Category");
			query1.addAscendingOrder("order");
			query1.whereExists("category_name");

			try {
				InFridgeFragment.this.mCategoryList = query1.find();
				// ----------------------
				setListByCategory();
				// ----------------------
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}
	}

//	private void saveListToPref() {
//		Gson gson = new Gson();
//
//		String categoriesGson = gson.toJson(mCategoryList);
//		String productsGson = gson.toJson(mProductList);
//
//		mEditor.putString("categoryList", categoriesGson);
//		mEditor.putString("inFridgeList", productsGson);
//
//	}

//	private void loadListFromPref() {
//		Gson gson = new Gson();
//		String categoriesGson = "";
//		String productsGson = "";
//		Type listType = (Type) new TypeToken<ArrayList<ParseObject>>() {
//		}.getType();
//
//		categoriesGson = mPref.getString("categoryList", categoriesGson);
//		productsGson = mPref.getString("inFridgeList", productsGson);
//
//		if ((!categoriesGson.isEmpty()) && (!productsGson.isEmpty())) {
//			mCategoryList = gson.fromJson(categoriesGson, listType);
//			mProductList = gson.fromJson(productsGson, listType);
//		}
//
//	}
}
