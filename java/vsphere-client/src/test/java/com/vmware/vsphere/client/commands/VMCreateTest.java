package com.vmware.vsphere.client.commands;

import static com.vmware.vsphere.client.config.VcClientProperties.INSTANCE;
import org.testng.annotations.Test;

@Test(enabled = false)
public class VMCreateTest {

	public void testCreateVm() throws Exception {
		VMCreate vmCreate = new VMCreate();
		vmCreate.setVirtualMachineName("new-test-vm");
		vmCreate.setHostname(INSTANCE.getHostName());
		vmCreate.setDataCenterName(INSTANCE.getDatacenterName());
		vmCreate.createVirtualMachine();
	}
}
