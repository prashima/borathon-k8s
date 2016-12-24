package com.vmware.photon.controller.api.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.FutureCallback;
import com.vmware.photon.controller.api.model.Task;
import com.vmware.photon.controller.api.model.VmCreateSpec;
import com.vmware.photon.controller.api.model.Task.Entity;
import com.vmware.photon.controller.common.utils.VcsProperties;

public class VchClient {

	public static enum NodeType {
		  KubernetesEtcd,
		  KubernetesMaster,
		  KubernetesSlave
	}

	private static final Logger logger = LoggerFactory.getLogger(VchClient.class);
	private final String IP_ADDRESS_KEY = "\"IPAddress";
	private final String IPADDRESS_PATTERN =
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
	private final Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);

	private String engineArgument = null;
	private static volatile VchClient instance = null;
	
	public static synchronized VchClient getVchClient() {
		if (instance == null) {
			instance = new VchClient();
		}
		return instance;
	}

	public VchClient() {
		engineArgument = getEngineArgument();
	}

	public void pullImage(String imageName) {
		try {

			Process p = Runtime.getRuntime().exec(
					"/usr/local/bin/docker " + engineArgument + " --tls  pull "
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
					"/usr/local/bin/docker " + engineArgument
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

	public void craeteContainer(VmCreateSpec vmCreateSpec, FutureCallback<Task> callback) {
		String nodeType = vmCreateSpec.getNodeType();
		Exception e = null;

		// TODO populate appropriate image id/name.
		if (NodeType.KubernetesEtcd.name().equals(nodeType)) {
			createContainer(vmCreateSpec.getName(), NodeType.KubernetesEtcd.name());
		} else if (NodeType.KubernetesMaster.name().equals(nodeType)) {
			createContainer(vmCreateSpec.getName(), NodeType.KubernetesMaster.name());
		} else if (NodeType.KubernetesSlave.name().equals(nodeType)) {
			createContainer(vmCreateSpec.getName(), NodeType.KubernetesSlave.name());
		} else {
			e = new IllegalArgumentException("No valid nodeType available for VmCreateSpec.");
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
		logger.info("Successfully created container with name {} in project {}", vmCreateSpec.getName());
		callback.onSuccess(vmCreateTask);
	}

	public void createContainer(String containerName, String imageName) {
		try {
			// if (!isImageAvailable(imageName)) {
			// pullImage(imageName);
			// }

			Process p = Runtime.getRuntime().exec(
					"/usr/local/bin/docker " + engineArgument
							+ " --tls run --name " + containerName + " "
							+ imageName,
					new String[] { "DOCKER_API_VERSION=1.23" }, null);
			p.waitFor();
			System.out.println("Process docker run exited with status "
					+ p.exitValue());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private String getEngineArgument() {
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

	public String getContainerIp(String containerName) {
		if (containerName == null || containerName.isEmpty()) {
			throw new IllegalArgumentException("No valid containerName passed in input.");
		}

		String ipAddress = null;
		try {

			Process p = Runtime.getRuntime().exec(
					"/usr/local/bin/docker " + engineArgument
							+ " --tls inspect " + containerName,
					new String[] { "DOCKER_API_VERSION=1.23" }, null);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String output = reader.readLine();

			while (output != null) {
				System.out.println(output);
				if (output.indexOf(IP_ADDRESS_KEY) != -1) {
					
					Matcher matcher = pattern.matcher(output);
					if (matcher.find()) {
						ipAddress = matcher.group();
					}

				}

				output = reader.readLine();
			}

			p.waitFor();
			System.out.println("Process dokcer pull exited with status "
					+ p.exitValue());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return ipAddress;
	}
}
