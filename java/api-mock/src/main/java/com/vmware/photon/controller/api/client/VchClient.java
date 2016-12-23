package com.vmware.photon.controller.api.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.vmware.photon.controller.common.utils.VcsProperties;

public class VchClient {

	private String engineArgument = null;

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
}
