package com.vmware.vcs.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OtherMain {

	public static void main(String[] args) {
		String ipAddress = null;
		String IPADDRESS_PATTERN =
				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
		Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
		try {

			Process p = Runtime.getRuntime().exec(
					"/usr/local/bin/docker -H 10.161.31.59:2376 "
							+ " --tls inspect ubuntu1",
					new String[] { "DOCKER_API_VERSION=1.23" }, null);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String output = reader.readLine();

			while (output != null) {
				//output = output.replace("\"", "");
				System.out.println(output);
				if (output.indexOf("\"IPAddress") != -1) {
					
					Matcher matcher = pattern.matcher(output);
					if (matcher.find()) {
						ipAddress = matcher.group();
					}

				}

				output = reader.readLine();
			}
			p.waitFor();
			System.out.println("IP address " + ipAddress);
			System.out.println("Process dokcer pull exited with status "
					+ p.exitValue());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
