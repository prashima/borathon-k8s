package com.vmware.vsphere.client.commands;

import org.testng.annotations.Test;

@Test
public class VMCreateFromImageTest {

	@Test
	public void testVMCreate() throws Exception {
		VMCreateFromImage vmCreate = new VMCreateFromImage();
		vmCreate.setVmname("etcd1");
		vmCreate.createVmFromImage();
	}
}
