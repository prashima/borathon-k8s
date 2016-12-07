package com.vmware.photon.controller.clustermanager.servicedocuments;

import com.vmware.xenon.common.ServiceDocument;

public class TenantSpec extends ServiceDocument {
	private String id;
	private String name;
	private String description;
	private ResourceAllocationSpec[] res;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ResourceAllocationSpec[] getRes() {
		return res;
	}

	public void setRes(ResourceAllocationSpec[] res) {
		this.res = res;
	}

}
