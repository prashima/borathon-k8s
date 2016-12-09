package com.vmware.photon.controller.xenon.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import com.google.common.util.concurrent.FutureCallback;
import com.vmware.photon.controller.api.model.Vm;
import com.vmware.photon.controller.clustermanager.tasks.VmProvisionTaskService;
import com.vmware.photon.controller.common.xenon.ServiceUriPaths;
import com.vmware.photon.controller.common.xenon.exceptions.BadRequestException;
import com.vmware.photon.controller.common.xenon.exceptions.DocumentNotFoundException;
import com.vmware.xenon.common.Operation;
import com.vmware.xenon.common.ServiceDocumentQueryResult;
import com.vmware.xenon.common.TaskState.TaskStage;

public class GeneralApiClient {

	public static <T> void getAsync(String url, Class<T> className, FutureCallback<T> callback) {
		try {
			URL serverUrl = new URL(url);
			Operation op = GeneralXenonRestClient.getVcsRestClient(url).get(serverUrl.getPath());
			T result = op.getBody(className);
			callback.onSuccess(result);
		} catch (BadRequestException | DocumentNotFoundException
				| TimeoutException | InterruptedException | MalformedURLException e) {
			e.printStackTrace();
			callback.onFailure(e);
		}
	}

	public static void getVmAsync(String clusterId, FutureCallback<List<Vm>> callback) {
		try {
			Operation op = VcsXenonRestClient.getVcsRestClient().get(
					ServiceUriPaths.VM_PROVISIONING_TASK);
			ServiceDocumentQueryResult queryResult = op
					.getBody(ServiceDocumentQueryResult.class);
			if (queryResult != null) {
				List<String> documentLinks = queryResult.documentLinks;
				List<Vm> vms = new ArrayList<>(documentLinks.size());
				if (documentLinks != null) {
					for (String links : documentLinks) {
						op = VcsXenonRestClient.getVcsRestClient().get(links);
						VmProvisionTaskService.State vmTask = op
								.getBody(VmProvisionTaskService.State.class);
						if (vmTask.taskState.stage != TaskStage.FAILED
								&& vmTask.taskState.stage != TaskStage.CANCELLED) {
							for(String tag : vmTask.vmTags) {
								if (tag.equals("cluster:" + clusterId)) {
									vms.add(getVm(vmTask));
									break;
								}
							}							
						}
					}
					callback.onSuccess(vms);
				}
			} else {
				callback.onFailure(null);
			}
		} catch (BadRequestException | DocumentNotFoundException
				| TimeoutException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static Vm getVm(VmProvisionTaskService.State vmTask) {
		Vm vm = new Vm();
		vm.setId(vmTask.vmId);
		vm.setName(vmTask.vmName);
		vm.setTags(vmTask.vmTags);
		return vm;
	}
}
