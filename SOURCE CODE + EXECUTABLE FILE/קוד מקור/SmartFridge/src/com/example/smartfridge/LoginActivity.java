package com.example.smartfridge;

import com.parse.Parse;
import com.parse.ParseUser;
import com.parse.ParseException;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;
	private CheckBox mRemember;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	

	private SharedPreferences mPref;
	private Editor mEditor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);


		Parse.initialize(this, "kquXnokAChuwQaYndtnqtv2nL8qTraEIsfnlQhuy",
				"LN74BlS6O4N6k7sfo1exBiBzy5klf5tpcAWAVvTD");


        mPref = getApplicationContext().getSharedPreferences("SmartFridgePref", MODE_PRIVATE); 
        mEditor = mPref.edit();
        mEmail = mPref.getString("email", getIntent().getStringExtra(EXTRA_EMAIL));
        mPassword = mPref.getString("pass", "");
		
		// Set up the login form.

    	mRemember = (CheckBox) findViewById(R.id.login_remember_me);
     
		mEmailView = (EditText) findViewById(R.id.login_email);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.login_password);
		mPasswordView.setText(mPassword);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});

		findViewById(R.id.link_to_register).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
//TODO: kjfkd
						Intent k = new Intent(LoginActivity.this,
								RegisterActivity.class);
						startActivity(k);
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mPasswordView.getWindowToken(), 0);
		
		
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginFormView.requestFocus();
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			ParseUser user;
			//EditText pass = (EditText) findViewById(R.id.login_password);
			//EditText email = (EditText) findViewById(R.id.login_email);
			
			//CheckBox remember = (CheckBox) findViewById(R.id.login_remember_me);

			
			try {
				user = ParseUser.logIn(mEmailView.getText().toString(), mPasswordView
						.getText().toString());

				if (user.isDataAvailable()) {
					// Hooray! Let them use the app now.
					if(mRemember.isChecked())
					{
				        mEditor.putString("email", mEmailView.getText().toString());
				        mEditor.putString("pass", mPasswordView.getText().toString());
				        mEditor.commit();
					}
					
					Intent k = new Intent(LoginActivity.this,
							MainActivity.class);
					startActivity(k);
				} else {
					// Signup failed. Look at the ParseException to see what
					// happened.
					return false;
				}

			} catch (ParseException e) {
				// Signup failed. Look at the ParseException to see what
				// happened.
				return false;
			}


			// ParseUser.logInInBackground(email.getText().toString(),
			// pass.getText().toString(), new LogInCallback() {
			// public void done(ParseUser user, ParseException e) {
			// if (user != null) {
			// Intent k = new Intent(LoginActivity.this,
			// MainActivity.class);
			// startActivity(k);
			// } else {
			// // Signup failed. Look at the ParseException to see what
			// // happened.
			// findViewById(R.id.login_try_again_label).setVisibility(View.VISIBLE);
			// return;
			// }
			// }
			// });
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				// finish();
			} else {

				TextView tryAgain = (TextView)findViewById(R.id.login_try_again_label);
				tryAgain.setVisibility(View.VISIBLE);
				
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
