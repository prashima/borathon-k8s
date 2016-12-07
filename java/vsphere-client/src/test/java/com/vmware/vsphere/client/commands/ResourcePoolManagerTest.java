package com.vmware.vsphere.client.commands;

import org.testng.annotations.Test;

@Test(enabled = false)
public class ResourcePoolManagerTest {
	public void testCreateResPool() throws Exception {
		ResourcePoolManager resPoolMgr = new ResourcePoolManager();
		resPoolMgr.createResoucePool("pool1", "resgroup-153", 1000, 2000, 1000, 2000);
	}

}
