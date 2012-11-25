package com.zerobyte.lifesync;

import com.turbomanage.httpclient.android.AndroidHttpClient;

public class LifeSyncHttpClient extends AndroidHttpClient  {
	private final static String SERVER_URL = "http://54.245.83.84:8080/FBWebServer/android";
	private final int MAX_TIMEOUT = 5000;
	private final int MAX_RETRIES = 1;
	public final int HTTP_OK = 200;
	public final int HTTP_CREATED = 201;
	public final int HTTP_CONFLICT = 409;
	
	LifeSyncHttpClient()
	{
		super( SERVER_URL );
		
		setConnectionTimeout( MAX_TIMEOUT );
    	setReadTimeout( MAX_TIMEOUT );
    	setMaxRetries( MAX_RETRIES );
	}
}
