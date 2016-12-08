package com.vmware.photon.controller.clustermanager.servicedocuments;

import com.vmware.xenon.common.ServiceDocument;

public class TenantClusterQueryResult extends ServiceDocument {
	public String clusterName;
	public String clusterId;
}
