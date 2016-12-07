package com.vmware.photon.controller.xenon.client;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.vmware.photon.controller.common.thrift.ServerSet;
import com.vmware.photon.controller.common.thrift.StaticServerSet;
import com.vmware.photon.controller.common.xenon.XenonRestClient;

public class VcsXenonRestClient extends XenonRestClient {
	private static volatile VcsXenonRestClient instance = null;

	private VcsXenonRestClient(ServerSet serverSet, ExecutorService executor,
			ScheduledExecutorService scheduledExecutorService) {
		super(serverSet, executor, scheduledExecutorService);
	}
	
	public static synchronized VcsXenonRestClient getVcsRestClient() {
		if (instance == null) {
			StaticServerSet serverSet = new StaticServerSet(new InetSocketAddress("127.0.0.1", 19000));
			ExecutorService executor = Executors.newFixedThreadPool(16);
			ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
			instance = new VcsXenonRestClient(serverSet, executor, scheduledExecutorService);
			instance.start();
		}
		return instance;
	}
	
}
