package com.vmware.photon.controller.clustermanager.tasks;

import com.vmware.photon.controller.clustermanager.servicedocuments.TenantSpec;
import com.vmware.photon.controller.common.xenon.ServiceUriPaths;
import com.vmware.xenon.common.FactoryService;
import com.vmware.xenon.common.Service;

public class VcsTenantFactoryService extends FactoryService {
	public static final String SELF_LINK = ServiceUriPaths.VCS_TENANT;

	  public VcsTenantFactoryService() {
	    super(TenantSpec.class);
	  }

	@Override
	public Service createServiceInstance() throws Throwable {
		return new VcsTenantService();
	}

}
