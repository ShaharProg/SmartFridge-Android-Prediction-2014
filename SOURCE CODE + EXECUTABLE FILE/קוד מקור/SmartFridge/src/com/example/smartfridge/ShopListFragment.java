package com.example.smartfridge;

import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

public class ShopListFragment extends Fragment {

	ListView mListView;
	List<ParseObject> mCategoryList;
	List<ParseObject> mProductList = new ArrayList<ParseObject>();

	boolean mIsFirstTime = true;
	View mView;
	private SharedPreferences mPref;
	private Editor mEditor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_shop_list, container,
				false);

		mView.getContext();
		mPref = mView
				.getContext()
				.getApplicationContext()
				.getSharedPreferences("SmartFridgePref",
						Context.MODE_PRIVATE);
		mEditor = mPref.edit();

		Button buyBtn = (Button)mView.findViewById(R.id.shop_list_btn);
		buyBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//ContentArrayAdapter adapter = (ContentArrayAdapter)mListView.getAdapter();
				int len = mListView.getCount();
				for (int i=1; i<len; ++i)
				{
					final ParseObject curProdShop = (ParseObject) mListView.getItemAtPosition(i);
					if( curProdShop.getBoolean("isChecked") )
					{
						//Add to InFridge
						ParseQuery<ParseObject> queryIF = ParseQuery.getQuery("InFridge");
						queryIF.whereEqualTo("user", ParseUser.getCurrentUser());
						queryIF.whereEqualTo("product", curProdShop);
						try {
							List<ParseObject> curFridgePrdct = queryIF.find();
							if (curFridgePrdct != null && curFridgePrdct.size()!=0)
							{
								int qntty = curFridgePrdct.get(0).getInt("quantity") + curProdShop.getInt("quantity");
								curFridgePrdct.get(0).put("quantity", qntty);
								curFridgePrdct.get(0).saveInBackground();
							}
							else
							{
								ParseObject curProdFridge = new ParseObject("InFridge");
								curProdFridge.put("user", ParseUser.getCurrentUser());
								curProdFridge.put("product", curProdShop);
								curProdFridge.put("quantity", curProdShop.get("quantity"));
								curProdFridge.saveInBackground();
							}
						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						//Delete from ShopList
						ParseQuery<ParseObject> querySL = ParseQuery.getQuery("ShopList");
						querySL.whereEqualTo("user", ParseUser.getCurrentUser());
						querySL.whereEqualTo("product", curProdShop);
						try {
							List<ParseObject> curShopPrdct = querySL.find();
							if (curShopPrdct != null && curShopPrdct.size()!=0)
							{
								curShopPrdct.get(0).deleteInBackground();
							}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						//Add to UserProducts
						ParseQuery<ParseObject> queryUP = ParseQuery.getQuery("UserProducts");
						queryUP.whereEqualTo("user", ParseUser.getCurrentUser());
						queryUP.whereEqualTo("product", curProdShop);
						queryUP.findInBackground(new FindCallback<ParseObject>() {
							public void done(List<ParseObject> userProductList,
									ParseException e) {
								if (e == null) {
									
									if (userProductList == null || userProductList.size()==0)
									{
										ParseObject curUsrPrdct = new ParseObject("UserProducts");
										curUsrPrdct.put("user", ParseUser.getCurrentUser());
										curUsrPrdct.put("product", curProdShop);
										curUsrPrdct.saveInBackground(); 
									}
									
								} else {
									// Log.d("score", "Error: " +
									// e.getMessage());
								}
							}
						});
						
						//Update to ProductHistory
						ParseQuery<ParseObject> queryPH = ParseQuery.getQuery("ProductHistory");
						queryPH.whereEqualTo("user", ParseUser.getCurrentUser());
						queryPH.whereEqualTo("product", curProdShop);
						queryPH.addDescendingOrder("createdAt");
						queryPH.setLimit(1);
						queryPH.findInBackground(new FindCallback<ParseObject>() {
							public void done(List<ParseObject> productHistoryList,
									ParseException e) {
								if (e == null) {
									
									if (productHistoryList != null && productHistoryList.size()!=0)
									{
										int tmpQntty = productHistoryList.get(0).getInt("wasBuy");
										productHistoryList.get(0).put("wasBuy", tmpQntty + curProdShop.getInt("quantity") );
										productHistoryList.get(0).saveInBackground();
									}
									
								} else {
									// Log.d("score", "Error: " +
									// e.getMessage());
								}
							}
						});

						
					}
				}
				mListView.findViewById(R.id.pull_to_refresh_header).callOnClick();
				Toast.makeText(v.getContext(), "קנייה בוצעה בהצלחה",
						Toast.LENGTH_SHORT).show();
				
			}
		});
		
		mListView = (ListView) mView.findViewById(R.id.shop_list);

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
		return mView;
		
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
							.getQuery("ShopList");
					query2.whereEqualTo("user", ParseUser.getCurrentUser());
					query2.include("product");
					query2.whereMatchesQuery("product", innerQuery2);
					List<ParseObject> shopList;
					try {
						shopList = query2.find();
						for (ParseObject inFridge : shopList) {
							ParseObject tmpProdct = inFridge
									.getParseObject("product");
							tmpProdct.put("quantity", inFridge.getInt("quantity"));
							productByCatagoryLists.get(tmpIndex).add(tmpProdct);
						}
					} catch (ParseException e) {
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
				ShopListFragment.this.mCategoryList = query1.find();
				// ----------------------
				setListByCategory();
				// ----------------------
			} catch (ParseException e) {
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
//		mEditor.putString("shopList", productsGson);
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
//		productsGson = mPref.getString("shopList", productsGson);
//
//		if ((!categoriesGson.isEmpty()) && (!productsGson.isEmpty())) {
//			mCategoryList = gson.fromJson(categoriesGson, listType);
//			mProductList = gson.fromJson(productsGson, listType);
//		}
//
//	}
}
