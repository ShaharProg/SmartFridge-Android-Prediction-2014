package com.example.smartfridge;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.view.KeyEvent;

public class FeedbackFragment extends Fragment {

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_feedback, container,
				false);

		Display display = ((Activity) view.getContext()).getWindowManager()
				.getDefaultDisplay();
		Point outSize = new Point();
		display.getSize(outSize);

		final EditText feedbackField = (EditText) view
				.findViewById(R.id.feedback_field);
		feedbackField.setHeight(outSize.y / 3);

		final Button sendBtn = (Button) view
				.findViewById(R.id.send_feedback_btn);
		sendBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// EditText feedbackField =
				// (EditText)v.findViewById(R.id.feedback_field);

				String to = "shaharz@mail.afeka.ac.il";
				String subject = "SmartFridge Feedback";
				String message = feedbackField.getText().toString();

				Intent email = new Intent(Intent.ACTION_SEND);
				email.putExtra(Intent.EXTRA_EMAIL, new String[] { to });
				// email.putExtra(Intent.EXTRA_CC, new String[]{ to});
				// email.putExtra(Intent.EXTRA_BCC, new String[]{to});
				email.putExtra(Intent.EXTRA_SUBJECT, subject);
				email.putExtra(Intent.EXTRA_TEXT, message);

				// need this to prompts email client only
				email.setType("message/rfc822");

				startActivity(Intent.createChooser(email,
						"Choose an Email client :"));

			}
		});

		feedbackField.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEND) {
					// Button sendBtn =
					// (Button)v.findViewById(R.id.send_feedback_btn);
					sendBtn.callOnClick();
					return true;
				}
				return false;
			}
		});

		return view;
	}

}
