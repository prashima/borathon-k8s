package com.vmware.photon.controller.clustermanager.tasks;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.vmware.photon.controller.common.xenon.ServiceUtils;
import com.vmware.photon.controller.common.xenon.deployment.NoMigrationDuringDeployment;
import com.vmware.photon.controller.common.xenon.migration.NoMigrationDuringUpgrade;
import com.vmware.photon.controller.mockcloudstore.xenon.entity.ClusterService;
import com.vmware.photon.controller.xenon.client.VcsXenonRestClient;
import com.vmware.xenon.common.Operation;
import com.vmware.xenon.common.Service;
import com.vmware.xenon.common.ServiceDocument;
import com.vmware.xenon.common.ServiceDocumentQueryResult;
import com.vmware.xenon.common.StatefulService;
import com.vmware.photon.controller.api.model.ClusterHealth;
import com.vmware.photon.controller.clustermanager.utils.HostUtils;
import com.vmware.photon.controller.common.utils.VcsProperties;
import com.vmware.photon.controller.common.xenon.ServiceUriPaths;


public class VcsKubernetesClusterHealthCheckTaskService extends StatefulService {
	
	public VcsKubernetesClusterHealthCheckTaskService() {
		super(State.class);
		super.toggleOption(Service.ServiceOption.PERSISTENCE, true);
		super.toggleOption(ServiceOption.PERIODIC_MAINTENANCE, true);
		
		// TODO: As of now checking health state of the cluster after every minute.
		// Need to fine tune according the final product requirements.
		super.setMaintenanceIntervalMicros(TimeUnit.SECONDS.toMicros(30));
	}

	/**
	 * Handle service Start calls.
	 */
	@Override
	public void handleStart(Operation start) {
		ServiceUtils.logInfo(this, "Starting service %s", getSelfLink());
			
		try {
			// Copy kubeCheckStatus scripts to tmp dir.
			String cmd = "cp -f " + 
					VcsProperties.getKubeStatusPath() + "/checkKubeStatus " +
					VcsProperties.getKubeStatusPath() + "/exp.vcs " +
					"/tmp";

			
			java.lang.Runtime rt = java.lang.Runtime.getRuntime();
			java.lang.Process p = rt.exec(cmd);
			p.waitFor();
			ServiceUtils.logInfo(this, "cmd = %s, exitStatus = %d", cmd, p.exitValue());
			
			// Give exe permissions if missing.
			cmd = "chmod +x /tmp/checkKubeStatus /tmp/exp.vcs";
			p = rt.exec(cmd);
			p.waitFor();
			ServiceUtils.logInfo(this, "cmd = %s, exitStatus = %d", cmd, p.exitValue());
		} catch (Throwable e) {
			ServiceUtils.logSevere(this, e);
		}
		
		start.complete();		
	}
	
	/**
     * Handle service periodic maintenance calls.
	 */
	@Override
    public void handlePeriodicMaintenance(Operation maintenance) {
	    ServiceUtils.logInfo(this, "Periodic maintenance triggered. %s", getSelfLink());
	    maintenance.complete();
	    CheckAndUpdateK8SClusterStatus();
	}
	
	
	public void CheckAndUpdateK8SClusterStatus() {
		ServiceUtils.logInfo(this, "Check and Update K8S Cluster health triggered. %s", getSelfLink());
		
		try {
			Operation op = VcsXenonRestClient.getVcsRestClient().get(
					ServiceUriPaths.CLOUDSTORE_ROOT + "/clusters");
			ServiceDocumentQueryResult result = op.getBody(ServiceDocumentQueryResult.class);
			
			if (result.documentLinks.isEmpty()) {	
				return;
			}
			
			Map<String, Operation> opMap = VcsXenonRestClient.getVcsRestClient().get(result.documentLinks, 8);
			for (Map.Entry<String, Operation> entry : opMap.entrySet()) {
				op = entry.getValue();
				ClusterService.State cluster = op.getBody(ClusterService.State.class);
				String masterIp = cluster.extendedProperties.get("master_ip");
				ServiceUtils.logInfo(this, "%s: MasterIP = %s, numberSlaves = %d", entry.getKey(), masterIp, cluster.slaveCount);
				
				String cmd = "/tmp/checkKubeStatus " + masterIp + " " +  cluster.slaveCount;
				
				java.lang.Runtime rt = java.lang.Runtime.getRuntime();
				java.lang.Process p = rt.exec(cmd);
				p.waitFor();
				
				ServiceUtils.logInfo(this, "cmd = %s, exitStatus = %d", cmd, p.exitValue());
				
				ClusterService.State patchState = new ClusterService.State();
				patchState.clusterhealth = ClusterHealth.GREEN;
				if (p.exitValue() != 0) {
					patchState.clusterhealth = ClusterHealth.RED;
				}
				
				if (patchState.clusterhealth != cluster.clusterhealth) {
					ServiceUtils.logInfo(this, "Existing health state is not maching with the current state.");
					
					sendRequest(
			              HostUtils.getCloudStoreHelper(this)
			                  .createPatch(entry.getKey())
			                  .setBody(patchState)
			                  .setCompletion(
			                      (Operation operation, Throwable throwable) -> {
			                        if (null != throwable) {
			                          // Ignore the failure. Otherwise if we fail the maintenance task we may end up
			                          // in a dead loop.
			                          ServiceUtils.logSevere(this, "Failed to patch cluster state to ERROR: %s", throwable.toString());
			                        }
			                      }
			                  ));
				} else {
					ServiceUtils.logInfo(this, "Skipping health check patch");
				}
			}
		} catch (Throwable e) {
			ServiceUtils.logSevere(this, e);
		}
	}
	  
	  
	@NoMigrationDuringUpgrade
	@NoMigrationDuringDeployment
	public static class State extends ServiceDocument {

	}
}
