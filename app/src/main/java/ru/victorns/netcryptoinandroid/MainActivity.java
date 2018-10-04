package ru.victorns.netcryptoinandroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ru.victorns.netcryptoinandroid.Utils.Crypto;

public class MainActivity extends AppCompatActivity {

	// UI references.
	private EditText mHashView;
	private EditText mPinView;
	private TextView mMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mHashView = findViewById(R.id.email);
		mHashView.setText("ACLHH6V6V2O/fkh9uhZNW+ybY7GEr17Uhhqqc/OIE7S/2yqBZQ+VeMI7u84jvZ8CXA==");
		mPinView = findViewById(R.id.password);
		mPinView.setText("6791");
		mMessage = findViewById(R.id.message);

		Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
		mEmailSignInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	private void attemptLogin() {
		// Reset errors.
		mHashView.setError(null);
		mPinView.setError(null);

		// Store values at the time of the login attempt.
		String hash = mHashView.getText().toString();
		String pwd = mPinView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid pwd, if the user entered one.
		if (TextUtils.isEmpty(pwd)) {
			mHashView.setError(getString(R.string.error_field_required));
			focusView = mPinView;
			cancel = true;
		}

		// Check for a valid hash address.
		if (TextUtils.isEmpty(hash)) {
			mHashView.setError(getString(R.string.error_field_required));
			focusView = mHashView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
			mMessage.setText(getString(R.string.message_initial));
		} else {
			boolean res = Crypto.VerifyHashedPassword(hash, pwd);
			if (res) {
				mMessage.setText(getString(R.string.message_OK));
			} else {
				mMessage.setText(getString(R.string.error_incorrect_pin));
			}
		}
	}
}
