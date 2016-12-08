package com.vmware.photon.controller.clustermanager;

import com.vmware.xenon.common.ServiceHost;

public class VcsHelper {
	private static ServiceHost vcsHost = null;
	
	public static void setVcsHost(ServiceHost host) {
		vcsHost = host;
	}
	
	public static ServiceHost getServiceHost() {
		return vcsHost;
	}

}
