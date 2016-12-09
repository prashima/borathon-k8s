package com.vmware.photon.controller.clustermanager.servicedocuments;

import com.vmware.xenon.common.ServiceDocument;

public class ClusterVmsQueryResult extends ServiceDocument {
	public String vmId;
	public String vmName;
}
