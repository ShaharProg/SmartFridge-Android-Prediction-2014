package com.example.smartfridge;

import java.util.Locale;

import com.parse.ParseUser;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class SettingsFragment extends Fragment {

	View mView;
	
	ParseUser mUser;
	
	EditText mUsername;
	EditText mEmail;
	EditText mPass;
	Spinner mDay;
	Button mSaveBtn;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_settings, container,
				false);
		mView = view;

		mUser = ParseUser.getCurrentUser();
		
		mUsername = (EditText)view.findViewById(R.id.settings_usernameValue);
		mEmail = (EditText)view.findViewById(R.id.settings_emailValue);
		mPass = (EditText)view.findViewById(R.id.settings_passValue);
		mDay = (Spinner)view.findViewById(R.id.settings_shoppingDayValue);
		mSaveBtn = (Button)view.findViewById(R.id.settings_saveBtn);
		
		mUsername.setText(mUser.getString("nickname"));
		mEmail.setText(mUser.getEmail());
		mDay.setSelection(mUser.getInt("shoppingDay")-1);
		
		mSaveBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String nickname = mUsername.getText().toString();
				String email = mEmail.getText().toString();
				String pass = mPass.getText().toString();
				
				if (nickname != "")
					mUser.put("nickname", mUsername.getText().toString());
				if (email != ""){
					mUser.setEmail(mEmail.getText().toString());
					mUser.setUsername(mEmail.getText().toString());
				}
				if(pass != "")
					mUser.setPassword(mPass.getText().toString());
				
				mUser.put("shoppingDay", mDay.getSelectedItemPosition()+1);
				mUser.saveInBackground();

				Toast.makeText(mView.getContext(), "עדכון הפרטים בוצע בהצלחה",
						Toast.LENGTH_SHORT).show();
			}
		});
		
//		TextView tv = (TextView) view.findViewById(R.id.setting_try);
//		tv.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				setDefaultLocale(getActivity().getBaseContext(), new Locale("he"));
//			}
//		});
		
		return view;
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
