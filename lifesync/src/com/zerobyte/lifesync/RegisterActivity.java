package com.zerobyte.lifesync;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegisterActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		Button registerButton = (Button) findViewById(R.id.btnRegister);

		registerButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent registeredIntent = new Intent(RegisterActivity.this,
						AndroidTabLayoutActivity.class);
				startActivity(registeredIntent);
			}
		});

	}

}
