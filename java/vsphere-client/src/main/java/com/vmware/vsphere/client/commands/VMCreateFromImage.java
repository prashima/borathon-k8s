package com.vmware.vsphere.client.commands;

import static com.vmware.vsphere.client.config.VcClientProperties.INSTANCE;

import java.rmi.RemoteException;

import com.vmware.vim25.AlreadyExistsFaultMsg;
import com.vmware.vim25.CustomizationFaultFaultMsg;
import com.vmware.vim25.DuplicateNameFaultMsg;
import com.vmware.vim25.FileFaultFaultMsg;
import com.vmware.vim25.InsufficientResourcesFaultFaultMsg;
import com.vmware.vim25.InvalidCollectorVersionFaultMsg;
import com.vmware.vim25.InvalidDatastoreFaultMsg;
import com.vmware.vim25.InvalidNameFaultMsg;
import com.vmware.vim25.InvalidPropertyFaultMsg;
import com.vmware.vim25.InvalidStateFaultMsg;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.MigrationFaultFaultMsg;
import com.vmware.vim25.OutOfBoundsFaultMsg;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.SnapshotFaultFaultMsg;
import com.vmware.vim25.TaskInProgressFaultMsg;
import com.vmware.vim25.VmConfigFaultFaultMsg;

public class VMCreateFromImage extends BaseCommand {

	 private static final String KUBERNETES_IMAGE_VM = "kubernetes-image-vm";
	 private static final String KUBERNETES_IMAGE_SNAPSHOT =
	 "kubernetes-image-vm-snapshot";
	// private static final String KUBERNETES_IMAGE_VM = "vm1";
	// private static final String KUBERNETES_IMAGE_SNAPSHOT = "vm1-snapshot";

	private String vmname;
	private String resPool;

	public void setVmname(String vmname) {
		this.vmname = vmname;
	}
	
	public void setResPool(String resPool) {
		this.resPool = resPool;
	}

	public String createVmFromImage() throws InvalidPropertyFaultMsg,
			RuntimeFaultFaultMsg, RemoteException,
			InvalidCollectorVersionFaultMsg, OutOfBoundsFaultMsg,
			DuplicateNameFaultMsg, VmConfigFaultFaultMsg,
			InsufficientResourcesFaultFaultMsg, AlreadyExistsFaultMsg,
			InvalidDatastoreFaultMsg, FileFaultFaultMsg, InvalidStateFaultMsg,
			InvalidNameFaultMsg, TaskInProgressFaultMsg, SnapshotFaultFaultMsg,
			CustomizationFaultFaultMsg, MigrationFaultFaultMsg {

		ManagedObjectReference vmMOR = getVmMoRef(KUBERNETES_IMAGE_VM);
		if (vmMOR == null) {
			vmMOR = importAndDeployImageVM();//createImageVm();
		}

		checkAndCreateSnapshot();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		VMLinkedClone vmLinkedClone = new VMLinkedClone();
		vmLinkedClone.setVirtualMachineName(KUBERNETES_IMAGE_VM);
		vmLinkedClone.setSnapshotName(KUBERNETES_IMAGE_SNAPSHOT);
		vmLinkedClone.setCloneName(vmname);
		vmLinkedClone.setTargetResourcePool(resPool);
		String cloneMoRef = vmLinkedClone.createLinkedClone();
		return cloneMoRef;
	}

	private ManagedObjectReference importAndDeployImageVM()
			throws InvalidPropertyFaultMsg, RuntimeFaultFaultMsg {
		OVFManagerImportLocalVApp ovfCmd = new OVFManagerImportLocalVApp();
		ovfCmd.setDatastore(INSTANCE.getDatastoreName());
		ovfCmd.setHost(INSTANCE.getHostName());
		ovfCmd.setLocalPath(INSTANCE.getLocalFilePath());
		ovfCmd.setVappName(KUBERNETES_IMAGE_VM);
		ovfCmd.importVApp();
		return getVmMoRef(KUBERNETES_IMAGE_VM);
	}

	private void checkAndCreateSnapshot() throws InvalidPropertyFaultMsg,
			RuntimeFaultFaultMsg, TaskInProgressFaultMsg,
			SnapshotFaultFaultMsg, VmConfigFaultFaultMsg, FileFaultFaultMsg,
			InvalidStateFaultMsg, InvalidCollectorVersionFaultMsg,
			InvalidNameFaultMsg, InsufficientResourcesFaultFaultMsg {
		VMSnapshot vmSnapshot = new VMSnapshot();
		vmSnapshot.setVirtualMachineName(KUBERNETES_IMAGE_VM);
		vmSnapshot.setSnapshotname(KUBERNETES_IMAGE_SNAPSHOT);
		vmSnapshot.setOperation("list");
		if (!vmSnapshot.snapshotExists()) {
			vmSnapshot.setOperation("create");
			vmSnapshot.setDescription("Snapshoting PhotonOS k8s image.");
			vmSnapshot.createSnapshot();
		}
	}

	ManagedObjectReference getVmMoRef(String vmname)
			throws InvalidPropertyFaultMsg, RuntimeFaultFaultMsg {
		ManagedObjectReference propCol = vcService.getConnection()
				.getServiceContent().getPropertyCollector();
		return vcService.getGetMOREFs()
				.vmByVMname(KUBERNETES_IMAGE_VM, propCol);
	}
}
