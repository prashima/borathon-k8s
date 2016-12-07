package com.vmware.photon.controller.clustermanager.tasks;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.photon.controller.clustermanager.servicedocuments.KubernetesClusterCreateTask;
import com.vmware.photon.controller.clustermanager.servicedocuments.KubernetesClusterCreateTask.TaskState;
import com.vmware.photon.controller.common.xenon.ControlFlags;
import com.vmware.photon.controller.common.xenon.InitializationUtils;
import com.vmware.photon.controller.common.xenon.PatchUtils;
import com.vmware.photon.controller.common.xenon.ServiceUriPaths;
import com.vmware.photon.controller.common.xenon.ServiceUtils;
import com.vmware.photon.controller.common.xenon.TaskUtils;
import com.vmware.photon.controller.mockcloudstore.xenon.entity.ClusterService;
import com.vmware.photon.controller.mockcloudstore.xenon.entity.ClusterServiceFactory;
import com.vmware.photon.controller.xenon.client.VcsXenonRestClient;
import com.vmware.xenon.common.Operation;
import com.vmware.xenon.common.StatelessService;

public class VcsClusterCreateTaskService extends StatelessService {
	private static final Logger logger = LoggerFactory.getLogger(VcsClusterCreateTaskService.class);

	public VcsClusterCreateTaskService() {
		super(ClusterService.State.class);
	}

	@Override
	public void handleStart(Operation start) {
		ServiceUtils.logInfo(this, "Starting service %s with operation (%s)", getSelfLink(), start);
		ClusterService.State cluster = start.getBody(ClusterService.State.class);
		cluster.documentSelfLink = UUID.randomUUID().toString();
		ServiceUtils.logInfo(this, "Cluster = (%s)", cluster);
		start.complete();
		try {
		    Operation op = VcsXenonRestClient.getVcsRestClient().post(
		            ClusterServiceFactory.SELF_LINK,
		            cluster);
		    KubernetesClusterCreateTask createTask = new KubernetesClusterCreateTask();
		    createTask.clusterId = cluster.documentSelfLink;

		    // Post createTask to KubernetesClusterCreateTaskService
		    Operation operation = VcsXenonRestClient.getVcsRestClient().post(
		        ServiceUriPaths.KUBERNETES_CLUSTER_CREATE_TASK_SERVICE, createTask);


		} catch(Throwable t) {
			t.printStackTrace();
			ServiceUtils.logSevere(this, "Service (%s) failed with exception", getSelfLink(), t);
		}

	}

	@Override
	public void handlePatch(Operation patch) {
		ServiceUtils.logInfo(this, "Handling patch for service %s with operation(%s)", getSelfLink(), patch);
		patch.complete();
	}
}
