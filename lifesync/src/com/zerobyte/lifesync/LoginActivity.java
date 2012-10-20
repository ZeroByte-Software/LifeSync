package com.zerobyte.lifesync;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
	  
        Button loginButton = (Button) findViewById(R.id.btnLogin);
		loginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent loginIntent = new Intent(LoginActivity.this,
						AndroidTabLayoutActivity.class);
				startActivity(loginIntent);
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
}
