package com.vmware.photon.controller.api.model;

public class ResourcePoolConfig {
	private String resMoRef;
	private long cpuReservationsInMHz;
	private long cpuLimitsInMHz;
	private long memReservationsInMB;
	private long memLimitsInMB;
	private long diskSpaceInMB;
	
	public long getCpuReservationsInMHz() {
		return cpuReservationsInMHz;
	}
	public void setCpuReservationsInMHz(long cpuReservationsInMHz) {
		this.cpuReservationsInMHz = cpuReservationsInMHz;
	}
	public long getCpuLimitsInMHz() {
		return cpuLimitsInMHz;
	}
	public void setCpuLimitsInMHz(long cpuLimitsInMHz) {
		this.cpuLimitsInMHz = cpuLimitsInMHz;
	}
	public long getMemReservationsInMB() {
		return memReservationsInMB;
	}
	public void setMemReservationsInMB(long memReservationsInMB) {
		this.memReservationsInMB = memReservationsInMB;
	}
	public long getMemLimitsInMB() {
		return memLimitsInMB;
	}
	public void setMemLimitsInMB(long memLimitsInMB) {
		this.memLimitsInMB = memLimitsInMB;
	}
	public long getDiskSpaceInMB() {
		return diskSpaceInMB;
	}
	public void setDiskSpaceInMB(long diskSpaceInMB) {
		this.diskSpaceInMB = diskSpaceInMB;
	}
	public String getResMoRef() {
		return resMoRef;
	}
	public void setResMoRef(String resMoRef) {
		this.resMoRef = resMoRef;
	}
}
