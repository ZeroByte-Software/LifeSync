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
	final int HTTP_OK = 200;
	int STATUS;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        final EditText editTxtEmail = (EditText)findViewById(R.id.loginEmail);
        final EditText editTxtPassword = (EditText)findViewById(R.id.loginPassword);
	  
        Button loginButton = (Button) findViewById(R.id.btnLogin);
		loginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String email = editTxtEmail.getText().toString();
				String password = editTxtPassword.getText().toString();
				
				Intent loginIntent = new Intent(LoginActivity.this,
						AndroidTabLayoutActivity.class);
				
				if( isAuthenticated(email, password) )
					startActivity(loginIntent);
				else
				{
					Context context = getApplicationContext();
					CharSequence text = "Incorrect email or password. Please try again.";
					Toast loginFailedToast = Toast.makeText( context, text, Toast.LENGTH_SHORT );
					loginFailedToast.show();
				}
			}
		});
		
		Button registerButton = (Button) findViewById(R.id.btnRegister);
		registerButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent registerIntent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivity(registerIntent);
			}
		});
    }
    
    boolean isAuthenticated( String email, String password )
    {
    	AndroidHttpClient httpClient = new AndroidHttpClient( "http://54.245.83.84:8080/FBWebServer/android" );
    	ParameterMap params = httpClient.newParams();
    	
    	params.add( "email", email );
    	params.add( "password", password );
    	
    	httpClient.post( "/login", params, new AsyncCallback()
    	{

			@Override
			public void onComplete( HttpResponse httpResponse ) {
				STATUS = httpResponse.getStatus();
			}
    		
			@Override
			public void onError( Exception e )
			{
				e.printStackTrace();
			}
    	});
    	
    	if( STATUS == HTTP_OK )
    		return true;
    	else
    		return false;
    }
}
