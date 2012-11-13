/* RegisterActivity.java
 * 
 * Activity to handle registration of new users.
 * 
 */

package com.zerobyte.lifesync;

import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import com.turbomanage.httpclient.android.AndroidHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class RegisterActivity extends Activity {
	final private String SERVER_URL = "http://54.245.83.84:8080/FBWebServer/android";
	final private int HTTP_CREATED = 201;
	final private int HTTP_CONFLICT = 409;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		// Allow properties of textbox to be used
		final EditText editTxtEmail = (EditText)findViewById(R.id.registerEmail);
        final EditText editTxtPassword = (EditText)findViewById(R.id.registerPassword);
        final EditText editTxtFirstName = (EditText)findViewById(R.id.registerFirstName);
        final EditText editTxtLastName = (EditText)findViewById(R.id.registerLastName);
		
		Button registerButton = (Button) findViewById(R.id.btnRegister);
		
		registerButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String email = editTxtEmail.getText().toString();
				String password = editTxtPassword.getText().toString();
				String fName = editTxtFirstName.getText().toString();
				String lName = editTxtLastName.getText().toString();
				
				if( email.equals("") || password.equals("") || fName.equals("") || lName.equals("") )
					showToast( "Please enter your registration info." );
				else
					register( email, password, fName, lName );
			}
		});
	}
	
	private void register( String email, String password, String fName, String lName )
	{
		AndroidHttpClient httpClient = new AndroidHttpClient( SERVER_URL );
    	ParameterMap params = httpClient.newParams();
    	
    	httpClient.setReadTimeout( 5000 );
    	httpClient.setMaxRetries( 1 );
    	
    	params.add( "email", email );
    	params.add( "password", password );
    	params.add( "fName", fName );
    	params.add( "lName", lName );
    	
    	// Contact server using POST via seperate thread
    	httpClient.post( "/register", params, new AsyncCallback()
    	{
			@Override
			public void onComplete( HttpResponse httpResponse ) {
				int status = httpResponse.getStatus();
				
				if( status == HTTP_CREATED )
				{
					AccountCreatedDialogFragment dialog = new AccountCreatedDialogFragment();
					dialog.show( getFragmentManager(), "accountCreated" );
				}
				else if( status == HTTP_CONFLICT )
				{
					DuplicateEmailDialogFragment dialog = new DuplicateEmailDialogFragment();
					dialog.show( getFragmentManager(), "duplicateEmail" );
				}
				else
					showToast( "Unknown error occured" );
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
     * Displays a toast with a specified string
     */
    public void showToast( String text )
    {
    	Context context = getApplicationContext();
    	Toast toast = Toast.makeText( context, text, Toast.LENGTH_SHORT);
    	toast.show();
    }
    
    /*
     * Creates dialog that displays if email already exists in database
     */
    public class AccountCreatedDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.dialogAccountCreated)
                   .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                    	   Intent registeredIntent = new Intent(RegisterActivity.this, AndroidTabLayoutActivity.class);
                    	   startActivity(registeredIntent);
                       }
                   });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
        
    /*
     * Creates dialog that displays if email already exists in database
     */
    public class DuplicateEmailDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.dialogDuplicateEmail)
                   .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           // Do nothing...
                       }
                   });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}
