package com.vmware.vsphere.client.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum VcClientProperties {
	INSTANCE;

	private final Logger logger = LoggerFactory
			.getLogger(VcClientProperties.class);
	private static final String PROPERTIES_FILE_NAME = "vsphere-client-properties";
	private String url;
	private String username;
	private String password;
	private String datacenterName;
	private String datastoreName;
	private String hostName;
	private String diskPath;
	private String localFilePath;
	private String remoteFilePath;
	private boolean sslTrustAll;

	private VcClientProperties() {
		String configFile = System.getProperty(PROPERTIES_FILE_NAME);
		Properties properties = new Properties();
		Reader reader = null;

		try {
			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource(configFile).getFile());
			reader = new FileReader(file);
			properties.load(reader);

			url = properties.getProperty("url");
			username = properties.getProperty("username");
			password = properties.getProperty("password");
			datacenterName = properties.getProperty("datacentername");
			datastoreName = properties.getProperty("datastorename");
			hostName = properties.getProperty("hostname");
			diskPath = properties.getProperty("diskpath");
			localFilePath = properties.getProperty("localfilepath");
			remoteFilePath = properties.getProperty("remotefilepath");
			sslTrustAll = Boolean.parseBoolean(properties
					.getProperty("ssl.trustAll.enabled"));
		} catch (IOException e) {
			logger.error(
					"Error occurred while loading vsphere-client properties.",
					e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e2) {
					logger.error(
							"Error occurred while trying to close vsphere-client properties file.",
							e2);
				}
			}
		}
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getDatacenterName() {
		return datacenterName;
	}

	public String getDatastoreName() {
		return datastoreName;
	}

	public String getHostName() {
		return hostName;
	}

	public String getDiskPath() {
		return diskPath;
	}

	public String getLocalFilePath() {
		return localFilePath;
	}

	public String getRemoteFilePath() {
		return remoteFilePath;
	}

	public boolean isSslTrustAll() {
		return sslTrustAll;
	}
}
