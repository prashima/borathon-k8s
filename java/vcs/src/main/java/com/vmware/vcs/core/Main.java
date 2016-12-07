package com.vmware.vcs.core;

import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.photon.controller.clustermanager.ClusterManagerFactory;
import com.vmware.photon.controller.clustermanager.servicedocuments.ClusterManagerConstants;
import com.vmware.photon.controller.common.clients.AgentControlClientFactory;
import com.vmware.photon.controller.common.clients.HostClientFactory;
import com.vmware.photon.controller.common.logging.LoggingConfiguration;
import com.vmware.photon.controller.common.logging.LoggingFactory;
import com.vmware.photon.controller.common.thrift.ServerSet;
import com.vmware.photon.controller.common.thrift.StaticServerSet;
import com.vmware.photon.controller.common.thrift.ThriftModule;
import com.vmware.photon.controller.common.utils.VcsProperties;
import com.vmware.photon.controller.common.xenon.ServiceHostUtils;
import com.vmware.xenon.common.ServiceHost;
import com.vmware.xenon.common.ServiceHost.Arguments;
import com.vmware.xenon.common.UriUtils;
import com.vmware.xenon.services.common.LuceneDocumentIndexService;
import com.vmware.xenon.services.common.RootNamespaceService;

public class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws Throwable {
		LoggingFactory.bootstrap();
		VcsProperties.init(args[0]);
		logger.info("args: " + Arrays.toString(args));
		new LoggingFactory(new LoggingConfiguration(), "photon-controller-core").configure();

		ThriftModule thriftModule = new ThriftModule();

		ServiceHost xenonHost = startXenonHost(thriftModule);
		Runtime.getRuntime().addShutdownHook(new Thread() {
		      @Override
		      public void run() {
		        logger.info("Shutting down");
		        xenonHost.stop();
		        logger.info("Done");
		        LoggingFactory.detachAndStop();
		      }
		    });

	}

	private static ServiceHost startXenonHost(ThriftModule thriftModule) throws Throwable {
		
		
		ServiceHost serviceHost = new VcsServerHost();
		Arguments arguments = new Arguments();
	    arguments.port = 19000;
	    arguments.bindAddress = "0.0.0.0";
	    arguments.sandbox = Paths.get(VcsProperties.getVcsStoragePath());
	    arguments.peerNodes = new String[] {"http://127.0.0.1:19000"};

        arguments.publicUri = UriUtils.buildUri("127.0.0.1", 19000, null, null).toString();

        serviceHost.initialize(arguments);
        serviceHost.start();
        /**
        * Xenon currently uses a garbage collection algorithm for its Lucene index searchers which
        * results in index searchers being closed while still in use by paginated queries. As a
        * temporary workaround until the issue is fixed on the framework side (v0.7.6), raise the
        * threshold at which index searcher garbage collection is triggered to limit the impact of
        * this issue.
        */
       LuceneDocumentIndexService.setSearcherCountThreshold(1024);

       serviceHost.getClient().setConnectionLimitPerHost(1024);
       serviceHost.startDefaultCoreServicesSynchronously();
       // Start all core factories
       ServiceHostUtils.startServices(serviceHost, new Class[]{RootNamespaceService.class});
       ServiceHostUtils.startServices(serviceHost, ClusterManagerFactory.FACTORY_SERVICES);
       return serviceHost;	    
	}
	
	static class VcsServerHost extends ServiceHost {
		public VcsServerHost() {
		}
	}

}
