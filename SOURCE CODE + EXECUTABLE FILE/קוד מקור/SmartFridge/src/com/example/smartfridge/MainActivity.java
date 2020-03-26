package com.example.smartfridge;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import com.example.smartfridge.MyHorizontalScrollView.SizeCallback;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.support.v4.app.FragmentActivity;
import android.util.Pair;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;

public class MainActivity extends FragmentActivity {

	MyHorizontalScrollView mScrollView;
	View mMenu;
	View mApp;
	ImageView mBtnSlide;
	boolean mMenuOut = false;
	Handler mHandler = new Handler();
	int mBtnWidth;
	int mCurrentInnerFragmentPos = 0;

	ParseUser mCurrentUser;

	private int screenW;
	private int screenH;
	public Bitmap bitmap;
	private ParseInstallation mInstallation; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Parse.initialize(this, "kquXnokAChuwQaYndtnqtv2nL8qTraEIsfnlQhuy",
				"LN74BlS6O4N6k7sfo1exBiBzy5klf5tpcAWAVvTD");


		
		mCurrentUser = ParseUser.getCurrentUser();
		if(mCurrentUser == null)
		{
			Intent k = new Intent(MainActivity.this,
					LoginActivity.class);
			startActivity(k);
			finish();
		}
		PushService.setDefaultPushCallback(this, MainActivity.class);

		ParseAnalytics.trackAppOpened(getIntent());
		// Save the current Installation to Parse.
		mInstallation = ParseInstallation.getCurrentInstallation();
		mInstallation.put("user", mCurrentUser);
		mInstallation.saveInBackground();


		// read screen dimensions:
		Display display = getWindowManager().getDefaultDisplay();
		screenW = display.getWidth(); // deprecated
		screenH = display.getHeight(); // deprecated
		
		// MySQLiteHelper myDbHelper = new MySQLiteHelper(this);
		// myDbHelper = new MySQLiteHelper(this);
		// try {
		//
		// myDbHelper.createDataBase();
		//
		// } catch (IOException ioe) {
		// throw new Error("Unable to create database");
		// }
		// try {
		//
		// myDbHelper.openDataBase();
		//
		// }catch(SQLException sqle){
		// throw sqle;
		// }

		setUpScrollView();

		setUpMenu();

		String temp = mCurrentUser.getString("nickname").toString();

