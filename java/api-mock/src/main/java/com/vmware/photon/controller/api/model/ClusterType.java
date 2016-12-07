package com.vmware.photon.controller.api.model;

public enum ClusterType {
	KUBERNETES, MESOS, SWARM;

	// String used in swagger documentation.
	public static final String ALLOWABLE_VALUES = "KUBERNETES, MESOS, SWARM";
}
