package com.vmware.photon.controller.xenon.client;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.vmware.photon.controller.common.thrift.ServerSet;
import com.vmware.photon.controller.common.thrift.StaticServerSet;
import com.vmware.photon.controller.common.xenon.XenonRestClient;

public class GeneralXenonRestClient extends XenonRestClient {

	private GeneralXenonRestClient(ServerSet serverSet, ExecutorService executor,
			ScheduledExecutorService scheduledExecutorService) {
		super(serverSet, executor, scheduledExecutorService);
	}

	public static synchronized GeneralXenonRestClient getVcsRestClient(
			String url) throws MalformedURLException {
		URL serverUrl = new URL(url);

		StaticServerSet serverSet = new StaticServerSet(new InetSocketAddress(
				serverUrl.getHost(), serverUrl.getPort()));
		ExecutorService executor = Executors.newFixedThreadPool(16);
		ScheduledExecutorService scheduledExecutorService = Executors
				.newScheduledThreadPool(10);
		GeneralXenonRestClient instance = new GeneralXenonRestClient(serverSet,
				executor, scheduledExecutorService);
		instance.start();
		return instance;
	}
	
}
