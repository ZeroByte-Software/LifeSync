/* LoginActivity.java
 * 
 * Activity to handle user logins.
 * 
 */

package com.zerobyte.lifesync;

import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.json.*;

import com.zerobyte.lifesync.model.User;


public class LoginActivity extends LifeSyncActivityBase {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        // Allow properties of textbox to be used
        final EditText editTxtEmail = (EditText)findViewById(R.id.loginEmail);
        final EditText editTxtPassword = (EditText)findViewById(R.id.loginPassword);
	  
        Button loginButton = (Button) findViewById(R.id.btnLogin);
		loginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String email = editTxtEmail.getText().toString();
				String password = editTxtPassword.getText().toString();
				
				if( email.equals("") || password.equals("") )
					showToast( "Please enter your email and password." );
				else
					login(email, password);
			}
		});
		
		Button registerButton = (Button) findViewById(R.id.btnRegister);
		registerButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent registerIntent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivity(registerIntent);
			}
		});
		
    }
    
    @Override
    public void onRestart()
    {
    	super.onRestart();
    	
    	final EditText editTxtEmail = (EditText)findViewById(R.id.loginEmail);
        final EditText editTxtPassword = (EditText)findViewById(R.id.loginPassword);
        
        // Clear text boxes
        editTxtEmail.setText( "" );
        editTxtPassword .setText( "" );
    }
    
    /* 
     * Creates HTTP client and connects to web server to authenticate user
     */
    private void login( String email, String password )
    {
    	final LifeSyncHttpClient httpClient = new LifeSyncHttpClient();
    	ParameterMap params = httpClient.newParams();
 	
    	params.add( "email", email );
    	params.add( "password", password );
    	
    	// Contact server using GET via separate thread
    	httpClient.get( "/login", params, new AsyncCallback()
    	{
			@Override
			public void onComplete( HttpResponse httpResponse ) {
				int status = httpResponse.getStatus();
				
				if( status == httpClient.HTTP_OK )
				{
					loggedInUser = new User();
					JSONObject userJSON = null;
					try {
						userJSON = new JSONObject(httpResponse.getBodyAsString());

						loggedInUser.setEmail(userJSON.getString("email"));
						loggedInUser.setFirst_name(userJSON.getString("first_name"));
						loggedInUser.setLast_name(userJSON.getString("last_name"));
						loggedInUser.setUserid(userJSON.getInt("user_id"));
						
						Intent loginIntent = new Intent(LoginActivity.this, AndroidTabLayoutActivity.class);
						startActivity(loginIntent);
						finish();
					} catch (JSONException e) {
						showToast( "JSON exception occured. Canceling log in." );
						showToast( e.getMessage() );
						e.printStackTrace();
					}
				}
				else
					showToast( "Incorrect email or password. Please try again." );
			}
    		
			@Override
			public void onError( Exception e )
			{
				showToast( "Server error. Please try again." );
				showToast( e.getMessage() );
				e.printStackTrace();
			}
    	});
    }
}
