package com.vmware.photon.controller.api.model;

import java.util.Set;

public class Vm {

	private String name;
	private String id;
	private Set<String> tags;

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public Set<String> getTags() {
		return tags;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}
}
