/* LifeSyncHttpClient.java
 * 
 * TurboManage HTTP client class customized for LifeSync
 * 
 * Source of TurboManage HTTP Client: https://turbomanage.wordpress.com/2012/06/12/a-basic-http-client-for-android-and-more/
 * Google Code: https://code.google.com/p/basic-http-client/
 */

package com.zerobyte.lifesync;

import com.turbomanage.httpclient.android.AndroidHttpClient;

public class LifeSyncHttpClient extends AndroidHttpClient  {
	// HTTP Status Codes
	public final int HTTP_OK = 200;
	public final int HTTP_CREATED = 201;
	public final int HTTP_BAD_REQUEST = 400;
	public final int HTTP_CONFLICT = 409;
	
	LifeSyncHttpClient()
	{
		super( "http://54.245.83.84:8080/FBWebServer/android" );
		
		setConnectionTimeout( 5000 );
    	setReadTimeout( 5000 );
    	setMaxRetries( 1 );
	}
}
