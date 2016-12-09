package com.vmware.photon.controller.clustermanager.tasks;

import com.vmware.photon.controller.common.xenon.ServiceUriPaths;
import com.vmware.xenon.common.FactoryService;
import com.vmware.xenon.common.Service;

public class VcsKubernetesClusterHealthCheckFactoryService extends FactoryService {
	
	public static final String SELF_LINK = ServiceUriPaths.VCS_K8S_HEALTH_CHECK;
	
	public VcsKubernetesClusterHealthCheckFactoryService() {
		super(VcsKubernetesClusterHealthCheckTaskService.State.class);
    }

     @Override
	 public Service createServiceInstance() throws Throwable {
    	 return new VcsKubernetesClusterHealthCheckTaskService();
	 }
}
