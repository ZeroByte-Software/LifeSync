/* RegisterActivity.java
 * 
 * Activity to handle registration of new users.
 * 
 */

package com.zerobyte.lifesync;

import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class RegisterActivity extends LifeSyncActivityBase {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		// Allow properties of textbox to be used
		final EditText editTxtEmail = (EditText)findViewById(R.id.registerEmail);
        final EditText editTxtPassword = (EditText)findViewById(R.id.registerPassword);
        final EditText editTxtConfirmPassword = (EditText)findViewById(R.id.registerConfirmPassword);
        final EditText editTxtFirstName = (EditText)findViewById(R.id.registerFirstName);
        final EditText editTxtLastName = (EditText)findViewById(R.id.registerLastName);
		
		Button registerButton = (Button) findViewById(R.id.btnRegister);
		
		registerButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String email = editTxtEmail.getText().toString();
				String password = editTxtPassword.getText().toString();
				String passwordConfirm = editTxtConfirmPassword.getText().toString();
				String fName = editTxtFirstName.getText().toString();
				String lName = editTxtLastName.getText().toString();
				
				// If any text boxes are empty...
				if( email.equals("") || password.equals("") || fName.equals("") || lName.equals("") )
					showToast( "Please enter your registration info." );
				// If not valid email address...
				else if( !email.contains("@") || !email.contains(".") )
					showToast( "Please enter a valid email address.");
				// If passwords do not match...
				else if( !password.equals(passwordConfirm) )
					showToast( "Passwords do not match. Please re-enter your password." );
				else
					register( email, password, fName, lName );
			}
		});
	}
	
    /* 
     * Creates HTTP client and connects to web server to register user
     */
	private void register( String email, String password, String fName, String lName )
	{
		final LifeSyncHttpClient httpClient = new LifeSyncHttpClient();
    	ParameterMap params = httpClient.newParams();
    	
    	params.add( "email", email );
    	params.add( "password", password );
    	params.add( "fName", fName );
    	params.add( "lName", lName );
    	
    	// Contact server using POST via separate thread
    	httpClient.post( "/register", params, new AsyncCallback()
    	{
			@Override
			public void onComplete( HttpResponse httpResponse ) {
				int status = httpResponse.getStatus();
				
				if( status == httpClient.HTTP_CREATED )
				{
					AccountCreatedDialogFragment dialog = new AccountCreatedDialogFragment();
					dialog.show( getFragmentManager(), "accountCreated" );
				}
				else if( status == httpClient.HTTP_CONFLICT )
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
     * Creates dialog that displays when account is successfully created
     */
    public class AccountCreatedDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.dialogAccountCreated)
                   .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                    	   finish();	// Close RegisterActivity and return to LoginActivity
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
                           // Do nothing but close dialog
                       }
                   });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}
