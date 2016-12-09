package com.vmware.photon.controller.clustermanager.tasks;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.FutureCallback;
import com.vmware.photon.controller.api.model.Vm;
import com.vmware.photon.controller.clustermanager.servicedocuments.ClusterVmsQueryResult;
import com.vmware.photon.controller.xenon.client.GeneralApiClient;
import com.vmware.xenon.common.Operation;
import com.vmware.xenon.common.StatelessService;

public class VcsClusterVmsQueryTask extends StatelessService {
	private static final Logger logger = LoggerFactory.getLogger(VcsClusterVmsQueryTask.class);
	
	@Override
	public void handleGet(Operation op) {
		logger.info("In VcsClusterVmsQueryTask op {}", op);
		String clusterId = getSelfLink().split("/")[4];
		List<ClusterVmsQueryResult> results = new ArrayList<>();
		logger.info("Cluster ID {}", clusterId);
		GeneralApiClient.getVmAsync(clusterId, new FutureCallback<List<Vm>>() {
			
			@Override
			public void onSuccess(List<Vm> result) {
				for(Vm vm: result) {
					ClusterVmsQueryResult a = new ClusterVmsQueryResult();
					a.vmId = vm.getId();
					a.vmName = vm.getName();
					results.add(a);
				}
					
			}
			
			@Override
			public void onFailure(Throwable t) {
				
			}
		});
		op.setBody(results);
		op.complete();
	}

}
