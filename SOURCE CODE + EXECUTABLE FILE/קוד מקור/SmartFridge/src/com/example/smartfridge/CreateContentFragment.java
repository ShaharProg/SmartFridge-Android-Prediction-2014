package com.example.smartfridge;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import android.util.Log;

import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CreateContentFragment extends Fragment implements OnClickListener {

	protected static final int RESULT_TYPE_PHOTO_PICKER = 1;
	MainActivity mMainActivity;
	private String mScanFormat, mScanContent;
	private Button mScanBtn;
	private TextView mFormatTxt, mContentTxt;
	private EditText mNameETxt, mCompanyETxt, mVolumeETxt;
	private Spinner mUnitSpn, mCategorySpn;
	List<ParseObject> mCategoryList;
	View mView;
	
	ImageView mProductImg;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mView = inflater.inflate(R.layout.fragment_create_content,
				container, false);

		mMainActivity = (MainActivity) getActivity();
		mProductImg = (ImageView)mView.findViewById(R.id.productImg_CreateContent);
		mScanBtn = (Button) mView.findViewById(R.id.scan_buttonCreateContent);
		mFormatTxt = (TextView) mView.findViewById(R.id.scan_format);
		mContentTxt = (TextView) mView.findViewById(R.id.scan_content);
		mNameETxt = (EditText) mView.findViewById(R.id.textBoxCreateContent_name);
		mCompanyETxt = (EditText) mView
				.findViewById(R.id.textBoxCreateContent_company);
		mVolumeETxt = (EditText) mView
				.findViewById(R.id.textBoxCreateContent_volume);
		mUnitSpn = (Spinner) mView.findViewById(R.id.spinnerCreateContent_unit);
		mCategorySpn = (Spinner) mView
				.findViewById(R.id.spinnerCreateContent_category);

		ParseQuery<ParseObject> query = ParseQuery.getQuery("Category");
		query.whereExists("category_name");
		query.findInBackground(new FindCallback<ParseObject>() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void done(List<ParseObject> categoryList, ParseException e) {
				if (e == null) {

					CreateContentFragment.this.mCategoryList = categoryList;
					List list = new ArrayList();
					for (int i = 0; i < categoryList.size(); i++) {
						list.add(categoryList.get(i).get("category_name").toString());
					}

    ArrayAdapter dataAdapter = new ArrayAdapter(mView.getContext() ,android.R.layout.simple_spinner_item, list);
    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    mCategorySpn.setAdapter(dataAdapter);


				} else {
					// Log.d("score", "Error: " +
					// e.getMessage());
				}
			}
		});

		mScanBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.scan_buttonCreateContent) {
					// scan

					Activity activity = (Activity) (v.getContext());
					FragmentManager fm = activity.getFragmentManager();
					Fragment fragment = fm.findFragmentById(R.id.InnerFragment);
					IntentIntegrator scanIntegrator = new IntentIntegrator(
							activity, fragment);
					scanIntegrator.initiateScan();
				}

			}
		});

		Button submitBtn = (Button) mView
				.findViewById(R.id.submit_buttonCreateContent);
		submitBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ParseObject testObject = new ParseObject("Product");
				testObject.put("name", mNameETxt.getText().toString());
				testObject.put("company", mCompanyETxt.getText().toString());
				testObject.put("volume", mVolumeETxt.getText().toString());
				testObject.put("unit", mUnitSpn.getSelectedItem().toString());
				testObject.put("barcode_format", mScanFormat);
				testObject.put("barcode_content", mScanContent);
				
				ParseObject category_parent = mCategoryList.get( mCategorySpn.getSelectedItemPosition() );
				testObject.put("category", category_parent);
				
				//testObject.saveInBackground();
				
				//-------------------
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				mMainActivity.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

				byte[] fileDataResized = bos.toByteArray();
				final String fileName = "product_" + mScanFormat + "_"+ mScanContent + ".jpg";
				final ParseFile parsePhoto = new ParseFile(fileName,
						fileDataResized);

//				parsePhoto.saveInBackground();
				testObject.put("image", parsePhoto);

				testObject.saveInBackground(new SaveCallback() {

					@Override
					public void done(ParseException e) {
						if (e != null) {
							e.printStackTrace();
							Log.d("productPicture upload",
									"failed to upload profilePicture. e.getMessage()=="
											+ e.getMessage());
						}

						Toast.makeText(mView.getContext(), "הוספה בוצעה בהצלחה",
								Toast.LENGTH_SHORT).show();
					}
				});
				//-------------------

				mNameETxt.setText("");
				mCompanyETxt.setText("");
				mVolumeETxt.setText("");
				mFormatTxt.setText("FORMAT:");
				mContentTxt.setText("CONTENT:");
			}
		});

		mProductImg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
					// Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
					Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
					photoPickerIntent.setType("image/*");
					// photoPickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
					startActivityForResult(photoPickerIntent, RESULT_TYPE_PHOTO_PICKER);
			}
		});
		
		return mView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// super.onActivityResult(requestCode, resultCode, intent);
		
		if (requestCode == RESULT_TYPE_PHOTO_PICKER)
		{
			if (resultCode == Activity.RESULT_OK) {
				byte[] fileData = mMainActivity.getBytesFromUri(intent.getData());

				mMainActivity.bitmap = mMainActivity.bytesIntoImageView(fileData,
						mProductImg);

			}
			return;
		}
		else{
			// retrieve scan result
			IntentResult scanningResult = IntentIntegrator.parseActivityResult(
					requestCode, resultCode, intent);
			if (scanningResult != null) {
				// we have a result
				mScanContent = scanningResult.getContents();
				mScanFormat = scanningResult.getFormatName();
	
				mFormatTxt.setText("FORMAT: " + mScanFormat);
				mContentTxt.setText("CONTENT: " + mScanContent);
			} else {
				Toast toast = Toast.makeText(getActivity().getApplicationContext(),
						"No scan data received!", Toast.LENGTH_SHORT);
				toast.show();
			}
		}
	}

}
