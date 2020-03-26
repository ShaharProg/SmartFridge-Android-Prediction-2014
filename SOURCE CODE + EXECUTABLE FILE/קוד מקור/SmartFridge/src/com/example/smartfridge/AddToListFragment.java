package com.example.smartfridge;

import java.util.List;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseException;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AddToListFragment extends Fragment {

	View mView;
	EditText mTextToFind;
	Button mSearchButton, mScanBarcode;
	TextView mContentTxt, mFormatTxt;
	String mScanFormat, mScanContent;
	ListView mContentList;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater
				.inflate(R.layout.fragment_add_to_list, container, false);

		mTextToFind = (EditText) mView.findViewById(R.id.textBoxAddToList_nameOrCompany);
		mSearchButton = (Button) mView.findViewById(R.id.search_buttonAddToList);
		mScanBarcode = (Button) mView.findViewById(R.id.scan_buttonAddToList);
		mContentTxt = (TextView) mView.findViewById(R.id.AddToList_scan_content);
		mFormatTxt = (TextView) mView.findViewById(R.id.AddToList_scan_format);
		mContentList = (ListView) mView.findViewById(R.id.AddToList_contents_list);
		
		mSearchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				

				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mSearchButton.getWindowToken(), 0);
				
				ParseQuery<ParseObject> query = ParseQuery.getQuery("Product");
				query.whereContains("name", mTextToFind.getText().toString());
				//query.whereContains("company", mTextToFind.getText().toString());
				query.findInBackground(new FindCallback<ParseObject>() {
					public void done(List<ParseObject> productList,
							ParseException e) {
						if (e == null) {
							
							ListView listView = (ListView) mView.findViewById(R.id.AddToList_contents_list);
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

		mTextToFind.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == EditorInfo.IME_NULL) {
							mSearchButton.callOnClick();
							return true;
						}
						return false;
					}
				});
		
		mScanBarcode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				

				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mScanBarcode.getWindowToken(), 0);
				
				// scan
				
				Activity activity  = (Activity)(v.getContext());
				FragmentManager fm = activity.getFragmentManager();
				Fragment fragment = fm.findFragmentById(R.id.InnerFragment);
				IntentIntegrator scanIntegrator = new IntentIntegrator(activity, fragment);
				scanIntegrator.initiateScan();
			}
		});


		
//		contentList.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
//
//
//				final ParseObject selectedObj = (ParseObject)parent.getItemAtPosition(position);
//				
//				view.findViewById(R.id.contentBtn).setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//		
//						ParseObject contentToAddObject = new ParseObject("ShopList");
//						contentToAddObject.put("user", ParseUser.getCurrentUser());
//						contentToAddObject.put("product", selectedObj);
//						contentToAddObject.saveInBackground();
//						
//						contentToAddObject.saveInBackground();
//						
//						Toast.makeText(view.getContext(), "הוספה בוצעה בהצלחה",
//								Toast.LENGTH_SHORT).show();
//					}
//				});
//				
//			}
//			
//		});
		
//		view.findViewById(R.id.contentBtn).setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//
//				ParseObject selectedObj = (ParseObject)parent.getItemAtPosition(position);
//				
//
//				ParseObject contentToAddObject = new ParseObject("ShopList");
//				contentToAddObject.put("user", ParseUser.getCurrentUser());
//				contentToAddObject.put("product", selectedObj);
//				contentToAddObject.saveInBackground();
//				
//				contentToAddObject.saveInBackground();
//				
//				Toast.makeText(view.getContext(), "הוספה בוצעה בהצלחה",
//						Toast.LENGTH_SHORT).show();
//			}
//		});
		
		
		return mView;
	}
	
	
	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// super.onActivityResult(requestCode, resultCode, intent);
		// retrieve scan result
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);
		if (scanningResult != null) {
			// we have a result
			mScanContent = scanningResult.getContents();
			mScanFormat = scanningResult.getFormatName();

			
			mFormatTxt.setText("FORMAT: " + mScanFormat);
			mContentTxt.setText("CONTENT: " + mScanContent);
			

			ParseQuery<ParseObject> query = ParseQuery.getQuery("Product");
			query.whereContains("barcode_format", mScanFormat);
			query.whereContains("barcode_content", mScanContent);
			query.findInBackground(new FindCallback<ParseObject>() {
				public void done(List<ParseObject> productList,
						ParseException e) {
					if (e == null) {
						
						ListView listView = (ListView) mView.findViewById(R.id.AddToList_contents_list);
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
