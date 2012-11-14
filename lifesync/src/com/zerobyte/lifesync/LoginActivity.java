/* LoginActivity.java
 * 
 * Activity to handle user logins.
 * 
 */

package com.zerobyte.lifesync;

import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import com.turbomanage.httpclient.android.AndroidHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends Activity {
	final private String SERVER_URL = "http://54.245.83.84:8080/FBWebServer/android";
	final private int MAX_TIMEOUT = 5000;
	final private int MAX_RETRIES = 1;
	final private int HTTP_OK = 200;
	
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
    
    /* 
     * Creates HTTP client and connects to web server to authenticate user
     */
    private void login( String email, String password )
    {
    	AndroidHttpClient httpClient = new AndroidHttpClient( SERVER_URL );
    	ParameterMap params = httpClient.newParams();
    	
    	httpClient.setConnectionTimeout( MAX_TIMEOUT );
    	httpClient.setReadTimeout( MAX_TIMEOUT );
    	httpClient.setMaxRetries( MAX_RETRIES );
    	
    	params.add( "email", email );
    	params.add( "password", password );
    	
    	// Contact server using POST via separate thread
    	httpClient.post( "/login", params, new AsyncCallback()
    	{
			@Override
			public void onComplete( HttpResponse httpResponse ) {
				int status = httpResponse.getStatus();
				
				if( status == HTTP_OK )
				{
					Intent loginIntent = new Intent(LoginActivity.this, AndroidTabLayoutActivity.class);
					startActivity(loginIntent);
				}
				else
					showToast( "Incorrect email or password. Please try again." );
			}
    		
			@Override
			public void onError( Exception e )
			{
				showToast( "Server error. Please try again." );
				e.printStackTrace();
			}
    	});
    }
    
    /*
     * Displays toast with a specified string
     */
    public void showToast( String text )
    {
    	Context context = getApplicationContext();
    	Toast toast = Toast.makeText( context, text, Toast.LENGTH_SHORT);
    	toast.show();
    }
}
