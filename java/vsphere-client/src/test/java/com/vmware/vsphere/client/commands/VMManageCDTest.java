package com.vmware.vsphere.client.commands;

import static com.vmware.vsphere.client.config.VcClientProperties.INSTANCE;

import org.testng.annotations.Test;

@Test
public class VMManageCDTest {
	@Test
	public void testCDAdd() throws Exception {
		VMManageCD vmCDAdd = new VMManageCD();
		vmCDAdd.setVmMoRef("vm-31");
		vmCDAdd.setOperation("add");
		vmCDAdd.setIsoPath("[datastore-sdk] coreos_production_iso_image.iso");
		vmCDAdd.setRemote("false");
		vmCDAdd.setConnect("true");
		vmCDAdd.doOperation();
	}
}
