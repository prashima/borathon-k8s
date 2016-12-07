package com.vmware.photon.controller.clustermanager.tasks;

import com.vmware.photon.controller.clustermanager.servicedocuments.KubernetesClusterCreateTask;
import com.vmware.photon.controller.common.xenon.ServiceUriPaths;
import com.vmware.photon.controller.mockcloudstore.xenon.entity.ClusterService;
import com.vmware.xenon.common.FactoryService;
import com.vmware.xenon.common.Service;
import com.vmware.xenon.common.ServiceDocument;

public class VcsClusterFactoryService extends FactoryService {

	public static final String SELF_LINK = ServiceUriPaths.VCS_CLUSTER_CREATE;

	  public VcsClusterFactoryService() {
	    super(ClusterService.State.class);
	  }

	@Override
	public Service createServiceInstance() throws Throwable {
		return new VcsClusterCreateTaskService();
	}

}
