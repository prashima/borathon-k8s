/*
 * Copyright 2015 VMware, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED.  See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.vmware.photon.controller.clustermanager.utils;

import com.vmware.photon.controller.api.client.ApiClient;
import com.vmware.photon.controller.api.client.VcClient;
import com.vmware.photon.controller.api.client.VchClient;
import com.vmware.photon.controller.clustermanager.ClusterManagerFactory;
import com.vmware.photon.controller.clustermanager.ClusterManagerFactoryProvider;
import com.vmware.photon.controller.clustermanager.clients.EtcdClient;
import com.vmware.photon.controller.clustermanager.clients.KubernetesClient;
import com.vmware.photon.controller.clustermanager.clients.MesosClient;
import com.vmware.photon.controller.clustermanager.clients.SwarmClient;
import com.vmware.photon.controller.common.thrift.StaticServerSet;
import com.vmware.photon.controller.common.utils.VcsProperties;
import com.vmware.photon.controller.common.xenon.CloudStoreHelper;
import com.vmware.photon.controller.common.xenon.host.PhotonControllerXenonHost;
import com.vmware.xenon.common.Service;

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.ssl.SSLContexts;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * This class implements utility functions for the deployer Xenon host.
 */
public class HostUtils {
	private static final BlockingQueue<Runnable> blockingQueue = new LinkedBlockingDeque<>();
	private static final ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(
	        new ThreadPoolExecutor(
	            16,
	            32,
	            10,
	            TimeUnit.SECONDS,
	            blockingQueue));
	static final CloseableHttpAsyncClient httpClient;
	
	static {
		
	    try {
	      SSLContext sslcontext = SSLContexts.custom()
	          .loadTrustMaterial((chain, authtype) -> true)
	          .build();
	      httpClient = HttpAsyncClientBuilder.create()
	          .setHostnameVerifier(SSLIOSessionStrategy.ALLOW_ALL_HOSTNAME_VERIFIER)
	          .setSSLContext(sslcontext)
	          .build();
	      httpClient.start();
	    } catch (Throwable e) {
	      throw new RuntimeException(e);
	    }
	}

  public static ApiClient getApiClient(Service service) {
    return getClusterManagerFactory(service).createApiClient();
  }

  public static EtcdClient getEtcdClient(Service service) {
    //return getClusterManagerFactory(service).createEtcdClient();
	  
	  return new EtcdClient(httpClient);
  }

  public static KubernetesClient getKubernetesClient(Service service) {
//    return getClusterManagerFactory(service).createKubernetesClient();
    return new KubernetesClient(httpClient);
  }

  public static MesosClient getMesosClient(Service service) {
    return getClusterManagerFactory(service).createMesosClient();
  }

  public static SwarmClient getSwarmClient(Service service) {
    return getClusterManagerFactory(service).createSwarmClient();
  }
  
  public static ListeningExecutorService getVcsListeningExecutorService() {
	  return listeningExecutorService;
  }

  public static ListeningExecutorService getListeningExecutorService(Service service) {
    return getClusterManagerFactory(service).getListeningExecutorServiceInstance();
  }
  
  public static VcClient getVcClient() {
	  return VcClient.getVcClient();
	  
  }

  public static VchClient getVchClient() {
	  return VchClient.getVchClient();  
  }

  /**
   * Added as part off VCS work.
   * 
   * @param service
   * @return
   */
  public static CloudStoreHelper createCloudStoreHelper(Service service) {
		return new CloudStoreHelper(new StaticServerSet(new InetSocketAddress("127.0.0.1", 19000)));
  }
  
  public static CloudStoreHelper getCloudStoreHelper(Service service) {
	  return new CloudStoreHelper(new StaticServerSet(new InetSocketAddress("127.0.0.1", 19000)));
    //return getClusterManagerFactory(service).createCloudStoreHelper();
  }
  
  public static String getClusterManagerScriptsDirectory() {
	  // Make this point to the cluster manager's script directory
	  return VcsProperties.getClusterManagerScriptsDir();
  }

  public static String getScriptsDirectory(Service service) {
    return getClusterManagerFactory(service).getScriptsDirectory();
  }

  public static ClusterManagerFactory getClusterManagerFactory(Service service) {
    PhotonControllerXenonHost photonControllerXenonHost = (PhotonControllerXenonHost) service.getHost();
    return  ((ClusterManagerFactoryProvider) photonControllerXenonHost.getDeployer()).getClusterManagerFactory();
  }
}
