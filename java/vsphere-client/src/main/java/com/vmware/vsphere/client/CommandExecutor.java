package com.vmware.vsphere.client;

import static com.vmware.vsphere.client.config.VcClientProperties.INSTANCE;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vim25.AlreadyExistsFaultMsg;
import com.vmware.vim25.ConcurrentAccessFaultMsg;
import com.vmware.vim25.CustomizationFaultFaultMsg;
import com.vmware.vim25.DuplicateNameFaultMsg;
import com.vmware.vim25.FileFaultFaultMsg;
import com.vmware.vim25.InsufficientResourcesFaultFaultMsg;
import com.vmware.vim25.InvalidCollectorVersionFaultMsg;
import com.vmware.vim25.InvalidDatastoreFaultMsg;
import com.vmware.vim25.InvalidNameFaultMsg;
import com.vmware.vim25.InvalidPropertyFaultMsg;
import com.vmware.vim25.InvalidStateFaultMsg;
import com.vmware.vim25.MigrationFaultFaultMsg;
import com.vmware.vim25.OutOfBoundsFaultMsg;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.SnapshotFaultFaultMsg;
import com.vmware.vim25.TaskInProgressFaultMsg;
import com.vmware.vim25.VmConfigFaultFaultMsg;
import com.vmware.vsphere.client.commands.PutVMFiles;
import com.vmware.vsphere.client.commands.VMCreate;
import com.vmware.vsphere.client.commands.VMCreateFromImage;
import com.vmware.vsphere.client.commands.VMCreateWithExistingDisk;
import com.vmware.vsphere.client.commands.VMManageCD;

public class CommandExecutor {

	private static final Logger logger = LoggerFactory
			.getLogger(CommandExecutor.class);

	public static Map<String, String> createVm(Map<String, String> args) {
		Map<String, String> output = new HashMap<>();
		String vmname = args.get(CommandArgument.VM_NAME);
		VMCreateFromImage vmCreate = new VMCreateFromImage();
		vmCreate.setVmname(vmname);

			try {
				output.put(CommandOutput.VM_MOREF, vmCreate.createVmFromImage());
			} catch (RemoteException | InvalidPropertyFaultMsg
					| RuntimeFaultFaultMsg | InvalidCollectorVersionFaultMsg
					| OutOfBoundsFaultMsg | DuplicateNameFaultMsg
					| VmConfigFaultFaultMsg
					| InsufficientResourcesFaultFaultMsg
					| AlreadyExistsFaultMsg | InvalidDatastoreFaultMsg
					| FileFaultFaultMsg | InvalidStateFaultMsg
					| InvalidNameFaultMsg | TaskInProgressFaultMsg
					| SnapshotFaultFaultMsg | CustomizationFaultFaultMsg
					| MigrationFaultFaultMsg e) {
				logger.error("Error occurred while creating {} VM.", vmname, e);
			}

		return output;
	}

	public static Map<String, String> createVmWithExistingDisk(
			Map<String, String> args) {
		Map<String, String> output = new HashMap<>();
		VMCreateWithExistingDisk vmCreate = new VMCreateWithExistingDisk();
		vmCreate.setVirtualMachineName(args.get(CommandArgument.VM_NAME));
		vmCreate.setHostname(INSTANCE.getHostName());
		vmCreate.setDataCenterName(INSTANCE.getDatacenterName());
		vmCreate.setDiskPath(INSTANCE.getDiskPath());
		try {
			output.put(CommandOutput.VM_MOREF, vmCreate.createVirtualMachine());
		} catch (RemoteException | RuntimeFaultFaultMsg
				| InvalidPropertyFaultMsg | InvalidCollectorVersionFaultMsg
				| OutOfBoundsFaultMsg | DuplicateNameFaultMsg
				| VmConfigFaultFaultMsg | InsufficientResourcesFaultFaultMsg
				| AlreadyExistsFaultMsg | InvalidDatastoreFaultMsg
				| FileFaultFaultMsg | InvalidStateFaultMsg
				| InvalidNameFaultMsg | TaskInProgressFaultMsg e) {
			String message = "VM creation for parameters " + args + "failed.";
			logger.error(message, e);
			throw new RuntimeException(message, e);
		}
		return output;
	}

	/**
	 * Mounts ISO on given VM.
	 *
	 * @param args
	 *            Map with two entries {@link CommandArgument#VM_MOREF} : vm
	 *            moref {@link CommandArgument#ISO_LOCAL_PATH} : full path of
	 *            the ISO on datastore.
	 * @return empty map. TODO add result pass/failed.
	 */
	public static Map<String, String> mountIsoToVm(Map<String, String> args) {
		Map<String, String> output = new HashMap<>();
		VMManageCD vmCDAdd = new VMManageCD();
		vmCDAdd.setVmMoRef(args.get(CommandArgument.VM_MOREF));
		vmCDAdd.setIsoPath(args.get(CommandArgument.ISO_LOCAL_PATH));
		vmCDAdd.setOperation("add");
		vmCDAdd.setRemote("false");
		vmCDAdd.setConnect("true");
		vmCDAdd.setStartConnected("true");
		try {
			vmCDAdd.doOperation();
		} catch (RuntimeFaultFaultMsg | InvalidPropertyFaultMsg
				| DuplicateNameFaultMsg | TaskInProgressFaultMsg
				| InsufficientResourcesFaultFaultMsg | VmConfigFaultFaultMsg
				| InvalidDatastoreFaultMsg | FileFaultFaultMsg
				| ConcurrentAccessFaultMsg | InvalidStateFaultMsg
				| InvalidCollectorVersionFaultMsg | InvalidNameFaultMsg e) {
			String message = "VM CD ADD for parameters " + args + "failed.";
			logger.error(message, e);
			throw new RuntimeException(message, e);
		}
		return output;
	}
	
	public static void powerOn(String vmId) throws Exception  {
		VMCreate vmCreate = new VMCreate();
		vmCreate.powerOnVM(vmId);
		
	}
	
	public static void uploadAndAttachIso(String vmId, String isoFile) throws Exception {
		PutVMFiles putVMFiles = new PutVMFiles();
		putVMFiles.setDatacenter(INSTANCE.getDatacenterName());
		putVMFiles.setDatastore(INSTANCE.getDatastoreName());
		putVMFiles.setLocalPath(isoFile);
		putVMFiles.setRemotePath("/" + vmId + ".iso");
		putVMFiles.putFile();
		
		VMManageCD mountCd = new VMManageCD();
		mountCd.setVmMoRef(vmId);
		mountCd.setOperation("add");
		mountCd.setIsoPath("[" + INSTANCE.getDatastoreName() + "]" + vmId + ".iso");
		mountCd.setRemote("false");
		mountCd.setConnect("true");
		mountCd.setStartConnected("true");
		mountCd.doOperation();
	}
}