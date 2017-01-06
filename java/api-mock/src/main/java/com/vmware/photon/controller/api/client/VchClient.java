package com.vmware.photon.controller.api.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.FutureCallback;
import com.vmware.photon.controller.api.model.NetworkConnection;
import com.vmware.photon.controller.api.model.Task;
import com.vmware.photon.controller.api.model.VmCreateSpec;
import com.vmware.photon.controller.api.model.VmNetworks;
import com.vmware.photon.controller.api.model.Task.Entity;
import com.vmware.photon.controller.common.utils.VcsProperties;
import com.vmware.vsphere.client.CommandExecutor;
import com.vmware.vsphere.client.commands.VmNetwork.Network;

public class VchClient {

	public static enum NodeType {
		KubernetesEtcd(VcsProperties.getK8sImage()),
		KubernetesMaster(VcsProperties.getK8sImage()),
		KubernetesSlave(VcsProperties.getK8sImage());

		private String imageName;
		private NodeType(String imageName) {
			this.imageName = imageName;
		}
	
		public String getImageName() {
			return imageName;
		}
	}

	private static final Logger logger = LoggerFactory
			.getLogger(VchClient.class);
	private final int MAX_RETRY_COUNT = 60*10;
	private final String IP_ADDRESS_KEY = "\"IPAddress";
	private final String IPADDRESS_PATTERN = "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
	private final Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);

	private String engineArgument = null;
	private String dockerBinary = null;
	private static volatile VchClient instance = null;

	public static synchronized VchClient getVchClient() {
		if (instance == null) {
			instance = new VchClient();
		}
		return instance;
	}

	public VchClient() {
		engineArgument = getDockerEngineArgument();
		dockerBinary = VcsProperties.getDockerBinary();
	}

	public void pullImage(String imageName) {
		try {

			Process p = Runtime.getRuntime().exec(
					dockerBinary + " " + engineArgument + " --tls  pull "
							+ imageName,
					new String[] { "DOCKER_API_VERSION=1.23" }, null);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String output = reader.readLine();
			while (output != null) {
				System.out.println(reader.readLine());
				output = reader.readLine();
			}
			p.waitFor();
			System.out.println("Process dokcer pull exited with status "
					+ p.exitValue());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public boolean isImageAvailable(String imageName) {
		boolean imageFound = false;
		try {

			Process p = Runtime.getRuntime().exec(
					dockerBinary + " " + engineArgument
							+ " --tls images ",
					new String[] { "DOCKER_API_VERSION=1.23" }, null);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String output = reader.readLine();

			while (output != null) {
				System.out.println(output);
				if (output.indexOf(imageName) != -1) {
					System.out.println("Found Image " + imageName);
					imageFound = true;
					break;
				}

				output = reader.readLine();
			}
			p.waitFor();
			System.out.println("Process dokcer pull exited with status "
					+ p.exitValue());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return imageFound;
	}

	public void craeteContainer(VmCreateSpec vmCreateSpec,
			FutureCallback<Task> callback) {
		Exception e = null;

		try {
		createContainer(vmCreateSpec);
		} catch (Exception ex) {
			e = ex;
		}

		Task vmCreateTask = new Task();

		if (e != null) {
			callback.onFailure(e);
			return;
		}

		vmCreateTask.setId(vmCreateSpec.getName());
		vmCreateTask.setState("COMPLETED");
		Entity vmEntity = new Entity();
		vmEntity.setId(vmCreateSpec.getName());
		vmCreateTask.setEntity(vmEntity);
		logger.info(
				"Successfully created container with name {} in project {}",
				vmCreateSpec.getName());
		callback.onSuccess(vmCreateTask);
	}

	public synchronized void createContainer(VmCreateSpec vmCreateSpec) {
		NodeType nodeType = NodeType.valueOf(vmCreateSpec.getNodeType());
		String imageName = nodeType.getImageName();
		String containerName = vmCreateSpec.getName();
		String ipEtcd = vmCreateSpec.getIpEtcd();
		String ipMaster = vmCreateSpec.getIpMaster();

		try {

			StringBuilder sb = new StringBuilder();
			sb.append(dockerBinary);
			sb.append(" ");
			sb.append(engineArgument);
			sb.append(" --tls ");
			sb.append(" run ");
		    sb.append(" -dit ");
		    if (NodeType.KubernetesEtcd.equals(nodeType)) {
		    	sb.append(getIPAddressArgument(ipEtcd));
		    } else if (NodeType.KubernetesMaster.equals(nodeType)) {
		    	sb.append(getIPAddressArgument(ipMaster));
		    }
		    sb.append(" -e ETCD_IP=");
		    sb.append(ipEtcd);
		    sb.append(" -e MASTER_IP=");
		    sb.append(ipMaster);
		    sb.append(" -e NODETYPE=");
		    if (NodeType.KubernetesEtcd.equals(nodeType)) {
		    	sb.append("etcd");
		    } else if (NodeType.KubernetesMaster.equals(nodeType)) {
		    	sb.append("master");
		    } else if (NodeType.KubernetesSlave.equals(nodeType)) {
		    	sb.append("worker");
		    }
			sb.append(" --name ");
			sb.append(containerName);
			sb.append(" ");
			sb.append(imageName);
			sb.append(" bash");

			System.out.println("Executing: " + sb.toString());
			Process p = Runtime.getRuntime().exec(sb.toString(),
					new String[] { "DOCKER_API_VERSION=1.23" }, null);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					String command = sb.toString();
					Process pAlive = p;
					NodeType node = nodeType;
					try {
						System.out.println("Waiting for process:  " + pAlive.toString() + "  " + command);
						pAlive.waitFor();
						System.out.println("Process docker run exited with status "
								+ pAlive.exitValue());

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();

			// Sleep for some time to give container some time to come up, before
			// proceeding with next steps, eg: getting IP address for it.
			//Thread.sleep(2000);
		} catch (IOException  e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get docker engine argument to be passed by client. Currently only the
	 * host:port argument is specified, if available.
	 * 
	 * @return Additional argument string, if applicable. Else empty string.
	 */
	private String getDockerEngineArgument() {
		String dockerEngineIp = VcsProperties.getDockerEngineIp();
		if (dockerEngineIp == null || dockerEngineIp.isEmpty()) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		builder.append("-H ");
		builder.append(dockerEngineIp);
		builder.append(":");
		builder.append(VcsProperties.getDockerEnginePort());

		return builder.toString();
	}

	private String getIPAddressArgument(String ip) {
		if (ip == null || ip.isEmpty()) {
			return "";
		}
		return "--ip " + ip;
	}

	public void getNetworks(String containerName, FutureCallback<Task> futureCallback) {
		logger.info("Getting network details for {}", containerName);
		String ip = null;
		int retryCount = 1;
		while (retryCount <= MAX_RETRY_COUNT) {
			ip = getContainerIp(containerName);
			if (ip != null && !ip.isEmpty()) {
				break;
			}
			try {
				Thread.sleep(1000);// Sleep for 1 second before retrying.
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			retryCount++;
		}

		Task networkTask = new Task();
		String id = UUID.randomUUID().toString();
		networkTask.setId(id);
		Entity nodeEntity = new Entity();
		nodeEntity.setId(containerName);
		networkTask.setEntity(nodeEntity);

		if (ip != null && !ip.isEmpty()) {
			networkTask.setState("FINISHED");
			VmNetworks vmNetwork = new VmNetworks();
			Set<NetworkConnection> networkConnections = new HashSet<>();
			NetworkConnection netConn = new NetworkConnection(""); // No need to specify mac for container.
			netConn.setIpAddress(ip);
			networkConnections.add(netConn);
			vmNetwork.setNetworkConnections(networkConnections);
			networkTask.setResourceProperties(vmNetwork);
			futureCallback.onSuccess(networkTask);
			logger.info("Successfully got network details for {}", containerName);
		} else {
			networkTask.setState("FAILED");
			futureCallback.onFailure(new IllegalStateException("Failed to find any IP for " + containerName));
			logger.error("Failed to get network details for {}", containerName);
		}
		
	}

	/**
	 * Get IP for container with given name.
	 * 
	 * @param containerName
	 *            name of the container.
	 * @return IP of the given container.
	 */
	public String getContainerIp(String containerName) {
		if (containerName == null || containerName.isEmpty()) {
			throw new IllegalArgumentException(
					"No valid containerName passed in input.");
		}

		String ipAddress = null;
		try {

			Process p = Runtime.getRuntime().exec(
					dockerBinary + " " + engineArgument
							+ " --tls inspect " + containerName,
					new String[] { "DOCKER_API_VERSION=1.23" }, null);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String output = reader.readLine();

			while (output != null) {
				//System.out.println(output);
				if (output.indexOf(IP_ADDRESS_KEY) != -1) {

					Matcher matcher = pattern.matcher(output);
					if (matcher.find()) {
						ipAddress = matcher.group();
					}

				}

				output = reader.readLine();
			}

			p.waitFor();

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return ipAddress;
	}
}
