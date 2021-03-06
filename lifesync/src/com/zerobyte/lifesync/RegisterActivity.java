/* RegisterActivity.java
 * 
 * Activity to handle registration of new users.
 * 
 */

package com.zerobyte.lifesync;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;


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
    	ParameterMap params = httpClient.newParams();	// Parameters to send to server
    	
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
					dialog.setCancelable(false);
					dialog.show( getFragmentManager(), "accountCreated" );
					
				}
				else if( status == httpClient.HTTP_CONFLICT )
				{
					DuplicateEmailDialogFragment dialog = new DuplicateEmailDialogFragment();
					dialog.show( getFragmentManager(), "duplicateEmail" );
				}
				else
					showToast( "Sorry, an unknown error occured" );
			}
    		
			@Override
			public void onError( Exception e )
			{
				showToast( "Sorry, a server error. Please try again." );
				showToast( e.getMessage() );
				e.printStackTrace();
			}
    	});
	}

    
    /*
     * Creates a dialog that displays when account is successfully created
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
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
        		Bundle savedInstanceState) {
        	getDialog().setCanceledOnTouchOutside(false);
        	return super.onCreateView(inflater, container, savedInstanceState);
        }
    }
        
    /*
     * Creates a dialog that displays if email already exists in database
     */
    public class DuplicateEmailDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.dialogDuplicateEmail)
                   .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           dialog.dismiss();
                       }
                   });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}