		TextView nicknameBar = (TextView) findViewById(R.id.nicknameBar)
				.findViewById(R.id.nicknameTitle);
		nicknameBar.setText("שלום " + temp + ",");
	}

	public int getCurrentInnerFragmentPos()
	{
		return mCurrentInnerFragmentPos;
	}
	
	private void setUpScrollView() {
		LayoutInflater inflater = LayoutInflater.from(this);
		mScrollView = (MyHorizontalScrollView) inflater.inflate(
				R.layout.horz_scroll_with_list_menu, null);
		setContentView(mScrollView);

		mMenu = inflater.inflate(R.layout.horz_scroll_menu, null);
		mApp = inflater.inflate(R.layout.horz_scroll_app, null);
		ViewGroup tabBar = (ViewGroup) mApp.findViewById(R.id.tabBar);

		mBtnSlide = (ImageView) tabBar.findViewById(R.id.BtnSlide);
		mBtnSlide.setOnClickListener(new ClickListenerForScrolling(mScrollView,
				mMenu));

		final View[] children = new View[] { mMenu, mApp };

		// Scroll to app (view[1]) when layout finished.
		int scrollToViewIdx = 1;
		mScrollView.initViews(children, scrollToViewIdx,
				new SizeCallbackForMenu(mBtnSlide));
	}

	private void setUpMenu() {

		ListView listView = (ListView) mMenu.findViewById(R.id.menu_list);
		final String[] values = new String[] {
				"-ניהול רשימות-",
				"כרגע במקרר", "רשימת קניות", 
				"הוסף מוצר לרשימה", 
				"-מתכונים-",
				"חפש מתכון", "חפש מתכון לפי מוצרים", 
				"-יצירת אובייקטים-",
				"צור מוצר חדש","צור מתכון חדש",
				"-הגדרות-",
				"סטטיסטיקה", "הגדרות",
				"אודות", "שלח משוב",
				"התנתק" };
		final Integer[] icones = new Integer[] {
				-1,
				R.drawable.ic_launcher, R.drawable.shoppingcart_icon, 
				R.drawable.shoppingcart_pluse_icon,
				-1, 
				R.drawable.search_icon, R.drawable.search_icon,
				-1, 
				R.drawable.product_icon, R.drawable.note_icon,
				-1, 
				R.drawable.statistics_icon, R.drawable.settings_icon,
				R.drawable.about_icon, R.drawable.smile_icon,
				R.drawable.logout_icon };
		final Fragment[] fragments = new Fragment[] {
				null,//0
				new InFridgeFragment(), new ShopListFragment(),
				new AddToListFragment(),
				null, //4
				new RecipeMainFragment(), new FindRecipeByProductFragment(),
				null,//7
				new CreateContentFragment(), new CreateRecipeFragment(),
				null,//10
				new StatisticsFragment(), new SettingsFragment(),
				new AboutFragment(), new FeedbackFragment(),
				null };


		final ImageView tabFridge = (ImageView) mApp.findViewById(R.id.TabFridge);
		final ImageView tabShop = (ImageView) mApp.findViewById(R.id.TabShop);
		final ImageView tabBg_Fridge = (ImageView) mApp.findViewById(R.id.TabBg_Fridge);
		final ImageView tabBg_Shop = (ImageView) mApp.findViewById(R.id.TabBg_Shop);
		
		TextView title = (TextView) mApp.findViewById(R.id.appTitle);
		title.setText(values[0]);

		ArrayList<Pair<String,Integer>> list = new ArrayList<Pair<String,Integer>>();
		for (int i = 0; i < values.length; ++i) {
			list.add(Pair.create(values[i],icones[i]));
		}

		MenuArrayAdapter adapter = new MenuArrayAdapter(this,
				android.R.layout.simple_list_item_1, list);
		listView.setAdapter(adapter);

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.replace(R.id.InnerFragment, fragments[1], fragments[1].getClass().getSimpleName());
		fragmentTransaction.addToBackStack( fragments[1].getClass().getSimpleName() );
		fragmentTransaction.commit();
		fragmentManager.executePendingTransactions();

		TextView appTitle = (TextView) mApp.findViewById(R.id.appTitle);
		appTitle.setText(values[1]);
		mCurrentInnerFragmentPos = 1;

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Boolean isClickable = true;
				
				switch(position){
				case 1:
					tabShop.setScaleX((float) 0.8);
					tabShop.setScaleY((float) 0.8);
					tabFridge.setScaleX((float) 1);
					tabFridge.setScaleY((float) 1);

					tabBg_Shop.setScaleX((float) 0.8);
					tabBg_Shop.setScaleY((float) 0.8);
					tabBg_Fridge.setScaleX((float) 1);
					tabBg_Fridge.setScaleY((float) 1);
					break;
				case 2:
					tabFridge.setScaleX((float) 0.8);
					tabFridge.setScaleY((float) 0.8);
					tabShop.setScaleX((float) 1);
					tabShop.setScaleY((float) 1);

					tabBg_Fridge.setScaleX((float) 0.8);
					tabBg_Fridge.setScaleY((float) 0.8);
					tabBg_Shop.setScaleX((float) 1);
					tabBg_Shop.setScaleY((float) 1);
					break;
				case 15:
					//logout
					isClickable = false;
					ParseUser.logOut();
					Intent k = new Intent(MainActivity.this,
							LoginActivity.class);
					startActivity(k);
					finish();
					return;
				case 0:
					isClickable = false;
					break;
				case 4:
					isClickable = false;
					break;
				case 7:
					isClickable = false;
					break;
				case 10:
					isClickable = false;
					break;
				default:
					tabFridge.setScaleX((float) 0.8);
					tabFridge.setScaleY((float) 0.8);
					tabShop.setScaleX((float) 0.8);
					tabShop.setScaleY((float) 0.8);

					tabBg_Fridge.setScaleX((float) 0.8);
					tabBg_Fridge.setScaleY((float) 0.8);
					tabBg_Shop.setScaleX((float) 0.8);
					tabBg_Shop.setScaleY((float) 0.8);
					break;
				}
				
				if (isClickable == true)
				{
					FragmentManager fragmentManager = getFragmentManager();
					FragmentTransaction fragmentTransaction = fragmentManager
							.beginTransaction();
					fragmentTransaction.replace(R.id.InnerFragment,
							fragments[position],fragments[position].getClass().getSimpleName());
					fragmentTransaction.addToBackStack( fragments[position].getClass().getSimpleName() );
					fragmentTransaction.commit();
					fragmentManager.executePendingTransactions();
	
					TextView appTitle = (TextView) mApp.findViewById(R.id.appTitle);
					appTitle.setText(values[position]);
					mCurrentInnerFragmentPos = position;
					mBtnSlide.callOnClick();
				}
			}
		});
		

		tabFridge.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tabShop.setScaleX((float) 0.8);
				tabShop.setScaleY((float) 0.8);
				tabFridge.setScaleX((float) 1);
				tabFridge.setScaleY((float) 1);

				tabBg_Shop.setScaleX((float) 0.8);
				tabBg_Shop.setScaleY((float) 0.8);
				tabBg_Fridge.setScaleX((float) 1);
				tabBg_Fridge.setScaleY((float) 1);
				
				FragmentManager fragmentManager = getFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager
						.beginTransaction();
				fragmentTransaction.replace(R.id.InnerFragment,
						fragments[1],fragments[1].getClass().getSimpleName());
				fragmentTransaction.addToBackStack( fragments[1].getClass().getSimpleName() );
				fragmentTransaction.commit();
				fragmentManager.executePendingTransactions();
				
				TextView appTitle = (TextView) mApp.findViewById(R.id.appTitle);
				appTitle.setText(values[1]);
				mCurrentInnerFragmentPos = 1;
			}
		});
		tabShop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tabFridge.setScaleX((float) 0.8);
				tabFridge.setScaleY((float) 0.8);
				tabShop.setScaleX((float) 1);
				tabShop.setScaleY((float) 1);

				tabBg_Fridge.setScaleX((float) 0.8);
				tabBg_Fridge.setScaleY((float) 0.8);
				tabBg_Shop.setScaleX((float) 1);
				tabBg_Shop.setScaleY((float) 1);
				
				FragmentManager fragmentManager = getFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager
						.beginTransaction();
				fragmentTransaction.replace(R.id.InnerFragment,
						fragments[2],fragments[2].getClass().getSimpleName());
				fragmentTransaction.addToBackStack( fragments[2].getClass().getSimpleName() );
				fragmentTransaction.commit();
				fragmentManager.executePendingTransactions();
				TextView appTitle = (TextView) mApp.findViewById(R.id.appTitle);
				appTitle.setText(values[2]);
				mCurrentInnerFragmentPos = 2;
			}
		});
		
	}

	static class ClickListenerForScrolling implements OnClickListener {
		HorizontalScrollView mScrollView;
		View mMenu;
		/**
		 * Menu must NOT be out/shown to start with.
		 */
		boolean menuOut = false;

		public ClickListenerForScrolling(HorizontalScrollView scrollView,
				View menu) {
			super();
			this.mScrollView = scrollView;
			this.mMenu = menu;
		}

		@Override
		public void onClick(View v) {
//			Context context = menu.getContext();
			String msg = "Slide " + new Date();
			// Toast.makeText(context, msg, 1000).show();
			System.out.println(msg);

			int menuWidth = mMenu.getMeasuredWidth();

			// Ensure menu is visible
			mMenu.setVisibility(View.VISIBLE);

			if (!menuOut) {
				// Scroll to 0 to reveal menu
				int left = 0;
				mScrollView.smoothScrollTo(left, 0);
			} else {
				// Scroll to menuWidth so menu isn't on screen.
				int left = menuWidth;
				mScrollView.smoothScrollTo(left, 0);
			}
			menuOut = !menuOut;
		}
	}

	static class SizeCallbackForMenu implements SizeCallback {
		int mBtnWidth;
		View mBtnSlide;

		public SizeCallbackForMenu(View btnSlide) {
			super();
			this.mBtnSlide = btnSlide;
		}

		@Override
		public void onGlobalLayout() {
			mBtnWidth = mBtnSlide.getMeasuredWidth();
			System.out.println("btnWidth=" + mBtnWidth);
		}

		@Override
		public void getViewSize(int idx, int w, int h, int[] dims) {
			dims[0] = w;
			dims[1] = h;
			final int menuIdx = 0;
			if (idx == menuIdx) {
				dims[0] = w - mBtnWidth;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	 public boolean onKeyDown(int keyCode, KeyEvent event)
	 {
	 if (keyCode == KeyEvent.KEYCODE_BACK)
	 {
		 getFragmentManager().executePendingTransactions();
	    if (getFragmentManager().getBackStackEntryCount() == 1)
	    {
	        this.finish();
	        return false;
	    }
	    else
	    {
	    	getFragmentManager().popBackStack();
	        removeCurrentFragment();

	        return false;
	    }



	 }

	  return super.onKeyDown(keyCode, event);
	 }


	public void removeCurrentFragment()
	{
	//FragmentTransaction transaction = getFragmentManager().beginTransaction();
	
	//Fragment currentFrag =  getFragmentManager().findFragmentById(R.id.f);
	}


	public Bitmap bytesIntoImageView(byte[] fileData, ImageView imageView) {
		int w = imageView.getWidth();
		int h = imageView.getHeight();

		if (w < 30 || h < 30) {
			w = screenW / 4;
			h = screenH / 4;
		}

		Bitmap bitmap = bytesToBitmap(fileData, w, h);
		imageView.setImageBitmap(bitmap);
		return bitmap;
	}
	
	Bitmap bytesToBitmap(byte[] data, int width, int height) {
		Bitmap bitmap = null;
		Options optionsGetDimensions = new Options();
		Options optionsGetData = new Options();
		optionsGetDimensions.inJustDecodeBounds = true;

		BitmapFactory.decodeByteArray(data, 0, data.length,
				optionsGetDimensions);

		optionsGetData.inSampleSize = calculateInSampleSize(
				optionsGetDimensions, width, height);
		bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
				optionsGetData);

		return bitmap;
	}
	
	
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}
	
	

	public byte[] getBytesFromUri(Uri uri) {
		InputStream iStream;
		ByteArrayOutputStream byteBuffer = null;
		try {
			iStream = getContentResolver().openInputStream(uri);
			byteBuffer = new ByteArrayOutputStream();
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];

			int len = 0;
			while ((len = iStream.read(buffer)) != -1) {
				byteBuffer.write(buffer, 0, len);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return byteBuffer.toByteArray();
	}


}
