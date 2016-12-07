package com.vmware.photon.controller.api.client;

import org.apache.http.HttpResponse;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;

import com.vmware.photon.controller.api.client.RestClient.Method;

public class RestClient {
	public RestClient(String connectionString, CloseableHttpAsyncClient httpClient) {
	}

	/**
	 * Possible http verbs.
	 */
	public enum Method {
		GET, PUT, POST, DELETE;
	}

	public void performAsync(Method get, String etcdStatusPath, Object object,
			org.apache.http.concurrent.FutureCallback futureCallback) {
		// TODO Auto-generated method stub
		
	}

	public void checkResponse(HttpResponse result, int scOk) {
		// TODO Auto-generated method stub
		
	}

}
