package com.vmware.vsphere.client.commands;

import static com.vmware.vsphere.client.config.VcClientProperties.INSTANCE;
import org.testng.annotations.Test;

@Test(enabled = false)
public class PutVMFilesTest {

	public void testCreateVm() throws Exception {
		PutVMFiles putVMFiles = new PutVMFiles();
		putVMFiles.setDatacenter(INSTANCE.getDatacenterName());
		putVMFiles.setDatastore(INSTANCE.getDatastoreName());
		putVMFiles.setLocalPath(INSTANCE.getLocalFilePath());
		putVMFiles.setRemotePath(INSTANCE.getRemoteFilePath());
		putVMFiles.putFile();
	}
}
