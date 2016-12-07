package com.vmware.vsphere.client.config;

import org.testng.annotations.Test;

public class VcClientPropertiesTest {
  @Test(enabled = false)
  public void testReadAndPrintProperties() {
	  System.out.println(VcClientProperties.INSTANCE.getUrl());
	  System.out.println(VcClientProperties.INSTANCE.getUsername());
	  System.out.println(VcClientProperties.INSTANCE.getPassword());
	  System.out.println(VcClientProperties.INSTANCE.getDatacenterName());
	  System.out.println(VcClientProperties.INSTANCE.getDatastoreName());
	  System.out.println(VcClientProperties.INSTANCE.getHostName());
	  System.out.println(VcClientProperties.INSTANCE.getDiskPath());
	  System.out.println(VcClientProperties.INSTANCE.getLocalFilePath());
	  System.out.println(VcClientProperties.INSTANCE.getRemoteFilePath());
	  System.out.println(VcClientProperties.INSTANCE.isSslTrustAll());
  }
}
