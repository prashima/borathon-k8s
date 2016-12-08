package com.vmware.photon.controller.clustermanager.tasks;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.photon.controller.clustermanager.servicedocuments.TenantClusterQueryResult;
import com.vmware.photon.controller.mockcloudstore.xenon.entity.ClusterService;
import com.vmware.photon.controller.mockcloudstore.xenon.entity.ClusterServiceFactory;
import com.vmware.photon.controller.xenon.client.VcsXenonRestClient;
import com.vmware.xenon.common.Operation;
import com.vmware.xenon.common.ServiceDocumentQueryResult;
import com.vmware.xenon.common.StatelessService;

public class VcsTenantClusterQueryTask extends StatelessService {
	private static final Logger logger = LoggerFactory.getLogger(VcsTenantClusterQueryTask.class);
	
	@Override
	public void handleGet(Operation op) {
		logger.info("In VcsTenantClusterQueryTask op {}", op);
		String tenantId = getSelfLink().split("/")[3];
		List<TenantClusterQueryResult> returnResult = new ArrayList<>();
		try {			
			Operation allClsGet = VcsXenonRestClient.getVcsRestClient().get(
		            ClusterServiceFactory.SELF_LINK);
			logger.info("All cluster links {}", allClsGet);
			
			ServiceDocumentQueryResult res = allClsGet.getBody(ServiceDocumentQueryResult.class);
			logger.info("Res {}", res);
			for(String clusterLink : res.documentLinks) {
				Operation clsOpRes = VcsXenonRestClient.getVcsRestClient().get(clusterLink);
				ClusterService.State cls = clsOpRes.getBody(ClusterService.State.class);
				if (cls.projectId.equals(tenantId)) {
					TenantClusterQueryResult tcqr = new TenantClusterQueryResult();
					tcqr.clusterId = clusterLink.split("/")[4];
					tcqr.clusterName = cls.clusterName;
					returnResult.add(tcqr);
				}
				
			}
					
		} catch (Throwable t) {
			t.printStackTrace();
			op.fail(t);
		}
		op.setBody(returnResult);
		op.complete();
	}

}
