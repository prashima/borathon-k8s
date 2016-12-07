package com.vmware.vsphere.client.commands;

import static com.vmware.vsphere.client.config.VcClientProperties.INSTANCE;
import org.testng.annotations.Test;

@Test(enabled = true)
public class VMCreateWithExistingDiskTest {

	public void testCreateVm() throws Exception {
		VMCreateWithExistingDisk vmCreate = new VMCreateWithExistingDisk();
		vmCreate.setVirtualMachineName("new-test-vm");
		vmCreate.setHostname(INSTANCE.getHostName());
		vmCreate.setDataCenterName(INSTANCE.getDatacenterName());
		vmCreate.setDiskPath(INSTANCE.getDiskPath());
		vmCreate.createVirtualMachine();
	}
}
