package com.vmware.photon.controller.clustermanager.tasks;

import com.vmware.xenon.common.Operation;
import com.vmware.xenon.common.StatefulService;
import com.vmware.photon.controller.clustermanager.servicedocuments.TenantSpec;
import com.vmware.photon.controller.common.xenon.ServiceUtils;

public class VcsTenantService extends StatefulService {

	public VcsTenantService() {
		super(TenantSpec.class);
		super.toggleOption(ServiceOption.PERSISTENCE, true);
	    super.toggleOption(ServiceOption.REPLICATION, true);
	    super.toggleOption(ServiceOption.OWNER_SELECTION, true);
	}
	
	@Override
	public void handleStart(Operation start) {
	    ServiceUtils.logInfo(this, "In VcsTenantService. Starting service %s", getSelfLink());
	    TenantSpec spec = start.getBody(TenantSpec.class);
	    spec.setId(getSelfLink().split("/")[3]);
	    start.setBody(spec);
	    start.complete();
	}
}
