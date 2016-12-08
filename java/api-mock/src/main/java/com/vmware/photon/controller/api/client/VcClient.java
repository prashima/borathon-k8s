package com.vmware.photon.controller.api.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.FutureCallback;
import com.vmware.photon.controller.api.model.NetworkConnection;
import com.vmware.photon.controller.api.model.ResourcePoolConfig;
import com.vmware.photon.controller.api.model.Task;
import com.vmware.photon.controller.api.model.Task.Entity;
import com.vmware.photon.controller.api.model.VmCreateSpec;
import com.vmware.photon.controller.api.model.VmNetworks;
import com.vmware.vsphere.client.CommandArgument;
import com.vmware.vsphere.client.CommandExecutor;
import com.vmware.vsphere.client.CommandOutput;
import com.vmware.vsphere.client.commands.VmNetwork.Network;

public class VcClient {
	private static final Logger logger = LoggerFactory.getLogger(VcClient.class);
	private static volatile VcClient instance = null;
	
	public static synchronized VcClient getVcClient() {
		if (instance == null) {
			instance = new VcClient();
		}
		return instance;
	}

	public void createVmAsync(String projectId, VmCreateSpec composeVmCreateSpec, FutureCallback<Task> callback) {
		logger.info("Creating VM in project {} with specification {}", projectId, composeVmCreateSpec);
		Map<String, String> args = new HashMap<>();
		args.put(CommandArgument.VM_NAME, composeVmCreateSpec.getName());
		args.put(CommandArgument.RES_POOL, composeVmCreateSpec.getTenantResourcePool());
		Map<String, String> output = CommandExecutor.createVm(args);

		Task vmCreateTask = new Task();
		String id = output.get(CommandOutput.VM_MOREF);
		if (id == null) {
			callback.onFailure(new RuntimeException("Create VM failed"));
			return;
		}
		vmCreateTask.setId(id);
		vmCreateTask.setState("COMPLETED");
		Entity vmEntity = new Entity();
		vmEntity.setId(id);
		vmCreateTask.setEntity(vmEntity);
		logger.info("Successfully created VM with Id {} in project {}", id, projectId);
		callback.onSuccess(vmCreateTask);
	}

	public Task uploadAndAttachIso(String vmId, String isoFile) throws Exception {
		logger.info("Uploading and attaching isoFile {} to VM {}", isoFile, vmId);
		// TODO do the upload and attach ISO
		CommandExecutor.uploadAndAttachIso(vmId, isoFile);
		
		Task uploadAttachTask = new Task();
		String id = UUID.randomUUID().toString();
		uploadAttachTask.setId(id);
		uploadAttachTask.setState("COMPLETED");
		Entity vmEntity = new Entity();
		vmEntity.setId(vmId);
		uploadAttachTask.setEntity(vmEntity);
		logger.info("Successfully uploaded and attached isoFile {} to VM {}", isoFile, vmId);
		return uploadAttachTask;
	}

	public void performStartOperationAsync(String vmId, FutureCallback<Task> futureCallback) {
		logger.info("Starting VM {}", vmId);
		// TODO Start the VM
		
		try {
			CommandExecutor.powerOn(vmId);
		} catch (Exception e) {
			futureCallback.onFailure(e);
		}
		Task startVm = new Task();
		String id = UUID.randomUUID().toString();
		startVm.setId(id);
		startVm.setState("COMPLETED");
		Entity vmEntity = new Entity();
		vmEntity.setId(vmId);
		startVm.setEntity(vmEntity);
		futureCallback.onSuccess(startVm);
		logger.info("Successfully started VM {}", vmId);
		
	}

	public void getNetworksAsync(String vmId, FutureCallback<Task> futureCallback) {
		logger.info("Getting network details for VM {}", vmId);
		Network network = CommandExecutor.getVmNetwork(vmId);

		Task networkTask = new Task();
		String id = UUID.randomUUID().toString();
		networkTask.setId(id);
		networkTask.setState("COMPLETED");
		Entity vmEntity = new Entity();
		vmEntity.setId(vmId);
		networkTask.setEntity(vmEntity);
		VmNetworks vmNetwork = new VmNetworks();
		Set<NetworkConnection> networkConnections = new HashSet<>();
		NetworkConnection netConn = new NetworkConnection(network.getMacAddress());
		netConn.setIpAddress(network.getIpAddress());
		networkConnections.add(netConn);
		vmNetwork.setNetworkConnections(networkConnections);
		networkTask.setResourceProperties(vmNetwork);
		futureCallback.onSuccess(networkTask);
		logger.info("Successfully got network details for VM {}", vmId);
		
	}

	public String createResourcePool(String tenantName, ResourcePoolConfig resPoolConfig) {
		logger.info("Creating tenant in cluster {}", resPoolConfig.getResMoRef());
		try {
			return CommandExecutor.createResourcePool(tenantName, resPoolConfig.getResMoRef(),
					resPoolConfig.getCpuReservationsInMHz(), resPoolConfig.getCpuLimitsInMHz(), resPoolConfig.getMemReservationsInMB(), resPoolConfig.getMemLimitsInMB());
		} catch (Exception e) {
			logger.error("Exception occurred", e);
			throw new RuntimeException(e);
		}
	}
}
