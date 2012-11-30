/* LoginActivity.java
 * 
 * Activity to handle user logins.
 * 
 */

package com.zerobyte.lifesync;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;

public class LoginActivity extends LifeSyncActivityBase {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// Allow properties of textbox to be used
		final EditText editTxtEmail = (EditText) findViewById(R.id.loginEmail);
		final EditText editTxtPassword = (EditText) findViewById(R.id.loginPassword);

		Button loginButton = (Button) findViewById(R.id.btnLogin);
		loginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String email = editTxtEmail.getText().toString();
				String password = editTxtPassword.getText().toString();

				if (email.equals("") || password.equals(""))
					showToast("Please enter your email and password.");
				else
					login(email, password);
				
				//login("a@a.a", "a");
			}
		});

		Button registerButton = (Button) findViewById(R.id.btnRegister);
		registerButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//login("b@b.b", "b");
				
				Intent registerIntent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivity(registerIntent);
			}
		});
	}

	@Override
	public void onRestart() {
		super.onRestart();
		
		// Allow properties of textbox to be used
		final EditText editTxtEmail = (EditText) findViewById(R.id.loginEmail);
		final EditText editTxtPassword = (EditText) findViewById(R.id.loginPassword);

		// Clear text boxes
		editTxtEmail.setText("");
		editTxtPassword.setText("");
	}

	/*
	 * Creates HTTP client and connects to web server to authenticate user
	 */
	private void login(String email, String password) {
		final LifeSyncHttpClient httpClient = new LifeSyncHttpClient();
		ParameterMap params = httpClient.newParams();	// Parameters to send to server

		params.add("email", email);
		params.add("password", password);

		// Contact server using GET via separate thread
		httpClient.get("/login", params, new AsyncCallback() {
			@Override
			public void onComplete(HttpResponse httpResponse) {
				int status = httpResponse.getStatus();

				if (status == httpClient.HTTP_OK) {
					// Retrieve user information, and store for further use
					JSONObject userJSON = null;
					try {
						userJSON = new JSONObject(httpResponse
								.getBodyAsString());

						user.setEmail(userJSON.getString("email"));
						user.setFirst_name(userJSON.getString("first_name"));
						user.setLast_name(userJSON.getString("last_name"));
						user.setUserid(userJSON.getInt("user_id"));
						lfapp.saveUser(user);

						Intent loginIntent = new Intent(LoginActivity.this,
								AndroidTabLayoutActivity.class);
						startActivity(loginIntent);
						finish();
					} catch (JSONException e) {
						showToast( "JSON exception occured. Login cancelled." );
						showToast( e.getMessage() );
						e.printStackTrace();
					}
				} else
					showToast("Incorrect email or password. Please try again.");
			}

			@Override
			public void onError(Exception e) {
				showToast( "Sorry, a server error occured. Please try again." );
				showToast( e.getMessage() );
				e.printStackTrace();
			}
		});
	}
}
